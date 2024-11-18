package br.com.corretor.service;

import br.com.corretor.dto.VendaDTO;
import br.com.corretor.exception.ResourceNotFoundException;
import br.com.corretor.model.Venda;
import br.com.corretor.model.Fabrica;
import br.com.corretor.model.Cliente;
import br.com.corretor.model.Corretor;
import br.com.corretor.repository.VendaRepository;
import br.com.corretor.repository.FabricaRepository;
import br.com.corretor.repository.ClienteRepository;
import br.com.corretor.repository.CorretorRepository;
import br.com.corretor.service.ComissaoService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;
    
    @Mock
    private FabricaRepository fabricaRepository;
    
    @Mock
    private ClienteRepository clienteRepository;
    
    @Mock
    private CorretorRepository corretorRepository;
    
    @Mock
    private ComissaoService comissaoService;

    @InjectMocks
    private VendaService vendaService;

    private Venda venda;
    private VendaDTO vendaDTO;

    @BeforeEach
    void setUp() {
        venda = new Venda();
        venda.setId(1L);
        venda.setEmpresaId(1L);
        venda.setFabricaId(1L);
        venda.setClienteId(1L);
        venda.setCorretorId(1L);
        venda.setDataVenda(LocalDate.now());
        venda.setValorVenda(new BigDecimal("1000.00"));
        venda.setTipoVenda("PRONTA_ENTREGA");
        venda.setPercentualComissao(new BigDecimal("10.00"));
        venda.setValorComissao(new BigDecimal("100.00"));
        venda.setFormaPagamento("BOLETO");
        venda.setQuantidadeParcelas(1);

        vendaDTO = new VendaDTO();
        vendaDTO.setId(1L);
        vendaDTO.setEmpresaId(1L);
        vendaDTO.setFabricaId(1L);
        vendaDTO.setClienteId(1L);
        vendaDTO.setCorretorId(1L);
        vendaDTO.setDataVenda(LocalDate.now());
        vendaDTO.setValorVenda(new BigDecimal("1000.00"));
        vendaDTO.setTipoVenda("PRONTA_ENTREGA");
        vendaDTO.setPercentualComissao(new BigDecimal("10.00"));
        vendaDTO.setValorComissao(new BigDecimal("100.00"));
        vendaDTO.setFormaPagamento("BOLETO");
        vendaDTO.setQuantidadeParcelas(1);
    }

    @Test
    void buscarPorId_QuandoExiste_RetornaVenda() {
        when(vendaRepository.findById(1L)).thenReturn(Mono.just(venda));

        StepVerifier.create(vendaService.buscarPorId(1L))
                .expectNext(vendaDTO)
                .verifyComplete();
    }

    @Test
    void buscarPorId_QuandoNaoExiste_RetornaError() {
        when(vendaRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(vendaService.buscarPorId(1L))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void listarComFiltros_RetornaVendas() {
        when(vendaRepository.findByEmpresaId(1L))
                .thenReturn(Flux.just(venda));

        StepVerifier.create(vendaService.listarComFiltros(1L, 1L, 1L, null, 
                null, null, LocalDate.now(), LocalDate.now(), 
                new BigDecimal("100.00"), new BigDecimal("2000.00"), null))
                .expectNext(vendaDTO)
                .verifyComplete();
    }

    @Test
    void criar_QuandoNovaVenda_RetornaVendaSalva() {
        Fabrica fabrica = new Fabrica();
        fabrica.setId(1L);
        fabrica.setPercentualComissaoPorTipoVenda("PRONTA_ENTREGA", new BigDecimal("10.00"));

        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Corretor corretor = new Corretor();
        corretor.setId(1L);

        when(fabricaRepository.findById(1L)).thenReturn(Mono.just(fabrica));
        when(clienteRepository.findById(1L)).thenReturn(Mono.just(cliente));
        when(corretorRepository.findById(1L)).thenReturn(Mono.just(corretor));
        when(vendaRepository.save(any(Venda.class))).thenReturn(Mono.just(venda));
        when(comissaoService.gerarComissao(any(Venda.class))).thenReturn(Mono.empty());

        StepVerifier.create(vendaService.criar(vendaDTO))
                .expectNext(vendaDTO)
                .verifyComplete();
    }

    @Test
    void atualizar_QuandoVendaExiste_RetornaVendaAtualizada() {
        Fabrica fabrica = new Fabrica();
        fabrica.setId(1L);
        fabrica.setPercentualComissaoPorTipoVenda("PRONTA_ENTREGA", new BigDecimal("10.00"));

        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Corretor corretor = new Corretor();
        corretor.setId(1L);

        when(vendaRepository.findById(1L)).thenReturn(Mono.just(venda));
        when(fabricaRepository.findById(1L)).thenReturn(Mono.just(fabrica));
        when(clienteRepository.findById(1L)).thenReturn(Mono.just(cliente));
        when(corretorRepository.findById(1L)).thenReturn(Mono.just(corretor));
        when(vendaRepository.save(any(Venda.class))).thenReturn(Mono.just(venda));
        when(comissaoService.atualizarComissao(any(Venda.class))).thenReturn(Mono.empty());

        StepVerifier.create(vendaService.atualizar(1L, vendaDTO))
                .expectNext(vendaDTO)
                .verifyComplete();
    }

    @Test
    void atualizar_QuandoVendaNaoExiste_RetornaError() {
        when(vendaRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(vendaService.atualizar(1L, vendaDTO))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void excluir_QuandoVendaExiste_RetornaVazio() {
        when(vendaRepository.findById(1L)).thenReturn(Mono.just(venda));
        when(vendaRepository.delete(any(Venda.class))).thenReturn(Mono.empty());
        when(comissaoService.excluirComissao(any(Venda.class))).thenReturn(Mono.empty());

        StepVerifier.create(vendaService.excluir(1L))
                .verifyComplete();
    }

    @Test
    void excluir_QuandoVendaNaoExiste_RetornaError() {
        when(vendaRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(vendaService.excluir(1L))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}
