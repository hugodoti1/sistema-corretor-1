package br.com.corretor.repository;

import br.com.corretor.model.Conciliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConciliacaoRepository extends JpaRepository<Conciliacao, Long> {
    
    List<Conciliacao> findByEmpresaIdAndBancoId(Long empresaId, Long bancoId);
    
    List<Conciliacao> findByEmpresaIdAndBancoIdAndDataInicioBetween(
            Long empresaId, Long bancoId, LocalDateTime dataInicio, LocalDateTime dataFim);
    
    List<Conciliacao> findByEmpresaIdAndBancoIdAndConcluida(
            Long empresaId, Long bancoId, boolean concluida);
}
