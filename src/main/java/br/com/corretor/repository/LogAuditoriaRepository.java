package br.com.corretor.repository;

import br.com.corretor.model.LogAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long>, JpaSpecificationExecutor<LogAuditoria> {
    
    List<LogAuditoria> findByEntidadeAndDataHoraBetween(String entidade, LocalDateTime inicio, LocalDateTime fim);
    
    List<LogAuditoria> findByUsuarioAndDataHoraBetween(String usuario, LocalDateTime inicio, LocalDateTime fim);
    
    List<LogAuditoria> findByAcaoAndDataHoraBetween(String acao, LocalDateTime inicio, LocalDateTime fim);
    
    List<LogAuditoria> findByEntidadeAndAcaoAndDataHoraBetween(String entidade, String acao, LocalDateTime inicio, LocalDateTime fim);
}
