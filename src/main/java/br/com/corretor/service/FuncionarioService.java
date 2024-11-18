package br.com.corretor.service;

import br.com.corretor.dto.FuncionarioDTO;
import br.com.corretor.model.Funcionario;
import br.com.corretor.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FuncionarioService {
	
	@Autowired
    private FuncionarioRepository funcionarioRepository;

    public Mono<Funcionario> save(FuncionarioDTO dto) {
        Funcionario funcionario = convertToEntity(dto);
        
        if (funcionario.getId() == null) {
            return checkCpfUnico(funcionario.getCpf(), funcionario.getEmpresaId(), null)
                .flatMap(cpfUnico -> {
                    if (!cpfUnico) {
                        return Mono.error(new IllegalArgumentException("CPF já cadastrado para esta empresa"));
                    }
                    funcionario.setAtivo(true);
                    return funcionarioRepository.save(funcionario);
                });
        } else {
            return checkCpfUnico(funcionario.getCpf(), funcionario.getEmpresaId(), funcionario.getId())
                .flatMap(cpfUnico -> {
                    if (!cpfUnico) {
                        return Mono.error(new IllegalArgumentException("CPF já cadastrado para esta empresa"));
                    }
                    return funcionarioRepository.findById(funcionario.getId())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Funcionário não encontrado")))
                        .flatMap(existingFuncionario -> {
                            funcionario.setAtivo(existingFuncionario.getAtivo());
                            return funcionarioRepository.save(funcionario);
                        });
                });
        }
    }

    public Mono<Funcionario> findById(Long id) {
        return funcionarioRepository.findById(id)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Funcionário não encontrado")));
    }

    public Flux<Funcionario> findByEmpresaId(Long empresaId) {
        return funcionarioRepository.findByEmpresaId(empresaId);
    }

    public Flux<Funcionario> findAtivosbyEmpresaId(Long empresaId) {
        return funcionarioRepository.findAtivosbyEmpresaId(empresaId);
    }

    public Flux<Funcionario> findAtuaisbyEmpresaId(Long empresaId) {
        return funcionarioRepository.findAtuaisbyEmpresaId(empresaId);
    }

    public Mono<Void> delete(Long id) {
        return funcionarioRepository.findById(id)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Funcionário não encontrado")))
            .flatMap(funcionario -> {
                funcionario.setAtivo(false);
                return funcionarioRepository.save(funcionario);
            })
            .then();
    }

    private Mono<Boolean> checkCpfUnico(String cpf, Long empresaId, Long id) {
        if (id == null) {
            return funcionarioRepository.findByCpfAndEmpresaId(cpf, empresaId)
                .map(f -> false)
                .defaultIfEmpty(true);
        }
        return funcionarioRepository.existsByCpfAndEmpresaIdAndIdNot(cpf, empresaId, id)
            .map(exists -> !exists);
    }

    private Funcionario convertToEntity(FuncionarioDTO dto) {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(dto.getId());
        funcionario.setEmpresaId(dto.getEmpresaId());
        funcionario.setNome(dto.getNome());
        funcionario.setCpf(dto.getCpf());
        funcionario.setRg(dto.getRg());
        funcionario.setDataNascimento(dto.getDataNascimento());
        funcionario.setEndereco(dto.getEndereco());
        funcionario.setTelefone(dto.getTelefone());
        funcionario.setEmail(dto.getEmail());
        funcionario.setWhatsapp(dto.getWhatsapp());
        funcionario.setPix(dto.getPix());
        funcionario.setCarteiraTrabalho(dto.getCarteiraTrabalho());
        funcionario.setDataAdmissao(dto.getDataAdmissao());
        funcionario.setDataDemissao(dto.getDataDemissao());
        funcionario.setComissaoVendas(dto.getComissaoVendas());
        funcionario.setValorBonificacao(dto.getValorBonificacao());
        funcionario.setAtivo(dto.getAtivo());
        return funcionario;
    }
}

