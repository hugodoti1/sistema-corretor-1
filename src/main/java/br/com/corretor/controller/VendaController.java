package br.com.corretor.controller;

import br.com.corretor.dto.VendaDTO;
import br.com.corretor.model.Venda;
import br.com.corretor.service.VendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/vendas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VendaController {

	@Autowired
	private VendaService vendaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<VendaDTO> criar(@Valid @RequestBody VendaDTO vendaDTO) {
        return vendaService.criar(vendaDTO);
    }

    @PutMapping("/{id}")
    public Mono<VendaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody VendaDTO vendaDTO) {
        return vendaService.atualizar(id, vendaDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> excluir(@PathVariable Long id) {
        return vendaService.excluir(id);
    }

    @GetMapping("/{id}")
    public Mono<VendaDTO> buscarPorId(@PathVariable Long id) {
        return vendaService.buscarPorId(id);
    }

    @GetMapping
    public Flux<VendaDTO> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long fabricaId,
            @RequestParam(required = false) String cnpj,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) BigDecimal valorMinimo,
            @RequestParam(required = false) BigDecimal valorMaximo,
            @RequestParam(required = false) String numeroNotaFiscal) {
        return vendaService.listarComFiltros(empresaId, clienteId, fabricaId, cnpj, 
                cidade, estado, dataInicio, dataFim, valorMinimo, valorMaximo, numeroNotaFiscal);
    }

    @GetMapping("/cliente/{clienteId}")
    public Flux<VendaDTO> buscarPorCliente(@PathVariable Long clienteId, @RequestParam Long empresaId) {
        return vendaService.buscarPorCliente(empresaId, clienteId);
    }

    @GetMapping("/fabrica/{fabricaId}")
    public Flux<VendaDTO> buscarPorFabrica(@PathVariable Long fabricaId, @RequestParam Long empresaId) {
        return vendaService.buscarPorFabrica(empresaId, fabricaId);
    }

    @GetMapping("/corretor/{corretorId}")
    public Flux<VendaDTO> buscarPorCorretor(@PathVariable Long corretorId, @RequestParam Long empresaId) {
        return vendaService.buscarPorCorretor(empresaId, corretorId);
    }

    @GetMapping("/tipo/{tipoVenda}")
    public Flux<VendaDTO> buscarPorTipoVenda(@PathVariable String tipoVenda, @RequestParam Long empresaId) {
        return vendaService.buscarPorTipoVenda(empresaId, tipoVenda);
    }

    @GetMapping("/evento/{evento}")
    public Flux<VendaDTO> buscarPorEvento(@PathVariable String evento, @RequestParam Long empresaId) {
        return vendaService.buscarPorEvento(empresaId, evento);
    }

    @GetMapping("/forma-pagamento/{formaPagamento}")
    public Flux<VendaDTO> buscarPorFormaPagamento(@PathVariable String formaPagamento, @RequestParam Long empresaId) {
        return vendaService.buscarPorFormaPagamento(empresaId, formaPagamento);
    }

    @GetMapping("/faturadas")
    public Flux<VendaDTO> buscarFaturadas(@RequestParam Long empresaId) {
        return vendaService.buscarFaturadas(empresaId);
    }

    @GetMapping("/nao-faturadas")
    public Flux<VendaDTO> buscarNaoFaturadas(@RequestParam Long empresaId) {
        return vendaService.buscarNaoFaturadas(empresaId);
    }
}
