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
public class ItauIntegracao implements IntegracaoBancariaBase {

    private final RestTemplate restTemplate;

    @Value("${banco.itau.base-url}")
    private String baseUrl;

    @Value("${banco.itau.client-id}")
    private String clientId;

    @Value("${banco.itau.client-secret}")
    private String clientSecret;

    @Value("${banco.itau.certificado-path}")
    private String certificadoPath;

    @Override
    public String obterNovoToken(ContaBancaria conta) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, String> body = Map.of(
            "client_id", clientId,
            "client_secret", clientSecret,
            "grant_type", "client_credentials",
            "scope", "readonly"
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + "/api/oauth/token",
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

        String url = String.format("%s/api/v2/conta-corrente/%s/saldo",
            baseUrl, formatarContaItau(conta));

        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            Map.class
        );

        Map<String, Object> saldoInfo = (Map<String, Object>) response.getBody();
        return new BigDecimal(saldoInfo.get("saldo_disponivel").toString());
    }

    @Override
    public List<TransacaoBancaria> obterTransacoes(ContaBancaria conta, LocalDateTime dataInicio, LocalDateTime dataFim) {
        HttpHeaders headers = criarHeadersAutenticados(conta);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String url = String.format("%s/api/v2/conta-corrente/%s/extrato?data_inicial=%s&data_final=%s",
            baseUrl,
            formatarContaItau(conta),
            formatarData(dataInicio),
            formatarData(dataFim)
        );

        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            Map.class
        );

        List<Map<String, Object>> lancamentos = (List<Map<String, Object>>) response.getBody().get("lancamentos");
        return lancamentos.stream()
            .map(this::mapearTransacao)
            .toList();
    }

    @Override
    public void registrarWebhook(ContaBancaria conta, String webhookUrl) {
        HttpHeaders headers = criarHeadersAutenticados(conta);
        Map<String, Object> body = Map.of(
            "url", webhookUrl,
            "eventos", List.of("TRANSACAO", "SALDO"),
            "conta", formatarContaItau(conta)
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.exchange(
            baseUrl + "/api/v2/webhooks",
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
                baseUrl + "/api/v2/oauth/token/introspect",
                HttpMethod.GET,
                request,
                Map.class
            );

            return Boolean.TRUE.equals(response.getBody().get("active"));
        } catch (Exception e) {
            return false;
        }
    }

    private HttpHeaders criarHeadersAutenticados(ContaBancaria conta) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(conta.getTokenAcesso());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-itau-correlationID", java.util.UUID.randomUUID().toString());
        return headers;
    }

    private String formatarContaItau(ContaBancaria conta) {
        return String.format("%s%s", conta.getAgencia(), conta.getConta());
    }

    private String formatarData(LocalDateTime data) {
        return data.format(DateTimeFormatter.ISO_DATE);
    }

    private TransacaoBancaria mapearTransacao(Map<String, Object> lancamento) {
        TransacaoBancaria transacao = new TransacaoBancaria();
        transacao.setIdTransacaoBanco(lancamento.get("id_lancamento").toString());
        transacao.setData(LocalDateTime.parse(lancamento.get("data_lancamento").toString()));
        transacao.setDescricao(lancamento.get("descricao").toString());
        transacao.setValor(new BigDecimal(lancamento.get("valor").toString()));
        transacao.setTipo(lancamento.get("tipo_lancamento").toString());
        transacao.setDocumento(lancamento.get("documento").toString());
        return transacao;
    }
}
