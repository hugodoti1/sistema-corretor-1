package br.com.corretor.service;

import br.com.corretor.dto.FabricaDTO;
import br.com.corretor.model.Fabrica;
import br.com.corretor.repository.FabricaRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FabricaService {
    
	@Autowired
	private FabricaRepository fabricaRepository;

    public Mono<Fabrica> save(FabricaDTO dto) {
        Fabrica fabrica = convertToEntity(dto);
        
        if (fabrica.getId() == null) {
            return checkCnpjUnico(fabrica.getCnpj(), fabrica.getEmpresaId(), null)
                .flatMap(cnpjUnico -> {
                    if (!cnpjUnico) {
                        return Mono.error(new IllegalArgumentException("CNPJ já cadastrado para esta empresa"));
                    }
                    fabrica.setAtivo(true);
                    return fabricaRepository.save(fabrica);
                });
        } else {
            return checkCnpjUnico(fabrica.getCnpj(), fabrica.getEmpresaId(), fabrica.getId())
                .flatMap(cnpjUnico -> {
                    if (!cnpjUnico) {
                        return Mono.error(new IllegalArgumentException("CNPJ já cadastrado para esta empresa"));
                    }
                    return fabricaRepository.findById(fabrica.getId())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Fábrica não encontrada")))
                        .flatMap(existingFabrica -> {
                            fabrica.setAtivo(existingFabrica.getAtivo());
                            return fabricaRepository.save(fabrica);
                        });
                });
        }
    }

    public Mono<Fabrica> findById(Long id) {
        return fabricaRepository.findById(id)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Fábrica não encontrada")));
    }

    public Flux<Fabrica> findByEmpresaId(Long empresaId) {
        return fabricaRepository.findByEmpresaId(empresaId);
    }

    public Flux<Fabrica> findAtivasByEmpresaId(Long empresaId) {
        return fabricaRepository.findAtivasByEmpresaId(empresaId);
    }

    public Flux<Fabrica> searchByRazaoSocial(String razaoSocial, Long empresaId) {
        return fabricaRepository.findByRazaoSocialContainingIgnoreCaseAndEmpresaId(razaoSocial, empresaId);
    }

    public Flux<Fabrica> searchByNomeFantasia(String nomeFantasia, Long empresaId) {
        return fabricaRepository.findByNomeFantasiaContainingIgnoreCaseAndEmpresaId(nomeFantasia, empresaId);
    }

    public Mono<Void> delete(Long id) {
        return fabricaRepository.findById(id)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Fábrica não encontrada")))
            .flatMap(fabrica -> {
                fabrica.setAtivo(false);
                return fabricaRepository.save(fabrica);
            })
            .then();
    }

    private Mono<Boolean> checkCnpjUnico(String cnpj, Long empresaId, Long id) {
        if (id == null) {
            return fabricaRepository.findByCnpjAndEmpresaId(cnpj, empresaId)
                .map(f -> false)
                .defaultIfEmpty(true);
        }
        return fabricaRepository.existsByCnpjAndEmpresaIdAndIdNot(cnpj, empresaId, id)
            .map(exists -> !exists);
    }

    private Fabrica convertToEntity(FabricaDTO dto) {
        Fabrica fabrica = new Fabrica();
        fabrica.setId(dto.getId());
        fabrica.setEmpresaId(dto.getEmpresaId());
        fabrica.setRazaoSocial(dto.getRazaoSocial());
        fabrica.setNomeFantasia(dto.getNomeFantasia());
        fabrica.setCnpj(dto.getCnpj());
        fabrica.setInscricaoEstadual(dto.getInscricaoEstadual());
        fabrica.setEndereco(dto.getEndereco());
        fabrica.setBairro(dto.getBairro());
        fabrica.setCidade(dto.getCidade());
        fabrica.setEstado(dto.getEstado());
        fabrica.setCep(dto.getCep());
        fabrica.setTelefone(dto.getTelefone());
        fabrica.setEmail(dto.getEmail());
        fabrica.setWhatsapp(dto.getWhatsapp());
        fabrica.setSite(dto.getSite());
        fabrica.setObservacoes(dto.getObservacoes());
        fabrica.setAtivo(dto.getAtivo());
        fabrica.setPercentuaisComissao(dto.getPercentuaisComissao());
        return fabrica;
    }
}

