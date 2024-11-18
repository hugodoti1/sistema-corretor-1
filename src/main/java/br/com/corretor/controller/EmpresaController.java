package br.com.corretor.controller;

import br.com.corretor.dto.EmpresaDTO;
import br.com.corretor.model.Empresa;
import br.com.corretor.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmpresaController {
    
	@Autowired
	private EmpresaService empresaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Empresa> criar(@Valid @RequestBody EmpresaDTO empresaDTO) {
        return empresaService.criar(empresaDTO);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Empresa>> atualizar(@PathVariable Long id, @Valid @RequestBody EmpresaDTO empresaDTO) {
        return empresaService.atualizar(id, empresaDTO)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> excluir(@PathVariable Long id) {
        return empresaService.excluir(id)
            .then(Mono.just(ResponseEntity.ok().<Void>build()))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Empresa>> buscarPorId(@PathVariable Long id) {
        return empresaService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/cnpj/{cnpj}")
    public Mono<ResponseEntity<Empresa>> buscarPorCnpj(@PathVariable String cnpj) {
        return empresaService.buscarPorCnpj(cnpj)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<Empresa> listarTodas() {
        return empresaService.listarTodas();
    }
}
