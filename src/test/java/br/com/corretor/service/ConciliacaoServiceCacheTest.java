package br.com.corretor.service;

import br.com.corretor.config.CacheConfig;
import br.com.corretor.model.ContaBancaria;
import br.com.corretor.model.TransacaoBancaria;
import br.com.corretor.repository.ConciliacaoRepository;
import br.com.corretor.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ConciliacaoServiceCacheTest {

    @Autowired
    private ConciliacaoService conciliacaoService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private TransacaoRepository transacaoRepository;

    @MockBean
    private ConciliacaoRepository conciliacaoRepository;

    @MockBean
    private BancoIntegracaoService bancoIntegracaoService;

    private ContaBancaria conta;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    @BeforeEach
    void setUp() {
        conta = new ContaBancaria();
        conta.setId(1L);
        conta.setConta("123456");
        
        dataInicio = LocalDateTime.now().minusDays(30);
        dataFim = LocalDateTime.now();

        // Limpa todos os caches antes de cada teste
        cacheManager.getCacheNames()
            .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

    @Test
    void deveUsarCacheParaTransacoesPendentes() {
        // Configura mock para retornar lista de transações
        when(transacaoRepository.findPendentesParaConciliacao(eq(conta.getId()), any(), any()))
            .thenReturn(Arrays.asList(new TransacaoBancaria(), new TransacaoBancaria()));

        // Primeira chamada - deve acessar o repositório
        List<TransacaoBancaria> result1 = conciliacaoService.buscarTransacoesPendentes(conta, dataInicio, dataFim);

        // Segunda chamada - deve usar o cache
        List<TransacaoBancaria> result2 = conciliacaoService.buscarTransacoesPendentes(conta, dataInicio, dataFim);

        // Verifica se o repositório foi acessado apenas uma vez
        verify(transacaoRepository, times(1)).findPendentesParaConciliacao(any(), any(), any());
    }

    @Test
    void deveLimparCacheAoIniciarConciliacao() {
        // Configura mock para retornar lista de transações
        when(transacaoRepository.findPendentesParaConciliacao(eq(conta.getId()), any(), any()))
            .thenReturn(Arrays.asList(new TransacaoBancaria(), new TransacaoBancaria()));

        // Primeira chamada - popula o cache
        conciliacaoService.buscarTransacoesPendentes(conta, dataInicio, dataFim);

        // Inicia conciliação - deve limpar o cache
        conciliacaoService.iniciarConciliacao(conta);

        // Segunda chamada - deve acessar o repositório novamente
        conciliacaoService.buscarTransacoesPendentes(conta, dataInicio, dataFim);

        // Verifica se o repositório foi acessado duas vezes
        verify(transacaoRepository, times(2)).findPendentesParaConciliacao(any(), any(), any());
    }

    @Test
    void deveUsarCacheParaSaldoConciliado() {
        // Configura mock para retornar saldo
        when(conciliacaoRepository.calcularSaldoConciliado(conta.getId()))
            .thenReturn(1000.0);

        // Primeira chamada - deve acessar o repositório
        double saldo1 = conciliacaoService.calcularSaldoConciliado(conta);

        // Segunda chamada - deve usar o cache
        double saldo2 = conciliacaoService.calcularSaldoConciliado(conta);

        // Verifica se o repositório foi acessado apenas uma vez
        verify(conciliacaoRepository, times(1)).calcularSaldoConciliado(any());
    }
}
