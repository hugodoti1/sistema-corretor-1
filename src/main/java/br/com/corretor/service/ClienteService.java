package br.com.corretor.service;

import br.com.corretor.dto.ClienteDTO;
import br.com.corretor.model.Cliente;
import br.com.corretor.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;

    public Mono<Cliente> save(ClienteDTO dto) {
        Cliente cliente = convertToEntity(dto);
        
        if (cliente.getId() == null) {
            return validarDocumentoUnico(cliente)
                .flatMap(valido -> {
                    if (!valido) {
                        String documento = "PJ".equals(cliente.getTipo()) ? "CNPJ" : "CPF";
                        return Mono.error(new IllegalArgumentException(documento + " já cadastrado para esta empresa"));
                    }
                    cliente.setAtivo(true);
                    cliente.setDataCadastro(LocalDate.now());
                    return clienteRepository.save(cliente);
                });
        } else {
            return validarDocumentoUnico(cliente)
                .flatMap(valido -> {
                    if (!valido) {
                        String documento = "PJ".equals(cliente.getTipo()) ? "CNPJ" : "CPF";
                        return Mono.error(new IllegalArgumentException(documento + " já cadastrado para esta empresa"));
                    }
                    return clienteRepository.findById(cliente.getId())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Cliente não encontrado")))
                        .flatMap(existingCliente -> {
                            cliente.setAtivo(existingCliente.getAtivo());
                            cliente.setDataCadastro(existingCliente.getDataCadastro());
                            return clienteRepository.save(cliente);
                        });
                });
        }
    }

    public Mono<Cliente> findById(Long id) {
        return clienteRepository.findById(id)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Cliente não encontrado")));
    }

    public Flux<Cliente> findByEmpresaId(Long empresaId) {
        return clienteRepository.findByEmpresaId(empresaId);
    }

    public Flux<Cliente> findAtivosByEmpresaId(Long empresaId) {
        return clienteRepository.findAtivosByEmpresaId(empresaId);
    }

    public Flux<Cliente> findAtivosNaoBloqueadosByEmpresaId(Long empresaId) {
        return clienteRepository.findAtivosNaoBloqueadosByEmpresaId(empresaId);
    }

    public Flux<Cliente> searchByRazaoSocial(String razaoSocial, Long empresaId) {
        return clienteRepository.findByRazaoSocialContainingIgnoreCaseAndEmpresaId(razaoSocial, empresaId);
    }

    public Flux<Cliente> searchByNomeFantasia(String nomeFantasia, Long empresaId) {
        return clienteRepository.findByNomeFantasiaContainingIgnoreCaseAndEmpresaId(nomeFantasia, empresaId);
    }

    public Flux<Cliente> findByTipo(String tipo, Long empresaId) {
        return clienteRepository.findByTipoAndEmpresaId(tipo, empresaId);
    }

    public Flux<Cliente> findByCidade(String cidade, Long empresaId) {
        return clienteRepository.findByCidadeAndEmpresaId(cidade, empresaId);
    }

    public Flux<Cliente> findByEstado(String estado, Long empresaId) {
        return clienteRepository.findByEstadoAndEmpresaId(estado, empresaId);
    }

    public Mono<Void> delete(Long id) {
        return clienteRepository.findById(id)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Cliente não encontrado")))
            .flatMap(cliente -> {
                cliente.setAtivo(false);
                return clienteRepository.save(cliente);
            })
            .then();
    }

    public Mono<Void> bloquear(Long id, String motivo) {
        return clienteRepository.findById(id)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Cliente não encontrado")))
            .flatMap(cliente -> {
                cliente.setBloqueado(true);
                cliente.setMotivoBloqueio(motivo);
                return clienteRepository.save(cliente);
            })
            .then();
    }

    public Mono<Void> desbloquear(Long id) {
        return clienteRepository.findById(id)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Cliente não encontrado")))
            .flatMap(cliente -> {
                cliente.setBloqueado(false);
                cliente.setMotivoBloqueio(null);
                return clienteRepository.save(cliente);
            })
            .then();
    }

    private Mono<Boolean> validarDocumentoUnico(Cliente cliente) {
        if ("PJ".equals(cliente.getTipo())) {
            if (cliente.getId() == null) {
                return clienteRepository.findByCnpjAndEmpresaId(cliente.getCnpj(), cliente.getEmpresaId())
                    .map(c -> false)
                    .defaultIfEmpty(true);
            }
            return clienteRepository.existsByCnpjAndEmpresaIdAndIdNot(
                cliente.getCnpj(), cliente.getEmpresaId(), cliente.getId())
                .map(exists -> !exists);
        } else {
            if (cliente.getId() == null) {
                return clienteRepository.findByCpfAndEmpresaId(cliente.getCpf(), cliente.getEmpresaId())
                    .map(c -> false)
                    .defaultIfEmpty(true);
            }
            return clienteRepository.existsByCpfAndEmpresaIdAndIdNot(
                cliente.getCpf(), cliente.getEmpresaId(), cliente.getId())
                .map(exists -> !exists);
        }
    }

    private Cliente convertToEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setId(dto.getId());
        cliente.setEmpresaId(dto.getEmpresaId());
        cliente.setRazaoSocial(dto.getRazaoSocial());
        cliente.setNomeFantasia(dto.getNomeFantasia());
        cliente.setCnpj(dto.getCnpj());
        cliente.setInscricaoEstadual(dto.getInscricaoEstadual());
        cliente.setCpf(dto.getCpf());
        cliente.setRg(dto.getRg());
        cliente.setEndereco(dto.getEndereco());
        cliente.setBairro(dto.getBairro());
        cliente.setCidade(dto.getCidade());
        cliente.setEstado(dto.getEstado());
        cliente.setCep(dto.getCep());
        cliente.setTelefone(dto.getTelefone());
        cliente.setCelular(dto.getCelular());
        cliente.setEmail(dto.getEmail());
        cliente.setWhatsapp(dto.getWhatsapp());
        cliente.setInstagram(dto.getInstagram());
        cliente.setSite(dto.getSite());
        cliente.setDataNascimento(dto.getDataNascimento());
        cliente.setDataCadastro(dto.getDataCadastro());
        cliente.setObservacoes(dto.getObservacoes());
        cliente.setLimiteCredito(dto.getLimiteCredito());
        cliente.setDiasPrazo(dto.getDiasPrazo());
        cliente.setCondicoesPagamento(dto.getCondicoesPagamento());
        cliente.setBloqueado(dto.getBloqueado());
        cliente.setMotivoBloqueio(dto.getMotivoBloqueio());
        cliente.setAtivo(dto.getAtivo());
        cliente.setTipo(dto.getTipo());
        return cliente;
    }
}
