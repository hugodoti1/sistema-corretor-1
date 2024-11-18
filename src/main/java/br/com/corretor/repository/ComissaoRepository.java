package br.com.corretor.repository;

import br.com.corretor.model.Comissao;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface ComissaoRepository extends ReactiveCrudRepository<Comissao, Long> {
	
	
    @Query("SELECT c FROM Comissao c WHERE c.vendaId = :vendaId")
    Mono<Comissao> findByVendaId(Long vendaId);

    @Query("SELECT c FROM Comissao c WHERE " +
           "(:empresaId IS NULL OR c.empresaId = :empresaId) AND " +
           "(:corretorId IS NULL OR c.corretorId = :corretorId) AND " +
           "(:fabricaId IS NULL OR c.fabricaId = :fabricaId) AND " +
           "(:dataInicio IS NULL OR c.dataVenda >= :dataInicio) AND " +
           "(:dataFim IS NULL OR c.dataVenda <= :dataFim) AND " +
           "(:status IS NULL OR c.status = :status)")
    Flux<Comissao> findByFiltros(
            Long empresaId,
            Long corretorId,
            Long fabricaId,
            LocalDate dataInicio,
            LocalDate dataFim,
            String status);

    @Query("SELECT c FROM Comissao c WHERE " +
           "c.empresaId = :empresaId AND " +
           "c.status = 'PENDENTE' AND " +
           "c.dataVencimento < :dataReferencia")
    Flux<Comissao> findVencidas(Long empresaId, LocalDate dataReferencia);

    @Query("SELECT c FROM Comissao c WHERE " +
           "c.empresaId = :empresaId AND " +
           "c.status = 'PENDENTE' AND " +
           "c.dataVencimento >= :dataReferencia")
    Flux<Comissao> findAVencer(Long empresaId, LocalDate dataReferencia);

    @Query("SELECT c FROM Comissao c WHERE c.corretorId = :corretorId")
    Flux<Comissao> findByCorretorId(Long corretorId);

    @Query("SELECT c FROM Comissao c WHERE c.fabricaId = :fabricaId")
    Flux<Comissao> findByFabricaId(Long fabricaId);

    @Query("SELECT c FROM Comissao c WHERE " +
           "c.empresaId = :empresaId AND " +
           "c.dataVenda BETWEEN :dataInicio AND :dataFim")
    Flux<Comissao> findByEmpresaIdAndDataVendaBetween(
            Long empresaId,
            LocalDate dataInicio,
            LocalDate dataFim);

    @Query("SELECT c FROM Comissao c WHERE " +
           "c.empresaId = :empresaId AND " +
           "c.dataVencimento BETWEEN :dataInicio AND :dataFim")
    Flux<Comissao> findByEmpresaIdAndDataVencimentoBetween(
            Long empresaId,
            LocalDate dataInicio,
            LocalDate dataFim);

    @Query("SELECT c FROM Comissao c WHERE " +
           "c.empresaId = :empresaId AND " +
           "c.dataPagamento BETWEEN :dataInicio AND :dataFim")
    Flux<Comissao> findByEmpresaIdAndDataPagamentoBetween(
            Long empresaId,
            LocalDate dataInicio,
            LocalDate dataFim);

    @Query("SELECT c FROM Comissao c WHERE " +
           "c.empresaId = :empresaId AND " +
           "c.status = :status")
    Flux<Comissao> findByEmpresaIdAndStatus(Long empresaId, String status);

    @Query("SELECT c FROM Comissao c WHERE " +
           "c.empresaId = :empresaId AND " +
           "c.dataVencimento < :dataReferencia AND " +
           "c.status = 'PENDENTE'")
    Flux<Comissao> findVencidasPorEmpresa(Long empresaId, LocalDate dataReferencia);

    @Query("SELECT c FROM Comissao c WHERE " +
           "c.empresaId = :empresaId AND " +
           "c.corretorId = :corretorId AND " +
           "c.dataVenda BETWEEN :dataInicio AND :dataFim")
    Flux<Comissao> findByEmpresaIdAndCorretorIdAndPeriodo(
            Long empresaId,
            Long corretorId,
            LocalDate dataInicio,
            LocalDate dataFim);

    @Query("SELECT c FROM Comissao c WHERE " +
           "c.empresaId = :empresaId AND " +
           "c.fabricaId = :fabricaId AND " +
           "c.dataVenda BETWEEN :dataInicio AND :dataFim")
    Flux<Comissao> findByEmpresaIdAndFabricaIdAndPeriodo(
            Long empresaId,
            Long fabricaId,
            LocalDate dataInicio,
            LocalDate dataFim);
    
    Flux<Comissao> findByEmpresaId(Long empresaId);
    
    
    Flux<Comissao> findByStatus(String status);
    
    @Query("SELECT * FROM comissoes WHERE empresa_id = :empresaId AND data_venda BETWEEN :dataInicio AND :dataFim")
    Flux<Comissao> findByPeriodo(Long empresaId, LocalDate dataInicio, LocalDate dataFim);
    
    @Query("SELECT * FROM comissoes WHERE empresa_id = :empresaId AND corretor_id = :corretorId AND data_venda BETWEEN :dataInicio AND :dataFim")
    Flux<Comissao> findByCorretorEPeriodo(Long empresaId, Long corretorId, LocalDate dataInicio, LocalDate dataFim);
    
    Mono<Void> deleteByVendaId(Long vendaId);
    
}
