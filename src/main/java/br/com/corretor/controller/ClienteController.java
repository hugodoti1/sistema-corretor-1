package br.com.corretor.controller;

import br.com.corretor.dto.ClienteDTO;
import br.com.corretor.model.Cliente;
import br.com.corretor.service.ClienteService;
import jakarta.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/clientes")

public class ClienteController {
	
	@Autowired
    private ClienteService clienteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Cliente> create(@Valid @RequestBody ClienteDTO clienteDTO) {
        return clienteService.save(clienteDTO);
    }

    @PutMapping("/{id}")
    public Mono<Cliente> update(@PathVariable Long id, @Valid @RequestBody ClienteDTO clienteDTO) {
        clienteDTO.setId(id);
        return clienteService.save(clienteDTO);
    }

    @GetMapping("/{id}")
    public Mono<Cliente> findById(@PathVariable Long id) {
        return clienteService.findById(id);
    }

    @GetMapping("/empresa/{empresaId}")
    public Flux<Cliente> findByEmpresaId(@PathVariable Long empresaId) {
        return clienteService.findByEmpresaId(empresaId);
    }

    @GetMapping("/empresa/{empresaId}/ativos")
    public Flux<Cliente> findAtivosByEmpresaId(@PathVariable Long empresaId) {
        return clienteService.findAtivosByEmpresaId(empresaId);
    }

    @GetMapping("/empresa/{empresaId}/ativos-nao-bloqueados")
    public Flux<Cliente> findAtivosNaoBloqueadosByEmpresaId(@PathVariable Long empresaId) {
        return clienteService.findAtivosNaoBloqueadosByEmpresaId(empresaId);
    }

    @GetMapping("/empresa/{empresaId}/search/razao-social")
    public Flux<Cliente> searchByRazaoSocial(
            @PathVariable Long empresaId,
            @RequestParam String razaoSocial) {
        return clienteService.searchByRazaoSocial(razaoSocial, empresaId);
    }

    @GetMapping("/empresa/{empresaId}/search/nome-fantasia")
    public Flux<Cliente> searchByNomeFantasia(
            @PathVariable Long empresaId,
            @RequestParam String nomeFantasia) {
        return clienteService.searchByNomeFantasia(nomeFantasia, empresaId);
    }

    @GetMapping("/empresa/{empresaId}/tipo/{tipo}")
    public Flux<Cliente> findByTipo(
            @PathVariable Long empresaId,
            @PathVariable String tipo) {
        return clienteService.findByTipo(tipo, empresaId);
    }

    @GetMapping("/empresa/{empresaId}/cidade/{cidade}")
    public Flux<Cliente> findByCidade(
            @PathVariable Long empresaId,
            @PathVariable String cidade) {
        return clienteService.findByCidade(cidade, empresaId);
    }

    @GetMapping("/empresa/{empresaId}/estado/{estado}")
    public Flux<Cliente> findByEstado(
            @PathVariable Long empresaId,
            @PathVariable String estado) {
        return clienteService.findByEstado(estado, empresaId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return clienteService.delete(id);
    }

    @PostMapping("/{id}/bloquear")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> bloquear(
            @PathVariable Long id,
            @RequestParam String motivo) {
        return clienteService.bloquear(id, motivo);
    }

    @PostMapping("/{id}/desbloquear")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> desbloquear(@PathVariable Long id) {
        return clienteService.desbloquear(id);
    }
}

