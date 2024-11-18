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
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterAPI implements IntegracaoBancariaBase {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final BancoValidator validator;

    @Value("${banco.inter.base-url}")
    private String baseUrl;

    @Value("${banco.inter.client-id}")
    private String clientId;

    @Value("${banco.inter.client-secret}")
    private String clientSecret;

    @Value("${banco.inter.certificado}")
    private Resource certificado;

    @Value("${banco.inter.certificado-senha}")
    private String certificadoSenha;

    private KeyStore keyStore;

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(certificado.getInputStream(), certificadoSenha.toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            log.error("Erro ao carregar certificado do Banco Inter: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.INTER,
                "Erro ao carregar certificado",
                e
            );
        }
    }

    @Override
    public String obterToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(clientId, clientSecret);

            HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials&scope=extrato.read", headers);
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/oauth/v2/token",
                HttpMethod.POST,
                request,
                String.class
            );

            JsonNode node = objectMapper.readTree(response.getBody());
            return node.get("access_token").asText();

        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("Erro de autenticação no Banco Inter: {}", e.getMessage());
            throw new BancoAutenticacaoException(
                TipoBanco.INTER,
                "Credenciais inválidas",
                "AUTH_001",
                "Verifique o client_id e client_secret"
            );
        } catch (Exception e) {
            log.error("Erro ao obter token do Banco Inter: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.INTER,
                "Erro ao obter token de acesso",
                e
            );
        }
    }

    @Override
    public Double consultarSaldo(String conta) {
        validator.validarConta(conta, TipoBanco.INTER);

        try {
            String token = obterToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/banking/v2/saldo",
                HttpMethod.GET,
                request,
                String.class
            );

            JsonNode node = objectMapper.readTree(response.getBody());
            return node.get("saldo").asDouble();

        } catch (HttpClientErrorException.NotFound e) {
            log.error("Conta não encontrada no Banco Inter: {}", conta);
            throw new BancoIntegracaoException(
                TipoBanco.INTER,
                "Conta não encontrada",
                "CONTA_404",
                "A conta informada não existe ou não está acessível"
            );
        } catch (Exception e) {
            log.error("Erro ao consultar saldo no Banco Inter: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.INTER,
                "Erro ao consultar saldo",
                e
            );
        }
    }

    @Override
    public List<TransacaoBancariaDTO> sincronizarTransacoes(String conta, LocalDate dataInicio, LocalDate dataFim) {
        validator.validarConta(conta, TipoBanco.INTER);
        validator.validarPeriodoConsulta(dataInicio, dataFim, TipoBanco.INTER);

        try {
            String token = obterToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/banking/v2/extrato" +
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

        } catch (Exception e) {
            log.error("Erro ao sincronizar transações no Banco Inter: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.INTER,
                "Erro ao sincronizar transações",
                e
            );
        }
    }

    @Override
    public boolean registrarWebhook(String conta, String webhookUrl) {
        validator.validarConta(conta, TipoBanco.INTER);
        validator.validarWebhookUrl(webhookUrl, TipoBanco.INTER);

        try {
            String token = obterToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = String.format(
                "{\"webhookUrl\":\"%s\",\"tipoWebhook\":\"BANKING\"}",
                webhookUrl
            );

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/webhooks/v2/config",
                HttpMethod.PUT,
                request,
                String.class
            );

            return response.getStatusCode() == HttpStatus.OK;

        } catch (Exception e) {
            log.error("Erro ao registrar webhook no Banco Inter: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.INTER,
                "Erro ao registrar webhook",
                e
            );
        }
    }

    @Override
    public boolean processarWebhook(String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            String tipo = node.get("tipoEvento").asText();
            
            log.info("Processando webhook do Banco Inter - Tipo: {}", tipo);
            
            switch (tipo) {
                case "TRANSACAO_REALIZADA":
                    processarWebhookTransacao(node);
                    break;
                case "SALDO_ATUALIZADO":
                    processarWebhookSaldo(node);
                    break;
                default:
                    log.warn("Tipo de webhook não reconhecido: {}", tipo);
                    return false;
            }
            
            return true;
        } catch (Exception e) {
            log.error("Erro ao processar webhook do Banco Inter: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.INTER,
                "Erro ao processar webhook",
                e
            );
        }
    }

    @Override
    public boolean verificarContaAtiva(String conta) {
        validator.validarConta(conta, TipoBanco.INTER);

        try {
            String token = obterToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/banking/v2/status",
                HttpMethod.GET,
                request,
                String.class
            );

            JsonNode node = objectMapper.readTree(response.getBody());
            return "ATIVA".equals(node.get("situacao").asText());

        } catch (Exception e) {
            log.error("Erro ao verificar status da conta no Banco Inter: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.INTER,
                "Erro ao verificar status da conta",
                e
            );
        }
    }

    @Override
    public JsonNode obterDetalhesConta(String conta) {
        validator.validarConta(conta, TipoBanco.INTER);

        try {
            String token = obterToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/banking/v2/conta",
                HttpMethod.GET,
                request,
                String.class
            );

            return objectMapper.readTree(response.getBody());

        } catch (Exception e) {
            log.error("Erro ao obter detalhes da conta no Banco Inter: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.INTER,
                "Erro ao obter detalhes da conta",
                e
            );
        }
    }

    @Override
    public String getCodigoBanco() {
        return "077";
    }

    @Override
    public String getNomeBanco() {
        return "Banco Inter";
    }

    @Override
    public TipoAutenticacao getTipoAutenticacao() {
        return TipoAutenticacao.CERTIFICADO_DIGITAL;
    }

    @Override
    public List<RecursoBancario> getRecursosSuportados() {
        return Arrays.asList(
            RecursoBancario.SALDO,
            RecursoBancario.EXTRATO,
            RecursoBancario.WEBHOOK,
            RecursoBancario.PAGAMENTO,
            RecursoBancario.TRANSFERENCIA,
            RecursoBancario.PIX,
            RecursoBancario.BOLETO
        );
    }

    private void processarWebhookTransacao(JsonNode node) {
        String tipo = node.get("dadosEvento").get("tipoTransacao").asText();
        Double valor = node.get("dadosEvento").get("valor").asDouble();
        
        log.info("Processando transação - Tipo: {}, Valor: {}", tipo, valor);
        // Adicionar lógica específica para processamento de transação
    }

    private void processarWebhookSaldo(JsonNode node) {
        Double saldo = node.get("dadosEvento").get("saldo").asDouble();
        
        log.info("Atualizando saldo - Novo Saldo: {}", saldo);
        // Adicionar lógica específica para atualização de saldo
    }
}
