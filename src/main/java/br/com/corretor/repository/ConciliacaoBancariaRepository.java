package br.com.corretor.repository;

import br.com.corretor.model.ConciliacaoBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConciliacaoBancariaRepository extends JpaRepository<ConciliacaoBancaria, Long> {
    List<ConciliacaoBancaria> findByDataConciliacaoBetweenOrderByDataConciliacaoDesc(
        LocalDateTime dataInicio,
        LocalDateTime dataFim
    );

    List<ConciliacaoBancaria> findByUsuarioConciliacao(String usuarioConciliacao);

    boolean existsByTransacaoBancariaIdOrTransacaoSistemaId(Long transacaoBancariaId, Long transacaoSistemaId);
}
