package br.com.corretor.repository;

import br.com.corretor.model.TransacaoBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransacaoBancariaRepository extends JpaRepository<TransacaoBancaria, Long> {
    List<TransacaoBancaria> findByContaBancariaIdAndDataBetweenOrderByDataDesc(
        Long contaBancariaId, 
        LocalDateTime dataInicio, 
        LocalDateTime dataFim
    );

    List<TransacaoBancaria> findByContaBancariaIdAndConciliadoFalseOrderByDataDesc(Long contaBancariaId);

    @Query("SELECT t FROM TransacaoBancaria t WHERE t.contaBancaria.id = :contaBancariaId " +
           "AND t.idTransacaoBanco = :idTransacaoBanco")
    Optional<TransacaoBancaria> findByContaBancariaIdAndIdTransacaoBanco(
        Long contaBancariaId, 
        String idTransacaoBanco
    );
}
