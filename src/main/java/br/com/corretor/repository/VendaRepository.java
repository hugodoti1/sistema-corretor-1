package br.com.corretor.repository;

import br.com.corretor.model.Venda;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface VendaRepository extends ReactiveCrudRepository<Venda, Long> {
    
    Flux<Venda> findByEmpresaId(Long empresaId);
    
    Flux<Venda> findByEmpresaIdAndClienteId(Long empresaId, Long clienteId);
    
    Flux<Venda> findByEmpresaIdAndCorretorId(Long empresaId, Long corretorId);
    
    Flux<Venda> findByEmpresaIdAndFabricaId(Long empresaId, Long fabricaId);
    
    @Query("SELECT * FROM vendas WHERE empresa_id = :empresaId AND data_venda BETWEEN :inicio AND :fim")
    Flux<Venda> findByPeriodo(Long empresaId, LocalDate inicio, LocalDate fim);
    
    @Query("SELECT * FROM vendas WHERE empresa_id = :empresaId AND tipo_venda = :tipoVenda")
    Flux<Venda> findByTipoVenda(Long empresaId, String tipoVenda);
    
    @Query("SELECT * FROM vendas WHERE empresa_id = :empresaId AND evento = :evento")
    Flux<Venda> findByEvento(Long empresaId, String evento);
    
    @Query("SELECT * FROM vendas WHERE empresa_id = :empresaId AND forma_pagamento = :formaPagamento")
    Flux<Venda> findByFormaPagamento(Long empresaId, String formaPagamento);
    
    @Query("SELECT * FROM vendas WHERE empresa_id = :empresaId AND numero_nota_fiscal IS NOT NULL")
    Flux<Venda> findFaturadas(Long empresaId);
    
    @Query("SELECT * FROM vendas WHERE empresa_id = :empresaId AND numero_nota_fiscal IS NULL")
    Flux<Venda> findNaoFaturadas(Long empresaId);
    
    @Query("SELECT * FROM vendas WHERE empresa_id = :empresaId AND valor_venda >= :valorMinimo")
    Flux<Venda> findByValorMinimo(Long empresaId, BigDecimal valorMinimo);
    
    @Query("SELECT * FROM vendas WHERE empresa_id = :empresaId AND valor_comissao >= :valorMinimo")
    Flux<Venda> findByValorComissaoMinimo(Long empresaId, BigDecimal valorMinimo);
}

