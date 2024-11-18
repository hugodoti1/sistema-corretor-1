package br.com.corretor.repository;

import br.com.corretor.model.Fabrica;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FabricaRepository extends ReactiveCrudRepository<Fabrica, Long> {
    Flux<Fabrica> findByEmpresaId(Long empresaId);
    
    Mono<Fabrica> findByCnpjAndEmpresaId(String cnpj, Long empresaId);
    
    @Query("SELECT * FROM fabricas WHERE empresa_id = :empresaId AND ativo = true")
    Flux<Fabrica> findAtivasByEmpresaId(Long empresaId);
    
    @Query("SELECT COUNT(*) > 0 FROM fabricas WHERE cnpj = :cnpj AND empresa_id = :empresaId AND id != :id")
    Mono<Boolean> existsByCnpjAndEmpresaIdAndIdNot(String cnpj, Long empresaId, Long id);
    
    Flux<Fabrica> findByRazaoSocialContainingIgnoreCaseAndEmpresaId(String razaoSocial, Long empresaId);
    
    Flux<Fabrica> findByNomeFantasiaContainingIgnoreCaseAndEmpresaId(String nomeFantasia, Long empresaId);
}
