package br.com.corretor.controller;

import br.com.corretor.dto.ComissaoDTO;
import br.com.corretor.dto.ComissaoPagamentoDTO;
import br.com.corretor.dto.ComissaoResumoDTO;
import br.com.corretor.service.ComissaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RestController
@RequestMapping("/api/comissoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComissaoController {

	@Autowired
	private ComissaoService comissaoService;

    @GetMapping("/{id}")
    public Mono<ComissaoDTO> buscarPorId(@PathVariable Long id) {
        return comissaoService.buscarPorId(id);
    }

    @GetMapping
    public Flux<ComissaoDTO> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long corretorId,
            @RequestParam(required = false) Long fabricaId,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) String status) {
        return comissaoService.listarComFiltros(empresaId, corretorId, fabricaId, 
                dataInicio, dataFim, status);
    }

    @GetMapping("/vencidas")
    public Flux<ComissaoDTO> buscarVencidas(@RequestParam Long empresaId) {
        return comissaoService.buscarVencidas(empresaId);
    }

    @GetMapping("/a-vencer")
    public Flux<ComissaoDTO> buscarAVencer(@RequestParam Long empresaId) {
        return comissaoService.buscarAVencer(empresaId);
    }

    @GetMapping("/corretor/{corretorId}")
    public Flux<ComissaoDTO> buscarPorCorretor(@PathVariable Long corretorId) {
        return comissaoService.buscarPorCorretor(corretorId);
    }

    @GetMapping("/fabrica/{fabricaId}")
    public Flux<ComissaoDTO> buscarPorFabrica(@PathVariable Long fabricaId) {
        return comissaoService.buscarPorFabrica(fabricaId);
    }

    @GetMapping("/periodo-venda")
    public Flux<ComissaoDTO> buscarPorPeriodoVenda(
            @RequestParam Long empresaId,
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        return comissaoService.buscarPorPeriodoVenda(empresaId, dataInicio, dataFim);
    }

    @GetMapping("/periodo-vencimento")
    public Flux<ComissaoDTO> buscarPorPeriodoVencimento(
            @RequestParam Long empresaId,
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        return comissaoService.buscarPorPeriodoVencimento(empresaId, dataInicio, dataFim);
    }

    @GetMapping("/periodo-pagamento")
    public Flux<ComissaoDTO> buscarPorPeriodoPagamento(
            @RequestParam Long empresaId,
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        return comissaoService.buscarPorPeriodoPagamento(empresaId, dataInicio, dataFim);
    }

    @PutMapping("/{id}/pagar")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ComissaoDTO> registrarPagamento(
            @PathVariable Long id,
            @Valid @RequestBody ComissaoPagamentoDTO pagamentoDTO) { 
        return comissaoService.registrarPagamento(id, pagamentoDTO);
    }

    @PutMapping("/{id}/cancelar")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ComissaoDTO> cancelar(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        return comissaoService.cancelar(id, motivo);
    }

    @GetMapping("/status/{status}")
    public Flux<ComissaoDTO> buscarPorStatus(
            @PathVariable String status,
            @RequestParam Long empresaId) {
        return comissaoService.buscarPorStatus(empresaId, status);
    }

    @GetMapping("/resumo")
    public Mono<ComissaoResumoDTO> obterResumo(
            @RequestParam Long empresaId,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim) {
        return comissaoService.obterResumo(empresaId, dataInicio, dataFim);
    }
}
