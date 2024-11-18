package br.com.corretor.service.banco.impl;

import br.com.corretor.model.ContaBancaria;
import br.com.corretor.model.TransacaoBancaria;
import br.com.corretor.service.banco.IntegracaoBancariaBase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BancoInterIntegracao implements IntegracaoBancariaBase {

    private final RestTemplate restTemplate;

    @Value("${banco.inter.base-url}")
    private String baseUrl;

    @Value("${banco.inter.client-id}")
    private String clientId;

    @Value("${banco.inter.client-secret}")
    private String clientSecret;

    @Value("${banco.inter.certificado-path}")
    private String certificadoPath;

    @Override
    public String obterNovoToken(ContaBancaria conta) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("client_id", clientId);
        headers.set("client_secret", clientSecret);

        Map<String, String> body = Map.of(
            "grant_type", "client_credentials",
            "scope", "extrato.read"
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + "/oauth/v2/token",
            HttpMethod.POST,
            request,
            Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    @Override
    public BigDecimal obterSaldo(ContaBancaria conta) {
        HttpHeaders headers = criarHeadersAutenticados(conta);
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + "/banking/v2/saldo",
            HttpMethod.GET,
            request,
            Map.class
        );

        Map<String, Object> saldoInfo = (Map<String, Object>) response.getBody();
        return new BigDecimal(saldoInfo.get("disponivel").toString());
    }

    @Override
    public List<TransacaoBancaria> obterTransacoes(ContaBancaria conta, LocalDateTime dataInicio, LocalDateTime dataFim) {
        HttpHeaders headers = criarHeadersAutenticados(conta);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String url = String.format("%s/banking/v2/extrato?dataInicio=%s&dataFim=%s",
            baseUrl,
            formatarData(dataInicio),
            formatarData(dataFim)
        );

        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            Map.class
        );

        List<Map<String, Object>> transacoes = (List<Map<String, Object>>) response.getBody().get("transacoes");
        return transacoes.stream()
            .map(this::mapearTransacao)
            .toList();
    }

    @Override
    public void registrarWebhook(ContaBancaria conta, String webhookUrl) {
        HttpHeaders headers = criarHeadersAutenticados(conta);
        Map<String, Object> body = Map.of(
            "webhookUrl", webhookUrl,
            "tipoWebhook", List.of("BANKING_EXTRATO", "BANKING_SALDO")
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.exchange(
            baseUrl + "/webhooks/v2/config",
            HttpMethod.POST,
            request,
            Void.class
        );
    }

    @Override
    public boolean validarToken(ContaBancaria conta) {
        try {
            HttpHeaders headers = criarHeadersAutenticados(conta);
            HttpEntity<?> request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/oauth/v2/token/validate",
                HttpMethod.GET,
                request,
                Map.class
            );

            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }

    private HttpHeaders criarHeadersAutenticados(ContaBancaria conta) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(conta.getTokenAcesso());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String formatarData(LocalDateTime data) {
        return data.format(DateTimeFormatter.ISO_DATE);
    }

    private TransacaoBancaria mapearTransacao(Map<String, Object> transacaoMap) {
        TransacaoBancaria transacao = new TransacaoBancaria();
        transacao.setIdTransacaoBanco(transacaoMap.get("id").toString());
        transacao.setData(LocalDateTime.parse(transacaoMap.get("dataTransacao").toString()));
        transacao.setDescricao(transacaoMap.get("descricao").toString());
        transacao.setValor(new BigDecimal(transacaoMap.get("valor").toString()));
        transacao.setTipo(transacaoMap.get("tipo").toString());
        transacao.setDocumento(transacaoMap.get("idOperacao").toString());
        return transacao;
    }
}
