package br.com.corretor.service;

import br.com.corretor.model.Banco;
import br.com.corretor.model.Conciliacao;
import br.com.corretor.model.Transacao;
import br.com.corretor.repository.BancoRepository;
import br.com.corretor.repository.ConciliacaoRepository;
import br.com.corretor.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConciliacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private ConciliacaoRepository conciliacaoRepository;

    @Mock
    private BancoRepository bancoRepository;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private ConciliacaoService conciliacaoService;

    @Captor
    private ArgumentCaptor<Conciliacao> conciliacaoCaptor;

    @Captor
    private ArgumentCaptor<Transacao> transacaoCaptor;

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Banco banco;
    private Conciliacao conciliacao;
    private Transacao transacao1;
    private Transacao transacao2;

    @BeforeEach
    void setUp() {
        dataInicio = LocalDateTime.now().minusDays(30);
        dataFim = LocalDateTime.now();

        banco = new Banco();
        banco.setId(1L);
        banco.setEmpresaId(1L);
        banco.setAgencia("1234");
        banco.setConta("56789");

        conciliacao = new Conciliacao();
        conciliacao.setId(1L);
        conciliacao.setEmpresaId(1L);
        conciliacao.setBancoId(1L);
        conciliacao.setDataInicio(dataInicio);
        conciliacao.setDataFim(dataFim);
        conciliacao.setConcluida(false);

        transacao1 = new Transacao();
        transacao1.setId(1L);
        transacao1.setEmpresaId(1L);
        transacao1.setBancoId(1L);
        transacao1.setValor(new BigDecimal("100.00"));
        transacao1.setTipo("CREDITO");
        transacao1.setDataTransacao(LocalDateTime.now().minusDays(15));
        transacao1.setIdTransacaoBanco("TX123");

        transacao2 = new Transacao();
        transacao2.setId(2L);
        transacao2.setEmpresaId(1L);
        transacao2.setBancoId(1L);
        transacao2.setValor(new BigDecimal("100.00"));
        transacao2.setTipo("CREDITO");
        transacao2.setDataTransacao(LocalDateTime.now().minusDays(15));
        transacao2.setIdTransacaoBanco("TX123");
    }

    @Test
    void iniciarConciliacao_DeveIniciarComSucesso() {
        // Arrange
        when(bancoRepository.findById(1L)).thenReturn(Optional.of(banco));
        when(conciliacaoRepository.save(any(Conciliacao.class))).thenReturn(conciliacao);

        // Act
        Conciliacao resultado = conciliacaoService.iniciarConciliacao(1L, 1L, dataInicio, dataFim);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getEmpresaId());
        assertEquals(1L, resultado.getBancoId());
        assertEquals(dataInicio, resultado.getDataInicio());
        assertEquals(dataFim, resultado.getDataFim());
        assertFalse(resultado.isConcluida());

        verify(auditoriaService).registrarAcao(eq("CONCILIACAO"), eq("INICIO"), any());
    }

    @Test
    void processarConciliacao_DeveProcessarTransacoesDuplicadas() {
        // Arrange
        when(conciliacaoRepository.findById(1L)).thenReturn(Optional.of(conciliacao));
        when(transacaoRepository.findByEmpresaIdAndBancoIdAndDataTransacaoBetween(
                anyLong(), anyLong(), any(), any()))
                .thenReturn(Arrays.asList(transacao1, transacao2));

        // Act
        conciliacaoService.processarConciliacao(1L);

        // Assert
        verify(transacaoRepository, times(2)).save(transacaoCaptor.capture());
        List<Transacao> transacoesSalvas = transacaoCaptor.getAllValues();

        assertTrue(transacoesSalvas.get(0).isConciliada());
        assertTrue(transacoesSalvas.get(1).isConciliada());
        assertNotNull(transacoesSalvas.get(0).getDataConciliacao());
        assertNotNull(transacoesSalvas.get(1).getDataConciliacao());

        verify(conciliacaoRepository).save(conciliacaoCaptor.capture());
        Conciliacao conciliacaoSalva = conciliacaoCaptor.getValue();
        
        assertTrue(conciliacaoSalva.isConcluida());
        assertEquals(2, conciliacaoSalva.getTotalTransacoes());
        assertEquals(2, conciliacaoSalva.getTransacoesConciliadas());
        assertEquals(0, conciliacaoSalva.getTransacoesPendentes());
    }

    @Test
    void processarConciliacao_DeveProcessarTransacoesSimilares() {
        // Arrange
        transacao2.setIdTransacaoBanco("TX456"); // ID diferente
        transacao2.setDataTransacao(transacao1.getDataTransacao().plusHours(12)); // Mesma data +- 12h

        when(conciliacaoRepository.findById(1L)).thenReturn(Optional.of(conciliacao));
        when(transacaoRepository.findByEmpresaIdAndBancoIdAndDataTransacaoBetween(
                anyLong(), anyLong(), any(), any()))
                .thenReturn(Arrays.asList(transacao1, transacao2));

        // Act
        conciliacaoService.processarConciliacao(1L);

        // Assert
        verify(transacaoRepository, times(2)).save(transacaoCaptor.capture());
        List<Transacao> transacoesSalvas = transacaoCaptor.getAllValues();

        assertTrue(transacoesSalvas.get(0).isConciliada());
        assertTrue(transacoesSalvas.get(1).isConciliada());
    }

    @Test
    void calcularSaldoConciliado_DeveCalcularCorretamente() {
        // Arrange
        Transacao credito = new Transacao();
        credito.setValor(new BigDecimal("100.00"));
        credito.setTipo("CREDITO");

        Transacao debito = new Transacao();
        debito.setValor(new BigDecimal("50.00"));
        debito.setTipo("DEBITO");

        when(transacaoRepository.findByEmpresaIdAndBancoIdAndDataTransacaoBetweenAndConciliadaTrue(
                anyLong(), anyLong(), any(), any()))
                .thenReturn(Arrays.asList(credito, debito));

        // Act
        BigDecimal saldo = conciliacaoService.calcularSaldoConciliado(1L, 1L, dataInicio, dataFim);

        // Assert
        assertEquals(new BigDecimal("50.00"), saldo);
    }

    @Test
    void buscarTransacoesPendentes_DeveRetornarTransacoesPendentes() {
        // Arrange
        transacao1.setConciliada(false);
        transacao2.setConciliada(false);
        
        when(transacaoRepository.findByEmpresaIdAndBancoIdAndDataTransacaoBetweenAndConciliadaFalse(
                anyLong(), anyLong(), any(), any()))
                .thenReturn(Arrays.asList(transacao1, transacao2));

        // Act
        List<Transacao> transacoes = conciliacaoService.buscarTransacoesPendentes(1L, 1L, dataInicio, dataFim);

        // Assert
        assertEquals(2, transacoes.size());
        assertFalse(transacoes.get(0).isConciliada());
        assertFalse(transacoes.get(1).isConciliada());
    }
}
