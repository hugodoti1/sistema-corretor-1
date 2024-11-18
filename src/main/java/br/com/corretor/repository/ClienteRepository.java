package br.com.corretor.repository;

import br.com.corretor.model.Cliente;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClienteRepository extends ReactiveCrudRepository<Cliente, Long> {
    Flux<Cliente> findByEmpresaId(Long empresaId);
    
    Mono<Cliente> findByCnpjAndEmpresaId(String cnpj, Long empresaId);
    
    Mono<Cliente> findByCpfAndEmpresaId(String cpf, Long empresaId);
    
    @Query("SELECT * FROM clientes WHERE empresa_id = :empresaId AND ativo = true")
    Flux<Cliente> findAtivosByEmpresaId(Long empresaId);
    
    @Query("SELECT * FROM clientes WHERE empresa_id = :empresaId AND bloqueado = false AND ativo = true")
    Flux<Cliente> findAtivosNaoBloqueadosByEmpresaId(Long empresaId);
    
    @Query("SELECT COUNT(*) > 0 FROM clientes WHERE cnpj = :cnpj AND empresa_id = :empresaId AND id != :id")
    Mono<Boolean> existsByCnpjAndEmpresaIdAndIdNot(String cnpj, Long empresaId, Long id);
    
    @Query("SELECT COUNT(*) > 0 FROM clientes WHERE cpf = :cpf AND empresa_id = :empresaId AND id != :id")
    Mono<Boolean> existsByCpfAndEmpresaIdAndIdNot(String cpf, Long empresaId, Long id);
    
    Flux<Cliente> findByRazaoSocialContainingIgnoreCaseAndEmpresaId(String razaoSocial, Long empresaId);
    
    Flux<Cliente> findByNomeFantasiaContainingIgnoreCaseAndEmpresaId(String nomeFantasia, Long empresaId);
    
    @Query("SELECT * FROM clientes WHERE empresa_id = :empresaId AND tipo = :tipo AND ativo = true")
    Flux<Cliente> findByTipoAndEmpresaId(String tipo, Long empresaId);
    
    @Query("SELECT * FROM clientes WHERE empresa_id = :empresaId AND cidade = :cidade AND ativo = true")
    Flux<Cliente> findByCidadeAndEmpresaId(String cidade, Long empresaId);
    
    @Query("SELECT * FROM clientes WHERE empresa_id = :empresaId AND estado = :estado AND ativo = true")
    Flux<Cliente> findByEstadoAndEmpresaId(String estado, Long empresaId);
}
