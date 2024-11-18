package br.com.corretor.service.banco;

import br.com.corretor.config.BancoIntegracaoTestConfig;
import br.com.corretor.dto.banco.TransacaoBancariaDTO;
import br.com.corretor.enums.TipoBanco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Import(BancoIntegracaoTestConfig.class)
public class BancoIntegracaoServiceTest {

    @Autowired
    private Map<TipoBanco, IntegracaoBancariaBase> integracoesBancarias;

    @Autowired
    private BancoIntegracaoService bancoIntegracaoService;

    @BeforeEach
    void setUp() {
        // Configurar mocks para cada tipo de banco
        for (Map.Entry<TipoBanco, IntegracaoBancariaBase> entry : integracoesBancarias.entrySet()) {
            IntegracaoBancariaBase integracao = entry.getValue();
            
            // Mock obterToken
            when(integracao.obterToken()).thenReturn("test-token-" + entry.getKey());
            
            // Mock consultarSaldo
            when(integracao.consultarSaldo(any())).thenReturn(1000.00);
            
            // Mock sincronizarTransacoes
            when(integracao.sincronizarTransacoes(
                any(), any(LocalDate.class), any(LocalDate.class)
            )).thenReturn(Arrays.asList(
                TransacaoBancariaDTO.builder()
                    .id("123")
                    .data(LocalDate.now())
                    .valor(100.00)
                    .descricao("Transação teste")
                    .build()
            ));
            
            // Mock registrarWebhook
            when(integracao.registrarWebhook(any(), any())).thenReturn(true);
        }
    }

    @Test
    void testConsultarSaldoMultiplosBancos() {
        Map<TipoBanco, Double> saldos = bancoIntegracaoService.consultarSaldoMultiplosBancos(
            Arrays.asList(TipoBanco.BANCO_DO_BRASIL, TipoBanco.BRADESCO),
            "12345-6"
        );

        assertNotNull(saldos);
        assertEquals(2, saldos.size());
        assertTrue(saldos.containsKey(TipoBanco.BANCO_DO_BRASIL));
        assertTrue(saldos.containsKey(TipoBanco.BRADESCO));
        assertEquals(1000.00, saldos.get(TipoBanco.BANCO_DO_BRASIL));
        assertEquals(1000.00, saldos.get(TipoBanco.BRADESCO));
    }

    @Test
    void testSincronizarTransacoesMultiplosBancos() {
        Map<TipoBanco, List<TransacaoBancariaDTO>> transacoes = bancoIntegracaoService.sincronizarTransacoesMultiplosBancos(
            Arrays.asList(TipoBanco.BANCO_DO_BRASIL, TipoBanco.BRADESCO),
            "12345-6",
            LocalDate.now().minusDays(30),
            LocalDate.now()
        );

        assertNotNull(transacoes);
        assertEquals(2, transacoes.size());
        assertTrue(transacoes.containsKey(TipoBanco.BANCO_DO_BRASIL));
        assertTrue(transacoes.containsKey(TipoBanco.BRADESCO));
        assertFalse(transacoes.get(TipoBanco.BANCO_DO_BRASIL).isEmpty());
        assertFalse(transacoes.get(TipoBanco.BRADESCO).isEmpty());
    }

    @Test
    void testRegistrarWebhookMultiplosBancos() {
        Map<TipoBanco, Boolean> resultados = bancoIntegracaoService.registrarWebhookMultiplosBancos(
            Arrays.asList(TipoBanco.BANCO_DO_BRASIL, TipoBanco.BRADESCO),
            "12345-6",
            "https://exemplo.com/webhook"
        );

        assertNotNull(resultados);
        assertEquals(2, resultados.size());
        assertTrue(resultados.containsKey(TipoBanco.BANCO_DO_BRASIL));
        assertTrue(resultados.containsKey(TipoBanco.BRADESCO));
        assertTrue(resultados.get(TipoBanco.BANCO_DO_BRASIL));
        assertTrue(resultados.get(TipoBanco.BRADESCO));
    }

    @Test
    void testIntegracaoBancariaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> {
            bancoIntegracaoService.consultarSaldoMultiplosBancos(
                Arrays.asList(TipoBanco.BANCO_DO_BRASIL, null),
                "12345-6"
            );
        });
    }
}
