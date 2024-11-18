package br.com.corretor.repository;

import br.com.corretor.model.Corretor;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CorretorRepository extends ReactiveCrudRepository<Corretor, Long> {
    
    Flux<Corretor> findByEmpresaId(Long empresaId);
    
    Flux<Corretor> findByEmpresaIdAndAtivo(Long empresaId, Boolean ativo);
    
    Flux<Corretor> findByEmpresaIdAndAtivoAndBloqueado(Long empresaId, Boolean ativo, Boolean bloqueado);
    
    @Query("SELECT * FROM corretores WHERE empresa_id = :empresaId AND LOWER(nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    Flux<Corretor> searchByNome(String nome, Long empresaId);
    
    @Query("SELECT * FROM corretores WHERE empresa_id = :empresaId AND cpf = :cpf")
    Mono<Corretor> findByCpfAndEmpresaId(String cpf, Long empresaId);
    
    Flux<Corretor> findByEmpresaIdAndCidade(Long empresaId, String cidade);
    
    Flux<Corretor> findByEmpresaIdAndEstado(Long empresaId, String estado);
    
    @Query("SELECT * FROM corretores WHERE empresa_id = :empresaId AND data_validade_registro < CURRENT_DATE")
    Flux<Corretor> findByRegistroVencido(Long empresaId);
    
    @Query("SELECT * FROM corretores WHERE empresa_id = :empresaId AND data_validade_registro BETWEEN CURRENT_DATE AND (CURRENT_DATE + INTERVAL '30 days')")
    Flux<Corretor> findByRegistroAVencer(Long empresaId);
}
