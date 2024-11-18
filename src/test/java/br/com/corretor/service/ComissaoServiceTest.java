package br.com.corretor.service;

import br.com.corretor.dto.ComissaoDTO;
import br.com.corretor.dto.ComissaoPagamentoDTO;
import br.com.corretor.dto.ComissaoResumoDTO;
import br.com.corretor.exception.BusinessException;
import br.com.corretor.exception.ResourceNotFoundException;
import br.com.corretor.model.Comissao;
import br.com.corretor.repository.ComissaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComissaoServiceTest {

    @Mock
    private ComissaoRepository comissaoRepository;

    @Mock
    private VendaService vendaService;

    @InjectMocks
    private ComissaoService comissaoService;

    private Comissao comissao;
    private ComissaoDTO comissaoDTO;
    private ComissaoPagamentoDTO pagamentoDTO;

    @BeforeEach
    void setUp() {
        comissao = new Comissao();
        comissao.setId(1L);
        comissao.setEmpresaId(1L);
        comissao.setVendaId(1L);
        comissao.setFabricaId(1L);
        comissao.setCorretorId(1L);
        comissao.setDataVenda(LocalDate.now());
        comissao.setValorVenda(new BigDecimal("1000.00"));
        comissao.setPercentualComissao(new BigDecimal("10.00"));
        comissao.setValorComissao(new BigDecimal("100.00"));
        comissao.setDataPrevisaoPagamento(LocalDate.now().plusDays(30));
        comissao.setStatus("PENDENTE");

        comissaoDTO = new ComissaoDTO();
        comissaoDTO.setId(1L);
        comissaoDTO.setEmpresaId(1L);
        comissaoDTO.setVendaId(1L);
        comissaoDTO.setFabricaId(1L);
        comissaoDTO.setCorretorId(1L);
        comissaoDTO.setDataVenda(LocalDate.now());
        comissaoDTO.setValorVenda(new BigDecimal("1000.00"));
        comissaoDTO.setPercentualComissao(new BigDecimal("10.00"));
        comissaoDTO.setValorComissao(new BigDecimal("100.00"));
        comissaoDTO.setDataPrevisaoPagamento(LocalDate.now().plusDays(30));
        comissaoDTO.setStatus("PENDENTE");

        pagamentoDTO = new ComissaoPagamentoDTO();
        pagamentoDTO.setDataPagamento(LocalDate.now());
        pagamentoDTO.setValorPago(new BigDecimal("100.00"));
        pagamentoDTO.setFormaPagamento("PIX");
    }

    @Test
    void buscarPorId_QuandoExiste_RetornaComissao() {
        when(comissaoRepository.findById(1L)).thenReturn(Mono.just(comissao));

        StepVerifier.create(comissaoService.buscarPorId(1L))
                .expectNext(comissaoDTO)
                .verifyComplete();
    }

    @Test
    void buscarPorId_QuandoNaoExiste_RetornaError() {
        when(comissaoRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(comissaoService.buscarPorId(1L))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void listarComFiltros_RetornaComissoes() {
        when(comissaoRepository.findByFiltros(any(), any(), any(), any(), any(), any()))
                .thenReturn(Flux.just(comissao));

        StepVerifier.create(comissaoService.listarComFiltros(1L, 1L, 1L, 
                LocalDate.now(), LocalDate.now(), "PENDENTE"))
                .expectNext(comissaoDTO)
                .verifyComplete();
    }

    @Test
    void registrarPagamento_QuandoComissaoPendente_RetornaComissaoPaga() {
        when(comissaoRepository.findById(1L)).thenReturn(Mono.just(comissao));
        when(comissaoRepository.save(any(Comissao.class))).thenReturn(Mono.just(comissao));

        StepVerifier.create(comissaoService.registrarPagamento(1L, pagamentoDTO))
                .expectNextMatches(dto -> "PAGO".equals(dto.getStatus()))
                .verifyComplete();
    }

    @Test
    void registrarPagamento_QuandoComissaoCancelada_RetornaError() {
        comissao.setStatus("CANCELADO");
        when(comissaoRepository.findById(1L)).thenReturn(Mono.just(comissao));

        StepVerifier.create(comissaoService.registrarPagamento(1L, pagamentoDTO))
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void registrarPagamento_QuandoValorMaior_RetornaError() {
        pagamentoDTO.setValorPago(new BigDecimal("200.00"));
        when(comissaoRepository.findById(1L)).thenReturn(Mono.just(comissao));

        StepVerifier.create(comissaoService.registrarPagamento(1L, pagamentoDTO))
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void cancelar_QuandoComissaoPendente_RetornaComissaoCancelada() {
        when(comissaoRepository.findById(1L)).thenReturn(Mono.just(comissao));
        when(comissaoRepository.save(any(Comissao.class))).thenReturn(Mono.just(comissao));

        StepVerifier.create(comissaoService.cancelar(1L, "Motivo do cancelamento"))
                .expectNextMatches(dto -> "CANCELADO".equals(dto.getStatus()))
                .verifyComplete();
    }

    @Test
    void cancelar_QuandoComissaoPaga_RetornaError() {
        comissao.setStatus("PAGO");
        when(comissaoRepository.findById(1L)).thenReturn(Mono.just(comissao));

        StepVerifier.create(comissaoService.cancelar(1L, "Motivo do cancelamento"))
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void buscarVencidas_RetornaComissoesVencidas() {
        when(comissaoRepository.findVencidas(any(), any()))
                .thenReturn(Flux.just(comissao));

        StepVerifier.create(comissaoService.buscarVencidas(1L))
                .expectNext(comissaoDTO)
                .verifyComplete();
    }

    @Test
    void buscarAVencer_RetornaComissoesAVencer() {
        when(comissaoRepository.findAVencer(any(), any()))
                .thenReturn(Flux.just(comissao));

        StepVerifier.create(comissaoService.buscarAVencer(1L))
                .expectNext(comissaoDTO)
                .verifyComplete();
    }

    @Test
    void obterResumo_RetornaResumoCompleto() {
        Comissao comissaoPaga = new Comissao();
        comissaoPaga.setStatus("PAGO");
        comissaoPaga.setValorComissao(new BigDecimal("100.00"));
        comissaoPaga.setValorPago(new BigDecimal("100.00"));

        Comissao comissaoPendente = new Comissao();
        comissaoPendente.setStatus("PENDENTE");
        comissaoPendente.setValorComissao(new BigDecimal("100.00"));

        when(comissaoRepository.findByEmpresaIdAndDataVendaBetween(any(), any(), any()))
                .thenReturn(Flux.just(comissaoPaga, comissaoPendente));

        StepVerifier.create(comissaoService.obterResumo(1L, LocalDate.now(), LocalDate.now()))
                .expectNextMatches(resumo -> 
                    resumo.getTotalComissoes() == 2L &&
                    resumo.getValorTotalComissoes().compareTo(new BigDecimal("200.00")) == 0 &&
                    resumo.getValorTotalPago().compareTo(new BigDecimal("100.00")) == 0 &&
                    resumo.getTotalPago() == 1L &&
                    resumo.getTotalPendente() == 1L
                )
                .verifyComplete();
    }
}
