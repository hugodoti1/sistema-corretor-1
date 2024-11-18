package br.com.corretor.repository;

import br.com.corretor.model.ContaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, Long> {
    Optional<ContaBancaria> findByBancoAndAgenciaAndConta(String banco, String agencia, String conta);
    boolean existsByBancoAndAgenciaAndConta(String banco, String agencia, String conta);
}
