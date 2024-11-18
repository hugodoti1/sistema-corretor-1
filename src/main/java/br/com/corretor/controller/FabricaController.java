package br.com.corretor.controller;

import br.com.corretor.dto.FabricaDTO;
import br.com.corretor.model.Fabrica;
import br.com.corretor.service.FabricaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/fabricas")
@RequiredArgsConstructor
public class FabricaController {
    
	@Autowired
	private FabricaService fabricaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Fabrica> create(@Valid @RequestBody FabricaDTO fabricaDTO) {
        return fabricaService.save(fabricaDTO);
    }

    @PutMapping("/{id}")
    public Mono<Fabrica> update(@PathVariable Long id, @Valid @RequestBody FabricaDTO fabricaDTO) {
        fabricaDTO.setId(id);
        return fabricaService.save(fabricaDTO);
    }

    @GetMapping("/{id}")
    public Mono<Fabrica> findById(@PathVariable Long id) {
        return fabricaService.findById(id);
    }

    @GetMapping("/empresa/{empresaId}")
    public Flux<Fabrica> findByEmpresaId(@PathVariable Long empresaId) {
        return fabricaService.findByEmpresaId(empresaId);
    }

    @GetMapping("/empresa/{empresaId}/ativas")
    public Flux<Fabrica> findAtivasByEmpresaId(@PathVariable Long empresaId) {
        return fabricaService.findAtivasByEmpresaId(empresaId);
    }

    @GetMapping("/empresa/{empresaId}/search/razao-social")
    public Flux<Fabrica> searchByRazaoSocial(
            @PathVariable Long empresaId,
            @RequestParam String razaoSocial) {
        return fabricaService.searchByRazaoSocial(razaoSocial, empresaId);
    }

    @GetMapping("/empresa/{empresaId}/search/nome-fantasia")
    public Flux<Fabrica> searchByNomeFantasia(
            @PathVariable Long empresaId,
            @RequestParam String nomeFantasia) {
        return fabricaService.searchByNomeFantasia(nomeFantasia, empresaId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return fabricaService.delete(id);
    }
}

