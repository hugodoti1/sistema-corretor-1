package br.com.corretor.controller;

import br.com.corretor.dto.VendaDTO;
import br.com.corretor.service.VendaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebFluxTest(VendaController.class)
class VendaControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private VendaService vendaService;

    @Test
    void listarComFiltros_RetornaVendas() {
        VendaDTO venda = new VendaDTO();
        venda.setId(1L);
        venda.setEmpresaId(1L);
        venda.setValorVenda(new BigDecimal("1000.00"));
        venda.setDataVenda(LocalDate.now());
        venda.setTipoVenda("PRONTA_ENTREGA");
        venda.setPercentualComissao(new BigDecimal("10.00"));
        venda.setValorComissao(new BigDecimal("100.00"));
        venda.setFormaPagamento("BOLETO");
        venda.setQuantidadeParcelas(1);

        when(vendaService.listarComFiltros(
                anyLong(), any(), any(), any(), any(), any(), 
                any(), any(), any(), any(), any()))
            .thenReturn(Flux.just(venda));

        webTestClient.get()
                .uri("/api/vendas?empresaId=1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(VendaDTO.class)
                .hasSize(1)
                .value(list -> {
                    VendaDTO dto = list.get(0);
                    assertEquals(1L, dto.getId());
                    assertEquals(1L, dto.getEmpresaId());
                    assertEquals(new BigDecimal("1000.00"), dto.getValorVenda());
                    assertNotNull(dto.getDataVenda());
                    assertEquals("PRONTA_ENTREGA", dto.getTipoVenda());
                    assertEquals(new BigDecimal("10.00"), dto.getPercentualComissao());
                    assertEquals(new BigDecimal("100.00"), dto.getValorComissao());
                    assertEquals("BOLETO", dto.getFormaPagamento());
                    assertEquals(1, dto.getQuantidadeParcelas());
                });
    }

    @Test
    void buscarPorId_RetornaVenda() {
        VendaDTO venda = new VendaDTO();
        venda.setId(1L);
        venda.setEmpresaId(1L);
        venda.setValorVenda(new BigDecimal("1000.00"));
        venda.setDataVenda(LocalDate.now());
        venda.setTipoVenda("PRONTA_ENTREGA");
        venda.setPercentualComissao(new BigDecimal("10.00"));
        venda.setValorComissao(new BigDecimal("100.00"));
        venda.setFormaPagamento("BOLETO");
        venda.setQuantidadeParcelas(1);

        when(vendaService.buscarPorId(anyLong()))
            .thenReturn(Mono.just(venda));

        webTestClient.get()
                .uri("/api/vendas/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VendaDTO.class)
                .value(dto -> {
                    assertEquals(1L, dto.getId());
                    assertEquals(1L, dto.getEmpresaId());
                    assertEquals(new BigDecimal("1000.00"), dto.getValorVenda());
                    assertNotNull(dto.getDataVenda());
                    assertEquals("PRONTA_ENTREGA", dto.getTipoVenda());
                    assertEquals(new BigDecimal("10.00"), dto.getPercentualComissao());
                    assertEquals(new BigDecimal("100.00"), dto.getValorComissao());
                    assertEquals("BOLETO", dto.getFormaPagamento());
                    assertEquals(1, dto.getQuantidadeParcelas());
                });
    }

    @Test
    void criar_RetornaVendaCriada() {
        VendaDTO venda = new VendaDTO();
        venda.setEmpresaId(1L);
        venda.setValorVenda(new BigDecimal("1000.00"));
        venda.setDataVenda(LocalDate.now());
        venda.setTipoVenda("PRONTA_ENTREGA");
        venda.setPercentualComissao(new BigDecimal("10.00"));
        venda.setValorComissao(new BigDecimal("100.00"));
        venda.setFormaPagamento("BOLETO");
        venda.setQuantidadeParcelas(1);

        VendaDTO vendaSalva = new VendaDTO();
        vendaSalva.setId(1L);
        vendaSalva.setEmpresaId(1L);
        vendaSalva.setValorVenda(new BigDecimal("1000.00"));
        vendaSalva.setDataVenda(LocalDate.now());
        vendaSalva.setTipoVenda("PRONTA_ENTREGA");
        vendaSalva.setPercentualComissao(new BigDecimal("10.00"));
        vendaSalva.setValorComissao(new BigDecimal("100.00"));
        vendaSalva.setFormaPagamento("BOLETO");
        vendaSalva.setQuantidadeParcelas(1);

        when(vendaService.criar(any(VendaDTO.class)))
            .thenReturn(Mono.just(vendaSalva));

        webTestClient.post()
                .uri("/api/vendas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(venda)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(VendaDTO.class)
                .value(dto -> {
                    assertEquals(1L, dto.getId());
                    assertEquals(1L, dto.getEmpresaId());
                    assertEquals(new BigDecimal("1000.00"), dto.getValorVenda());
                    assertNotNull(dto.getDataVenda());
                    assertEquals("PRONTA_ENTREGA", dto.getTipoVenda());
                    assertEquals(new BigDecimal("10.00"), dto.getPercentualComissao());
                    assertEquals(new BigDecimal("100.00"), dto.getValorComissao());
                    assertEquals("BOLETO", dto.getFormaPagamento());
                    assertEquals(1, dto.getQuantidadeParcelas());
                });
    }

    @Test
    void atualizar_RetornaVendaAtualizada() {
        VendaDTO venda = new VendaDTO();
        venda.setEmpresaId(1L);
        venda.setValorVenda(new BigDecimal("1000.00"));
        venda.setDataVenda(LocalDate.now());
        venda.setTipoVenda("PRONTA_ENTREGA");
        venda.setPercentualComissao(new BigDecimal("10.00"));
        venda.setValorComissao(new BigDecimal("100.00"));
        venda.setFormaPagamento("BOLETO");
        venda.setQuantidadeParcelas(1);

        when(vendaService.atualizar(anyLong(), any(VendaDTO.class)))
            .thenReturn(Mono.just(venda));

        webTestClient.put()
                .uri("/api/vendas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(venda)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VendaDTO.class)
                .value(dto -> {
                    assertEquals(1L, dto.getEmpresaId());
                    assertEquals(new BigDecimal("1000.00"), dto.getValorVenda());
                    assertNotNull(dto.getDataVenda());
                    assertEquals("PRONTA_ENTREGA", dto.getTipoVenda());
                    assertEquals(new BigDecimal("10.00"), dto.getPercentualComissao());
                    assertEquals(new BigDecimal("100.00"), dto.getValorComissao());
                    assertEquals("BOLETO", dto.getFormaPagamento());
                    assertEquals(1, dto.getQuantidadeParcelas());
                });
    }

    @Test
    void excluir_RetornaNoContent() {
        when(vendaService.excluir(anyLong()))
            .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/vendas/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
