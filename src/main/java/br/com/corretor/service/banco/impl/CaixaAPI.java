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
import java.security.cert.CertificateException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaixaAPI implements IntegracaoBancariaBase {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final BancoValidator validator;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Value("${banco.caixa.base-url}")
    private String baseUrl;

    @Value("${banco.caixa.client-id}")
    private String clientId;

    @Value("${banco.caixa.client-secret}")
    private String clientSecret;

    @Value("${banco.caixa.certificado}")
    private Resource certificado;

    @Value("${banco.caixa.certificado-senha}")
    private String certificadoSenha;

    private KeyStore keyStore;

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(certificado.getInputStream(), certificadoSenha.toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            log.error("Erro ao carregar certificado da Caixa: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.CAIXA,
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
            
            // A Caixa usa Basic Auth com client_id:client_secret em Base64
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);

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
            log.error("Erro de autenticação na Caixa: {}", e.getMessage());
            throw new BancoAutenticacaoException(
                TipoBanco.CAIXA,
                "Credenciais inválidas",
                "AUTH_001",
                "Verifique o client_id e client_secret"
            );
        } catch (Exception e) {
            log.error("Erro ao obter token da Caixa: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.CAIXA,
                "Erro ao obter token de acesso",
                e
            );
        }
    }

    @Override
    public Double consultarSaldo(String conta) {
        validator.validarConta(conta, TipoBanco.CAIXA);

        try {
            String token = obterToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/contas/v1/" + conta + "/saldo",
                HttpMethod.GET,
                request,
                String.class
            );

            JsonNode node = objectMapper.readTree(response.getBody());
            return node.get("saldo").get("disponivel").asDouble();

        } catch (HttpClientErrorException.NotFound e) {
            log.error("Conta não encontrada na Caixa: {}", conta);
            throw new BancoIntegracaoException(
                TipoBanco.CAIXA,
                "Conta não encontrada",
                "CONTA_404",
                "A conta informada não existe ou não está acessível"
            );
        } catch (Exception e) {
            log.error("Erro ao consultar saldo na Caixa: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.CAIXA,
                "Erro ao consultar saldo",
                e
            );
        }
    }

    @Override
    public List<TransacaoBancariaDTO> sincronizarTransacoes(String conta, LocalDate dataInicio, LocalDate dataFim) {
        validator.validarConta(conta, TipoBanco.CAIXA);
        validator.validarPeriodoConsulta(dataInicio, dataFim, TipoBanco.CAIXA);

        try {
            String token = obterToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/contas/v1/" + conta + "/extrato" +
                    "?dataInicio=" + dataInicio.format(DATE_FORMATTER) +
                    "&dataFim=" + dataFim.format(DATE_FORMATTER),
                HttpMethod.GET,
                request,
                String.class
            );

            JsonNode node = objectMapper.readTree(response.getBody());
            return objectMapper.readValue(
                node.get("lancamentos").toString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, TransacaoBancariaDTO.class)
            );

        } catch (Exception e) {
            log.error("Erro ao sincronizar transações na Caixa: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.CAIXA,
                "Erro ao sincronizar transações",
                e
            );
        }
    }

    @Override
    public boolean registrarWebhook(String conta, String webhookUrl) {
        validator.validarConta(conta, TipoBanco.CAIXA);
        validator.validarWebhookUrl(webhookUrl, TipoBanco.CAIXA);

        try {
            String token = obterToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = String.format(
                "{\"conta\":\"%s\",\"url\":\"%s\",\"eventos\":[\"TRANSACAO\",\"SALDO\"]}",
                conta,
                webhookUrl
            );

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/webhooks/v1/configuracao",
                HttpMethod.POST,
                request,
                String.class
            );

            return response.getStatusCode() == HttpStatus.CREATED;

        } catch (Exception e) {
            log.error("Erro ao registrar webhook na Caixa: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.CAIXA,
                "Erro ao registrar webhook",
                e
            );
        }
    }

    @Override
    public boolean processarWebhook(String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            String tipo = node.get("evento").asText();
            String conta = node.get("conta").asText();
            
            log.info("Processando webhook da Caixa - Tipo: {}, Conta: {}", tipo, conta);
            
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
            log.error("Erro ao processar webhook da Caixa: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.CAIXA,
                "Erro ao processar webhook",
                e
            );
        }
    }

    @Override
    public boolean verificarContaAtiva(String conta) {
        validator.validarConta(conta, TipoBanco.CAIXA);

        try {
            String token = obterToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/contas/v1/" + conta + "/status",
                HttpMethod.GET,
                request,
                String.class
            );

            JsonNode node = objectMapper.readTree(response.getBody());
            return "ATIVA".equals(node.get("situacao").asText());

        } catch (Exception e) {
            log.error("Erro ao verificar status da conta na Caixa: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.CAIXA,
                "Erro ao verificar status da conta",
                e
            );
        }
    }

    @Override
    public JsonNode obterDetalhesConta(String conta) {
        validator.validarConta(conta, TipoBanco.CAIXA);

        try {
            String token = obterToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/contas/v1/" + conta,
                HttpMethod.GET,
                request,
                String.class
            );

            return objectMapper.readTree(response.getBody());

        } catch (Exception e) {
            log.error("Erro ao obter detalhes da conta na Caixa: {}", e.getMessage());
            throw new BancoIntegracaoException(
                TipoBanco.CAIXA,
                "Erro ao obter detalhes da conta",
                e
            );
        }
    }

    @Override
    public String getCodigoBanco() {
        return "104";
    }

    @Override
    public String getNomeBanco() {
        return "Caixa Econômica Federal";
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
        String conta = node.get("conta").asText();
        Double valor = node.get("dados").get("valor").asDouble();
        String tipo = node.get("dados").get("tipoTransacao").asText();
        
        log.info("Processando transação - Conta: {}, Valor: {}, Tipo: {}", conta, valor, tipo);
        // Adicionar lógica específica para processamento de transação
    }

    private void processarWebhookSaldo(JsonNode node) {
        String conta = node.get("conta").asText();
        Double saldo = node.get("dados").get("saldo").asDouble();
        
        log.info("Atualizando saldo - Conta: {}, Novo Saldo: {}", conta, saldo);
        // Adicionar lógica específica para atualização de saldo
    }
}
