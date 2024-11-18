package br.com.corretor.controller;

import br.com.corretor.dto.CorretorDTO;
import br.com.corretor.model.Corretor;
import br.com.corretor.service.CorretorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/corretores")
@RequiredArgsConstructor
public class CorretorController {
    
	@Autowired
	private CorretorService corretorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Corretor> create(@Valid @RequestBody CorretorDTO corretorDTO) {
        return corretorService.save(corretorDTO);
    }

    @PutMapping("/{id}")
    public Mono<Corretor> update(@PathVariable Long id, @Valid @RequestBody CorretorDTO corretorDTO) {
        corretorDTO.setId(id);
        return corretorService.save(corretorDTO);
    }

    @GetMapping("/{id}")
    public Mono<Corretor> findById(@PathVariable Long id) {
        return corretorService.findById(id);
    }

    @GetMapping("/empresa/{empresaId}")
    public Flux<Corretor> findByEmpresaId(@PathVariable Long empresaId) {
        return corretorService.findByEmpresaId(empresaId);
    }

    @GetMapping("/empresa/{empresaId}/ativos")
    public Flux<Corretor> findAtivosByEmpresaId(@PathVariable Long empresaId) {
        return corretorService.findAtivosByEmpresaId(empresaId);
    }

    @GetMapping("/empresa/{empresaId}/ativos-nao-bloqueados")
    public Flux<Corretor> findAtivosNaoBloqueadosByEmpresaId(@PathVariable Long empresaId) {
        return corretorService.findAtivosNaoBloqueadosByEmpresaId(empresaId);
    }

    @GetMapping("/empresa/{empresaId}/search/nome")
    public Flux<Corretor> searchByNome(
            @PathVariable Long empresaId,
            @RequestParam String nome) {
        return corretorService.searchByNome(nome, empresaId);
    }

    @GetMapping("/empresa/{empresaId}/cidade/{cidade}")
    public Flux<Corretor> findByCidade(
            @PathVariable Long empresaId,
            @PathVariable String cidade) {
        return corretorService.findByCidade(cidade, empresaId);
    }

    @GetMapping("/empresa/{empresaId}/estado/{estado}")
    public Flux<Corretor> findByEstado(
            @PathVariable Long empresaId,
            @PathVariable String estado) {
        return corretorService.findByEstado(estado, empresaId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return corretorService.delete(id);
    }

    @PostMapping("/{id}/bloquear")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> bloquear(
            @PathVariable Long id,
            @RequestParam String motivo) {
        return corretorService.bloquear(id, motivo);
    }

    @PostMapping("/{id}/desbloquear")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> desbloquear(@PathVariable Long id) {
        return corretorService.desbloquear(id);
    }

    @GetMapping("/empresa/{empresaId}/registro-vencido")
    public Flux<Corretor> findByRegistroVencido(@PathVariable Long empresaId) {
        return corretorService.findByRegistroVencido(empresaId);
    }

    @GetMapping("/empresa/{empresaId}/registro-a-vencer")
    public Flux<Corretor> findByRegistroAVencer(@PathVariable Long empresaId) {
        return corretorService.findByRegistroAVencer(empresaId);
    }
}
