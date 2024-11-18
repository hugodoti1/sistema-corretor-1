package br.com.corretor.service.banco.impl;

import br.com.corretor.dto.banco.TransacaoBancariaDTO;
import br.com.corretor.enums.TipoAutenticacao;
import br.com.corretor.enums.TipoBanco;
import br.com.corretor.enums.RecursoBancario;
import br.com.corretor.exception.banco.BancoAutenticacaoException;
import br.com.corretor.exception.banco.BancoIntegracaoException;
import br.com.corretor.service.banco.IntegracaoBancariaBase;
import br.com.corretor.validator.BancoValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BancoDoBrasilIntegracao implements IntegracaoBancariaBase {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final BancoValidator validator;

    @Value("${banco.bb.base-url}")
    private String baseUrl;

    @Value("${banco.bb.client-id}")
    private String clientId;

    @Value("${banco.bb.client-secret}")
    private String clientSecret;

    @Override
    public String obterToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(clientId, clientSecret);

            HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials", headers);
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/oauth/token",
                HttpMethod.POST,
                request,
                String.class
            );

            JsonNode node = objectMapper.readTree(response.getBody());
            return node.get("access_token").asText();

        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("Erro de autenticação no Banco do Brasil: {}", e.getMessage());
            throw new BancoAutenticacaoException(
                TipoBanco.BANCO_DO_BRASIL,
                "Credenciais inválidas",
                "AUTH_001",
                "Verifique o client_id e client_secret"
            );
        } catch (Exception e) {
            log.error("Erro ao obter token do Banco do Brasil: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.BANCO_DO_BRASIL,
                "Erro ao obter token de acesso",
                e
            );
        }
    }

    @Override
    public Double consultarSaldo(String conta) {
        validator.validarConta(conta, TipoBanco.BANCO_DO_BRASIL);

        try {
            String token = obterToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/contas/" + conta + "/saldo",
                HttpMethod.GET,
                request,
                String.class
            );

            JsonNode node = objectMapper.readTree(response.getBody());
            return node.get("saldo").asDouble();

        } catch (HttpClientErrorException.NotFound e) {
            log.error("Conta não encontrada no Banco do Brasil: {}", conta);
            throw new BancoIntegracaoException(
                TipoBanco.BANCO_DO_BRASIL,
                "Conta não encontrada",
                "CONTA_404",
                "A conta informada não existe ou não está acessível"
            );
        } catch (BancoAutenticacaoException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao consultar saldo no Banco do Brasil: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.BANCO_DO_BRASIL,
                "Erro ao consultar saldo",
                e
            );
        }
    }

    @Override
    public List<TransacaoBancariaDTO> sincronizarTransacoes(String conta, LocalDate dataInicio, LocalDate dataFim) {
        validator.validarConta(conta, TipoBanco.BANCO_DO_BRASIL);
        validator.validarPeriodoConsulta(dataInicio, dataFim, TipoBanco.BANCO_DO_BRASIL);

        try {
            String token = obterToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/contas/" + conta + "/extrato" +
                    "?dataInicio=" + dataInicio +
                    "&dataFim=" + dataFim,
                HttpMethod.GET,
                request,
                String.class
            );

            JsonNode node = objectMapper.readTree(response.getBody());
            return objectMapper.readValue(
                node.get("transacoes").toString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, TransacaoBancariaDTO.class)
            );

        } catch (HttpClientErrorException.NotFound e) {
            log.error("Conta não encontrada no Banco do Brasil: {}", conta);
            throw new BancoIntegracaoException(
                TipoBanco.BANCO_DO_BRASIL,
                "Conta não encontrada",
                "CONTA_404",
                "A conta informada não existe ou não está acessível"
            );
        } catch (BancoAutenticacaoException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao sincronizar transações no Banco do Brasil: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.BANCO_DO_BRASIL,
                "Erro ao sincronizar transações",
                e
            );
        }
    }

    @Override
    public boolean registrarWebhook(String conta, String webhookUrl) {
        validator.validarConta(conta, TipoBanco.BANCO_DO_BRASIL);
        validator.validarWebhookUrl(webhookUrl, TipoBanco.BANCO_DO_BRASIL);

        try {
            String token = obterToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = String.format(
                "{\"conta\":\"%s\",\"webhookUrl\":\"%s\"}",
                conta,
                webhookUrl
            );

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/webhooks",
                HttpMethod.POST,
                request,
                String.class
            );

            return response.getStatusCode() == HttpStatus.CREATED;

        } catch (HttpClientErrorException.NotFound e) {
            log.error("Conta não encontrada no Banco do Brasil: {}", conta);
            throw new BancoIntegracaoException(
                TipoBanco.BANCO_DO_BRASIL,
                "Conta não encontrada",
                "CONTA_404",
                "A conta informada não existe ou não está acessível"
            );
        } catch (BancoAutenticacaoException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao registrar webhook no Banco do Brasil: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.BANCO_DO_BRASIL,
                "Erro ao registrar webhook",
                e
            );
        }
    }

    @Override
    public boolean processarWebhook(String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            String tipo = node.get("tipo").asText();
            String conta = node.get("conta").asText();
            
            log.info("Processando webhook do Banco do Brasil - Tipo: {}, Conta: {}", tipo, conta);
            
            switch (tipo) {
                case "TRANSACAO":
                    processarWebhookTransacao(node);
                    break;
                case "SALDO":
                    processarWebhookSaldo(node);
                    break;
                default:
                    log.warn("Tipo de webhook não reconhecido: {}", tipo);
                    return false;
            }
            
            return true;
        } catch (Exception e) {
            log.error("Erro ao processar webhook do Banco do Brasil: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.BANCO_DO_BRASIL,
                "Erro ao processar webhook",
                e
            );
        }
    }

    @Override
    public boolean verificarContaAtiva(String conta) {
        validator.validarConta(conta, TipoBanco.BANCO_DO_BRASIL);

        try {
            String token = obterToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/contas/" + conta + "/status",
                HttpMethod.GET,
                request,
                String.class
            );

            JsonNode node = objectMapper.readTree(response.getBody());
            return "ATIVA".equals(node.get("status").asText());

        } catch (Exception e) {
            log.error("Erro ao verificar status da conta no Banco do Brasil: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.BANCO_DO_BRASIL,
                "Erro ao verificar status da conta",
                e
            );
        }
    }

    @Override
    public JsonNode obterDetalhesConta(String conta) {
        validator.validarConta(conta, TipoBanco.BANCO_DO_BRASIL);

        try {
            String token = obterToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/contas/" + conta,
                HttpMethod.GET,
                request,
                String.class
            );

            return objectMapper.readTree(response.getBody());

        } catch (Exception e) {
            log.error("Erro ao obter detalhes da conta no Banco do Brasil: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.BANCO_DO_BRASIL,
                "Erro ao obter detalhes da conta",
                e
            );
        }
    }

    @Override
    public String getCodigoBanco() {
        return "001";
    }

    @Override
    public String getNomeBanco() {
        return "Banco do Brasil";
    }

    @Override
    public TipoAutenticacao getTipoAutenticacao() {
        return TipoAutenticacao.OAUTH2;
    }

    @Override
    public List<RecursoBancario> getRecursosSuportados() {
        return Arrays.asList(
            RecursoBancario.SALDO,
            RecursoBancario.EXTRATO,
            RecursoBancario.WEBHOOK,
            RecursoBancario.TRANSFERENCIA,
            RecursoBancario.PIX,
            RecursoBancario.BOLETO
        );
    }

    private void processarWebhookTransacao(JsonNode node) {
        // Implementação do processamento de webhook de transação
        String conta = node.get("conta").asText();
        Double valor = node.get("valor").asDouble();
        String tipo = node.get("tipoTransacao").asText();
        
        log.info("Processando transação - Conta: {}, Valor: {}, Tipo: {}", conta, valor, tipo);
        // Adicionar lógica específica para processamento de transação
    }

    private void processarWebhookSaldo(JsonNode node) {
        // Implementação do processamento de webhook de saldo
        String conta = node.get("conta").asText();
        Double saldo = node.get("saldo").asDouble();
        
        log.info("Atualizando saldo - Conta: {}, Novo Saldo: {}", conta, saldo);
        // Adicionar lógica específica para atualização de saldo
    }
}
