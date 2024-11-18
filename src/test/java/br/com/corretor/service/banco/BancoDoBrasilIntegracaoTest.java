package br.com.corretor.service.banco;

import br.com.corretor.config.BancoIntegracaoTestConfig;
import br.com.corretor.dto.banco.TransacaoBancariaDTO;
import br.com.corretor.service.banco.impl.BancoDoBrasilIntegracao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Import(BancoIntegracaoTestConfig.class)
public class BancoDoBrasilIntegracaoTest {

    @Autowired
    private BancoDoBrasilIntegracao bancoDoBrasilIntegracao;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        // Mock para autenticação
        when(restTemplate.exchange(
            eq("/oauth/token"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(new ResponseEntity<>("{\"access_token\":\"test-token\"}", HttpStatus.OK));
    }

    @Test
    void testObterToken() {
        String token = bancoDoBrasilIntegracao.obterToken();
        assertNotNull(token);
        assertEquals("test-token", token);
    }

    @Test
    void testConsultarSaldo() {
        // Mock para consulta de saldo
        when(restTemplate.exchange(
            eq("/contas/saldo"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(new ResponseEntity<>("{\"saldo\":1000.00}", HttpStatus.OK));

        Double saldo = bancoDoBrasilIntegracao.consultarSaldo("12345-6");
        assertNotNull(saldo);
        assertEquals(1000.00, saldo);
    }

    @Test
    void testSincronizarTransacoes() {
        // Mock para sincronização de transações
        String mockResponse = """
            {
                "transacoes": [
                    {
                        "id": "123",
                        "data": "2023-01-01",
                        "valor": 100.00,
                        "tipo": "CREDITO",
                        "descricao": "Depósito"
                    }
                ]
            }
            """;

        when(restTemplate.exchange(
            eq("/extrato"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        List<TransacaoBancariaDTO> transacoes = bancoDoBrasilIntegracao.sincronizarTransacoes(
            "12345-6",
            LocalDate.now().minusDays(30),
            LocalDate.now()
        );

        assertNotNull(transacoes);
        assertFalse(transacoes.isEmpty());
        assertEquals(1, transacoes.size());
        assertEquals(100.00, transacoes.get(0).getValor());
    }

    @Test
    void testRegistrarWebhook() {
        // Mock para registro de webhook
        when(restTemplate.exchange(
            eq("/webhooks"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(new ResponseEntity<>("{\"id\":\"webhook-123\"}", HttpStatus.CREATED));

        boolean resultado = bancoDoBrasilIntegracao.registrarWebhook("12345-6", "https://exemplo.com/webhook");
        assertTrue(resultado);
    }
}
