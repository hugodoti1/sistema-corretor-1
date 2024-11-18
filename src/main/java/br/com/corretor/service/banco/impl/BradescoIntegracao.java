package br.com.corretor.service.banco.impl;

import br.com.corretor.model.ContaBancaria;
import br.com.corretor.model.TransacaoBancaria;
import br.com.corretor.service.banco.IntegracaoBancariaBase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BradescoIntegracao implements IntegracaoBancariaBase {

    private final RestTemplate restTemplate;

    @Value("${banco.bradesco.base-url}")
    private String baseUrl;

    @Value("${banco.bradesco.client-id}")
    private String clientId;

    @Value("${banco.bradesco.client-secret}")
    private String clientSecret;

    @Override
    public String obterNovoToken(ContaBancaria conta) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("scope", "extrato.read saldo.read");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + "/auth/oauth/v2/token",
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

        String url = String.format("%s/v2/contas/%s-%s/saldo",
            baseUrl, conta.getAgencia(), conta.getConta());

        ResponseEntity<Map> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            Map.class
        );

        Map<String, Object> saldoInfo = (Map<String, Object>) response.getBody();
        return new BigDecimal(saldoInfo.get("saldo").toString());
    }

    @Override
    public List<TransacaoBancaria> obterTransacoes(ContaBancaria conta, LocalDateTime dataInicio, LocalDateTime dataFim) {
        HttpHeaders headers = criarHeadersAutenticados(conta);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String url = String.format("%s/v2/contas/%s-%s/movimentacao?dataInicio=%s&dataFim=%s",
            baseUrl,
            conta.getAgencia(),
            conta.getConta(),
            formatarData(dataInicio),
            formatarData(dataFim)
        );

        ResponseEntity<List<Map>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            List.class
        );

        return response.getBody().stream()
            .map(this::mapearTransacao)
            .toList();
    }

    @Override
    public void registrarWebhook(ContaBancaria conta, String webhookUrl) {
        HttpHeaders headers = criarHeadersAutenticados(conta);
        Map<String, Object> body = Map.of(
            "webhookUrl", webhookUrl,
            "eventos", List.of("TRANSACAO", "SALDO")
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.exchange(
            baseUrl + "/v2/webhooks",
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
                baseUrl + "/v2/token/validar",
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
        transacao.setData(LocalDateTime.parse(transacaoMap.get("dataMovimentacao").toString()));
        transacao.setDescricao(transacaoMap.get("descricao").toString());
        transacao.setValor(new BigDecimal(transacaoMap.get("valor").toString()));
        transacao.setTipo(transacaoMap.get("tipoLancamento").toString());
        transacao.setDocumento(transacaoMap.get("documento").toString());
        return transacao;
    }
}
