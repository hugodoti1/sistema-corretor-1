package br.com.corretor.repository;

import br.com.corretor.model.Empresa;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface EmpresaRepository extends R2dbcRepository<Empresa, Long> {
    Mono<Empresa> findByCnpj(String cnpj);
    Mono<Boolean> existsByCnpj(String cnpj);
}
