package br.com.corretor.repository;

import br.com.corretor.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    
    List<Transacao> findByEmpresaIdAndBancoIdAndDataTransacaoBetween(
            Long empresaId, Long bancoId, LocalDateTime dataInicio, LocalDateTime dataFim);
    
    List<Transacao> findByEmpresaIdAndBancoIdAndDataTransacaoBetweenAndConciliadaTrue(
            Long empresaId, Long bancoId, LocalDateTime dataInicio, LocalDateTime dataFim);
    
    List<Transacao> findByEmpresaIdAndBancoIdAndDataTransacaoBetweenAndConciliadaFalse(
            Long empresaId, Long bancoId, LocalDateTime dataInicio, LocalDateTime dataFim);
}
