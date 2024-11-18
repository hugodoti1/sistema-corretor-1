package br.com.corretor.service;

import br.com.corretor.dto.EmpresaDTO;
import br.com.corretor.model.Empresa;
import br.com.corretor.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmpresaService {
	
	@Autowired
    private EmpresaRepository empresaRepository;

    public Mono<Empresa> criar(EmpresaDTO empresaDTO) {
        return empresaRepository.existsByCnpj(empresaDTO.getCnpj())
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new RuntimeException("CNPJ já cadastrado"));
                }
                
                Empresa empresa = new Empresa();
                empresa.setRazaoSocial(empresaDTO.getRazaoSocial());
                empresa.setCnpj(empresaDTO.getCnpj());
                empresa.setNomeFantasia(empresaDTO.getNomeFantasia());
                empresa.setInscricaoEstadual(empresaDTO.getInscricaoEstadual());
                empresa.setEndereco(empresaDTO.getEndereco());
                empresa.setCidade(empresaDTO.getCidade());
                empresa.setEstado(empresaDTO.getEstado());
                empresa.setCep(empresaDTO.getCep());
                empresa.setTelefone(empresaDTO.getTelefone());
                empresa.setEmail(empresaDTO.getEmail());
                empresa.setDataCadastro(LocalDateTime.now());
                empresa.setAtivo(true);
                
                return empresaRepository.save(empresa);
            });
    }

    public Mono<Empresa> atualizar(Long id, EmpresaDTO empresaDTO) {
        return empresaRepository.findById(id)
            .flatMap(empresa -> {
                empresa.setRazaoSocial(empresaDTO.getRazaoSocial());
                empresa.setNomeFantasia(empresaDTO.getNomeFantasia());
                empresa.setInscricaoEstadual(empresaDTO.getInscricaoEstadual());
                empresa.setEndereco(empresaDTO.getEndereco());
                empresa.setCidade(empresaDTO.getCidade());
                empresa.setEstado(empresaDTO.getEstado());
                empresa.setCep(empresaDTO.getCep());
                empresa.setTelefone(empresaDTO.getTelefone());
                empresa.setEmail(empresaDTO.getEmail());
                empresa.setAtivo(empresaDTO.getAtivo());
                return empresaRepository.save(empresa);
            })
            .switchIfEmpty(Mono.error(new RuntimeException("Empresa não encontrada")));
    }

    public Mono<Void> excluir(Long id) {
        return empresaRepository.findById(id)
            .flatMap(empresa -> empresaRepository.delete(empresa))
            .switchIfEmpty(Mono.error(new RuntimeException("Empresa não encontrada")));
    }

    public Mono<Empresa> buscarPorId(Long id) {
        return empresaRepository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("Empresa não encontrada")));
    }

    public Mono<Empresa> buscarPorCnpj(String cnpj) {
        return empresaRepository.findByCnpj(cnpj)
            .switchIfEmpty(Mono.error(new RuntimeException("Empresa não encontrada")));
    }

    public Flux<Empresa> listarTodas() {
        return empresaRepository.findAll();
    }
}
