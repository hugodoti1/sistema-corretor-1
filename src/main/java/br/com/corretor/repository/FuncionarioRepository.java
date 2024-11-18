package br.com.corretor.repository;

import br.com.corretor.model.Funcionario;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FuncionarioRepository extends ReactiveCrudRepository<Funcionario, Long> {
    Flux<Funcionario> findByEmpresaId(Long empresaId);
    
    Mono<Funcionario> findByCpfAndEmpresaId(String cpf, Long empresaId);
    
    @Query("SELECT * FROM funcionarios WHERE empresa_id = :empresaId AND ativo = true")
    Flux<Funcionario> findAtivosbyEmpresaId(Long empresaId);
    
    @Query("SELECT * FROM funcionarios WHERE empresa_id = :empresaId AND data_demissao IS NULL")
    Flux<Funcionario> findAtuaisbyEmpresaId(Long empresaId);
    
    @Query("SELECT COUNT(*) > 0 FROM funcionarios WHERE cpf = :cpf AND empresa_id = :empresaId AND id != :id")
    Mono<Boolean> existsByCpfAndEmpresaIdAndIdNot(String cpf, Long empresaId, Long id);
}
