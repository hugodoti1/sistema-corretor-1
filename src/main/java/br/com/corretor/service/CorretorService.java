package br.com.corretor.service;

import br.com.corretor.dto.CorretorDTO;
import br.com.corretor.model.Corretor;
import br.com.corretor.repository.CorretorRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class CorretorService {
    
	@Autowired
	private CorretorRepository corretorRepository;

    public Mono<Corretor> save(CorretorDTO dto) {
        return validateUniqueCpf(dto)
                .then(Mono.just(dto))
                .map(this::toEntity)
                .flatMap(corretorRepository::save);
    }

    private Mono<Void> validateUniqueCpf(CorretorDTO dto) {
        return corretorRepository.findByCpfAndEmpresaId(dto.getCpf(), dto.getEmpresaId())
                .filter(corretor -> !corretor.getId().equals(dto.getId()))
                .flatMap(corretor -> Mono.error(new RuntimeException("CPF já cadastrado para esta empresa")))
                .then();
    }

    private Corretor toEntity(CorretorDTO dto) {
        Corretor corretor = new Corretor();
        corretor.setId(dto.getId());
        corretor.setEmpresaId(dto.getEmpresaId());
        corretor.setNome(dto.getNome());
        corretor.setCpf(dto.getCpf());
        corretor.setRg(dto.getRg());
        corretor.setDataNascimento(dto.getDataNascimento());
        corretor.setEndereco(dto.getEndereco());
        corretor.setBairro(dto.getBairro());
        corretor.setCidade(dto.getCidade());
        corretor.setEstado(dto.getEstado());
        corretor.setCep(dto.getCep());
        corretor.setTelefone(dto.getTelefone());
        corretor.setCelular(dto.getCelular());
        corretor.setEmail(dto.getEmail());
        corretor.setWhatsapp(dto.getWhatsapp());
        corretor.setDataCadastro(dto.getDataCadastro() != null ? dto.getDataCadastro() : LocalDate.now());
        corretor.setComissaoPadrao(dto.getComissaoPadrao());
        corretor.setObservacoes(dto.getObservacoes());
        corretor.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        corretor.setBloqueado(dto.getBloqueado() != null ? dto.getBloqueado() : false);
        corretor.setMotivoBloqueio(dto.getMotivoBloqueio());
        corretor.setNumeroRegistro(dto.getNumeroRegistro());
        corretor.setDataRegistro(dto.getDataRegistro());
        corretor.setDataValidadeRegistro(dto.getDataValidadeRegistro());
        corretor.setTipoRegistro(dto.getTipoRegistro());
        corretor.setOrgaoRegistro(dto.getOrgaoRegistro());
        return corretor;
    }

    public Mono<Corretor> findById(Long id) {
        return corretorRepository.findById(id);
    }

    public Flux<Corretor> findByEmpresaId(Long empresaId) {
        return corretorRepository.findByEmpresaId(empresaId);
    }

    public Flux<Corretor> findAtivosByEmpresaId(Long empresaId) {
        return corretorRepository.findByEmpresaIdAndAtivo(empresaId, true);
    }

    public Flux<Corretor> findAtivosNaoBloqueadosByEmpresaId(Long empresaId) {
        return corretorRepository.findByEmpresaIdAndAtivoAndBloqueado(empresaId, true, false);
    }

    public Flux<Corretor> searchByNome(String nome, Long empresaId) {
        return corretorRepository.searchByNome(nome, empresaId);
    }

    public Flux<Corretor> findByCidade(String cidade, Long empresaId) {
        return corretorRepository.findByEmpresaIdAndCidade(empresaId, cidade);
    }

    public Flux<Corretor> findByEstado(String estado, Long empresaId) {
        return corretorRepository.findByEmpresaIdAndEstado(empresaId, estado);
    }

    public Mono<Void> delete(Long id) {
        return corretorRepository.deleteById(id);
    }

    public Mono<Void> bloquear(Long id, String motivo) {
        return corretorRepository.findById(id)
                .map(corretor -> {
                    corretor.setBloqueado(true);
                    corretor.setMotivoBloqueio(motivo);
                    return corretor;
                })
                .flatMap(corretorRepository::save)
                .then();
    }

    public Mono<Void> desbloquear(Long id) {
        return corretorRepository.findById(id)
                .map(corretor -> {
                    corretor.setBloqueado(false);
                    corretor.setMotivoBloqueio(null);
                    return corretor;
                })
                .flatMap(corretorRepository::save)
                .then();
    }

    public Flux<Corretor> findByRegistroVencido(Long empresaId) {
        return corretorRepository.findByRegistroVencido(empresaId);
    }

    public Flux<Corretor> findByRegistroAVencer(Long empresaId) {
        return corretorRepository.findByRegistroAVencer(empresaId);
    }
}

