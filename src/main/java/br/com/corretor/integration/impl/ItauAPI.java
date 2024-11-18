package br.com.corretor.integration.impl;

import br.com.corretor.config.BancoConfig;
import br.com.corretor.dto.SaldoBancarioDTO;
import br.com.corretor.exception.banco.ItauException;
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
public class ItauAPI implements BancoIntegrationAPI {

    private final RestTemplate restTemplate;
    private final BancoConfig bancoConfig;
    private static final String BANCO = "ITAU";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public SaldoBancarioDTO obterSaldo(ContaBancaria conta) {
        return LogUtil.logOperacaoComRetorno(log, "OBTER_SALDO", BANCO, conta.getAgencia() + "/" + conta.getConta(),
            () -> {
                String url = bancoConfig.getItau().getBaseUrl() + "/saldo";
                
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
                    throw new ItauException("Erro ao consultar saldo", ItauException.ItauErrorCode.ITAU008, BANCO);
                }
            });
    }

    @Override
    public List<TransacaoBancaria> buscarTransacoes(ContaBancaria conta, LocalDateTime inicio, LocalDateTime fim) {
        return LogUtil.logOperacaoComRetorno(log, "BUSCAR_TRANSACOES", BANCO, conta.getAgencia() + "/" + conta.getConta(),
            () -> {
                String url = bancoConfig.getItau().getBaseUrl() + "/extrato";
                
                Map<String, String> params = Map.of(
                    "dataInicial", inicio.format(DATE_FORMATTER),
                    "dataFinal", fim.format(DATE_FORMATTER)
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
                    throw new ItauException("Erro ao buscar transações", ItauException.ItauErrorCode.ITAU008, BANCO);
                }
            });
    }

    @Override
    public boolean verificarContaAtiva(ContaBancaria conta) {
        return LogUtil.logOperacaoComRetorno(log, "VERIFICAR_CONTA", BANCO, conta.getAgencia() + "/" + conta.getConta(),
            () -> {
                String url = bancoConfig.getItau().getBaseUrl() + "/conta/status";
                
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
                    throw new ItauException("Erro ao verificar status da conta", ItauException.ItauErrorCode.ITAU008, BANCO);
                }
            });
    }

    @Override
    public ContaBancaria obterDetalhesConta(String agencia, String conta) {
        return LogUtil.logOperacaoComRetorno(log, "OBTER_DETALHES", BANCO, agencia + "/" + conta,
            () -> {
                String url = bancoConfig.getItau().getBaseUrl() + "/conta/detalhes";
                
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
                    throw new ItauException("Erro ao obter detalhes da conta", ItauException.ItauErrorCode.ITAU008, BANCO);
                }
            });
    }

    @Override
    public String getNomeBanco() {
        return "Itaú";
    }

    @Override
    public String getCodigoBanco() {
        return "341";
    }

    private HttpHeaders criarHeaders(ContaBancaria conta) {
        HttpHeaders headers = criarHeaders();
        headers.set("X-ITAU-CONTA", conta.getConta());
        headers.set("X-ITAU-AGENCIA", conta.getAgencia());
        return headers;
    }

    private HttpHeaders criarHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bancoConfig.getItau().getToken());
        headers.set("X-ITAU-APIKEY", bancoConfig.getItau().getApiKey());
        headers.set("X-ITAU-CERTIFICADO", bancoConfig.getItau().getCertificado());
        return headers;
    }
}
