                package br.com.corretor.controller;

import br.com.corretor.dto.ComissaoDTO;
import br.com.corretor.dto.ComissaoResumoDTO;
import br.com.corretor.service.ComissaoService;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebFluxTest(ComissaoController.class)
class ComissaoControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ComissaoService comissaoService;

    @Test
    void buscarVencidas_RetornaComissoes() {
        ComissaoDTO comissao = new ComissaoDTO();
        comissao.setId(1L);
        comissao.setEmpresaId(1L);
        comissao.setStatus("PENDENTE");

        when(comissaoService.buscarVencidas(anyLong()))
            .thenReturn(Flux.just(comissao));

        webTestClient.get()
                .uri("/api/comissoes/vencidas?empresaId=1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ComissaoDTO.class)
                .hasSize(1)
                .value(list -> {
                    ComissaoDTO dto = list.get(0);
                    assertEquals(1L, dto.getId());
                    assertEquals(1L, dto.getEmpresaId());
                    assertEquals("PENDENTE", dto.getStatus());
                });
    }

    @Test
    void buscarAVencer_RetornaComissoes() {
        ComissaoDTO comissao = new ComissaoDTO();
        comissao.setId(1L);
        comissao.setEmpresaId(1L);
        comissao.setStatus("PENDENTE");

        when(comissaoService.buscarAVencer(anyLong()))
            .thenReturn(Flux.just(comissao));

        webTestClient.get()
                .uri("/api/comissoes/a-vencer?empresaId=1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ComissaoDTO.class)
                .hasSize(1)
                .value(list -> {
                    ComissaoDTO dto = list.get(0);
                    assertEquals(1L, dto.getId());
                    assertEquals(1L, dto.getEmpresaId());
                    assertEquals("PENDENTE", dto.getStatus());
                });
    }

    @Test
    void obterResumo_RetornaResumoCompleto() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());

        ComissaoResumoDTO resumoEsperado = new ComissaoResumoDTO();
        resumoEsperado.setTotalComissoes(2L);
        resumoEsperado.setValorTotalComissoes(new BigDecimal("200.00"));
        resumoEsperado.setValorTotalPago(new BigDecimal("100.00"));
        resumoEsperado.setComissoesPagas(1L);
        resumoEsperado.setComissoesPendentes(1L);

        when(comissaoService.obterResumo(anyLong(), any(), any()))
            .thenReturn(Mono.just(resumoEsperado));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/comissoes/resumo")
                        .queryParam("empresaId", "1")
                        .queryParam("dataInicio", inicioMes)
                        .queryParam("dataFim", fimMes)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ComissaoResumoDTO.class)
                .value(resumo -> {
                    assertEquals(2L, resumo.getTotalComissoes());
                    assertEquals(new BigDecimal("200.00"), resumo.getValorTotalComissoes());
                    assertEquals(new BigDecimal("100.00"), resumo.getValorTotalPago());
                    assertEquals(1L, resumo.getComissoesPagas());
                    assertEquals(1L, resumo.getComissoesPendentes());
                });
    }
}
