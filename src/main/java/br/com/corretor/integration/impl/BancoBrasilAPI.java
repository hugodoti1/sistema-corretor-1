package br.com.corretor.integration.impl;

import br.com.corretor.config.BancoConfig;
import br.com.corretor.dto.SaldoBancarioDTO;
import br.com.corretor.exception.banco.BBException;
import br.com.corretor.integration.BancoIntegrationAPI;
import br.com.corretor.model.ContaBancaria;
import br.com.corretor.model.TransacaoBancaria;
import br.com.corretor.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BancoBrasilAPI implements BancoIntegrationAPI {

    private final RestTemplate restTemplate;
    private final BancoConfig bancoConfig;
    private static final String BANCO = "BANCO_DO_BRASIL";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public SaldoBancarioDTO obterSaldo(ContaBancaria conta) {
        return LogUtil.logOperacaoComRetorno(log, "OBTER_SALDO", BANCO, conta.getAgencia() + "/" + conta.getConta(),
            () -> {
                String url = bancoConfig.getBancoBrasil().getBaseUrl() + "/saldo";
                
                HttpHeaders headers = criarHeaders(conta);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                
                try {
                    ResponseEntity<SaldoBancarioDTO> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        SaldoBancarioDTO.class
                    );
                    
                    return response.getBody();
                } catch (Exception e) {
                    throw new BBException("Erro ao consultar saldo: " + e.getMessage(), e);
                }
            });
    }

    @Override
    public List<TransacaoBancaria> buscarTransacoes(ContaBancaria conta, LocalDateTime inicio, LocalDateTime fim) {
        return LogUtil.logOperacaoComRetorno(log, "BUSCAR_TRANSACOES", BANCO, conta.getAgencia() + "/" + conta.getConta(),
            () -> {
                String url = bancoConfig.getBancoBrasil().getBaseUrl() + "/transacoes";
                
                Map<String, String> params = Map.of(
                    "dataInicio", inicio.format(DATE_FORMATTER),
                    "dataFim", fim.format(DATE_FORMATTER)
                );
                
                HttpHeaders headers = criarHeaders(conta);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                
                try {
                    ResponseEntity<List<TransacaoBancaria>> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        new org.springframework.core.ParameterizedTypeReference<>() {},
                        params
                    );
                    
                    return response.getBody();
                } catch (Exception e) {
                    throw new BBException("Erro ao buscar transações: " + e.getMessage(), e);
                }
            });
    }

    @Override
    public boolean verificarContaAtiva(ContaBancaria conta) {
        return LogUtil.logOperacaoComRetorno(log, "VERIFICAR_CONTA", BANCO, conta.getAgencia() + "/" + conta.getConta(),
            () -> {
                String url = bancoConfig.getBancoBrasil().getBaseUrl() + "/conta/status";
                
                HttpHeaders headers = criarHeaders(conta);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                
                try {
                    ResponseEntity<Map<String, Boolean>> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        new org.springframework.core.ParameterizedTypeReference<>() {}
                    );
                    
                    return response.getBody() != null && response.getBody().getOrDefault("ativa", false);
                } catch (Exception e) {
                    throw new BBException("Erro ao verificar status da conta: " + e.getMessage(), e);
                }
            });
    }

    @Override
    public ContaBancaria obterDetalhesConta(String agencia, String conta) {
        return LogUtil.logOperacaoComRetorno(log, "OBTER_DETALHES", BANCO, agencia + "/" + conta,
            () -> {
                String url = bancoConfig.getBancoBrasil().getBaseUrl() + "/conta/detalhes";
                
                Map<String, String> params = Map.of(
                    "agencia", agencia,
                    "conta", conta
                );
                
                HttpHeaders headers = criarHeaders();
                HttpEntity<?> entity = new HttpEntity<>(headers);
                
                try {
                    ResponseEntity<ContaBancaria> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        ContaBancaria.class,
                        params
                    );
                    
                    return response.getBody();
                } catch (Exception e) {
                    throw new BBException("Erro ao obter detalhes da conta: " + e.getMessage(), e);
                }
            });
    }

    @Override
    public String getNomeBanco() {
        return "Banco do Brasil";
    }

    @Override
    public String getCodigoBanco() {
        return "001";
    }

    private HttpHeaders criarHeaders(ContaBancaria conta) {
        HttpHeaders headers = criarHeaders();
        headers.set("X-BB-Account", conta.getConta());
        headers.set("X-BB-Agency", conta.getAgencia());
        return headers;
    }

    private HttpHeaders criarHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bancoConfig.getBancoBrasil().getToken());
        headers.set("X-BB-Access-Key", bancoConfig.getBancoBrasil().getAccessKey());
        return headers;
    }
}
