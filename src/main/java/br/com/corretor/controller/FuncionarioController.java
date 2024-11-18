package br.com.corretor.controller;

import br.com.corretor.dto.FuncionarioDTO;
import br.com.corretor.model.Funcionario;
import br.com.corretor.service.FuncionarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/funcionarios")
@RequiredArgsConstructor
public class FuncionarioController {
    
	@Autowired
	private FuncionarioService funcionarioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Funcionario> create(@Valid @RequestBody FuncionarioDTO funcionarioDTO) {
        return funcionarioService.save(funcionarioDTO);
    }

    @PutMapping("/{id}")
    public Mono<Funcionario> update(@PathVariable Long id, @Valid @RequestBody FuncionarioDTO funcionarioDTO) {
        funcionarioDTO.setId(id);
        return funcionarioService.save(funcionarioDTO);
    }

    @GetMapping("/{id}")
    public Mono<Funcionario> findById(@PathVariable Long id) {
        return funcionarioService.findById(id);
    }

    @GetMapping("/empresa/{empresaId}")
    public Flux<Funcionario> findByEmpresaId(@PathVariable Long empresaId) {
        return funcionarioService.findByEmpresaId(empresaId);
    }

    @GetMapping("/empresa/{empresaId}/ativos")
    public Flux<Funcionario> findAtivosbyEmpresaId(@PathVariable Long empresaId) {
        return funcionarioService.findAtivosbyEmpresaId(empresaId);
    }

    @GetMapping("/empresa/{empresaId}/atuais")
    public Flux<Funcionario> findAtuaisbyEmpresaId(@PathVariable Long empresaId) {
        return funcionarioService.findAtuaisbyEmpresaId(empresaId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return funcionarioService.delete(id);
    }
}

