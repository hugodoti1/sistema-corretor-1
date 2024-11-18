package br.com.corretor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("comissoes")
public class Comissao {
    @Id
    private Long id;
    
    @Column("empresa_id")
    private Long empresaId;
    
    @Column("venda_id")
    private Long vendaId;
    
    @Column("corretor_id")
    private Long corretorId;
    
    @Column("fabrica_id")
    private Long fabricaId;
    
    @Column("data_venda")
    private LocalDate dataVenda;
    
    @Column("valor_venda")
    private BigDecimal valorVenda;
    
    @Column("percentual_comissao")
    private BigDecimal percentualComissao;
    
    @Column("valor_comissao")
    private BigDecimal valorComissao;
    
    @Column("valor_pago")
    private BigDecimal valorPago;
    
    @Column("data_previsao_pagamento")
    private LocalDate dataPrevisaoPagamento;
    
    @Column("data_pagamento")
    private LocalDate dataPagamento;
    
    @Column("status")
    private String status; // 'PENDENTE', 'PAGO', 'CANCELADO'
    
    @Column("forma_pagamento")
    private String formaPagamento;
    
    @Column("numero_nota_fiscal")
    private String numeroNotaFiscal;
    
    private String observacoes;

    // Construtor vazio
    public Comissao() {
    }

    // Construtor com todos os campos
    public Comissao(Long id, Long empresaId, Long vendaId, Long corretorId, Long fabricaId,
                   LocalDate dataVenda, BigDecimal valorVenda, BigDecimal percentualComissao,
                   BigDecimal valorComissao, BigDecimal valorPago, LocalDate dataPrevisaoPagamento, LocalDate dataPagamento,
                   String status, String formaPagamento, String numeroNotaFiscal, String observacoes) {
        this.id = id;
        this.empresaId = empresaId;
        this.vendaId = vendaId;
        this.corretorId = corretorId;
        this.fabricaId = fabricaId;
        this.dataVenda = dataVenda;
        this.valorVenda = valorVenda;
        this.percentualComissao = percentualComissao;
        this.valorComissao = valorComissao;
        this.valorPago = valorPago;
        this.dataPrevisaoPagamento = dataPrevisaoPagamento;
        this.dataPagamento = dataPagamento;
        this.status = status;
        this.formaPagamento = formaPagamento;
        this.numeroNotaFiscal = numeroNotaFiscal;
        this.observacoes = observacoes;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getVendaId() {
        return vendaId;
    }

    public void setVendaId(Long vendaId) {
        this.vendaId = vendaId;
    }

    public Long getCorretorId() {
        return corretorId;
    }

    public void setCorretorId(Long corretorId) {
        this.corretorId = corretorId;
    }

    public Long getFabricaId() {
        return fabricaId;
    }

    public void setFabricaId(Long fabricaId) {
        this.fabricaId = fabricaId;
    }

    public LocalDate getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDate dataVenda) {
        this.dataVenda = dataVenda;
    }

    public BigDecimal getValorVenda() {
        return valorVenda;
    }

    public void setValorVenda(BigDecimal valorVenda) {
        this.valorVenda = valorVenda;
    }

    public BigDecimal getPercentualComissao() {
        return percentualComissao;
    }

    public void setPercentualComissao(BigDecimal percentualComissao) {
        this.percentualComissao = percentualComissao;
    }

    public BigDecimal getValorComissao() {
        return valorComissao;
    }

    public void setValorComissao(BigDecimal valorComissao) {
        this.valorComissao = valorComissao;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public LocalDate getDataPrevisaoPagamento() {
        return dataPrevisaoPagamento;
    }

    public void setDataPrevisaoPagamento(LocalDate dataPrevisaoPagamento) {
        this.dataPrevisaoPagamento = dataPrevisaoPagamento;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getNumeroNotaFiscal() {
        return numeroNotaFiscal;
    }

    public void setNumeroNotaFiscal(String numeroNotaFiscal) {
        this.numeroNotaFiscal = numeroNotaFiscal;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    // Método estático para criar uma nova comissão
    public static Comissao criar(
            Long empresaId,
            Long vendaId,
            Long corretorId,
            Long fabricaId,
            LocalDate dataVenda,
            BigDecimal valorVenda,
            BigDecimal percentualComissao,
            BigDecimal valorComissao,
            LocalDate dataPrevisaoPagamento,
            String status,
            String formaPagamento,
            String numeroNotaFiscal) {
        return criar(empresaId, vendaId, corretorId, fabricaId, dataVenda, valorVenda, 
                    percentualComissao, valorComissao, BigDecimal.ZERO, dataPrevisaoPagamento, 
                    status, formaPagamento, numeroNotaFiscal);
    }

    public static Comissao criar(
            Long empresaId,
            Long vendaId,
            Long corretorId,
            Long fabricaId,
            LocalDate dataVenda,
            BigDecimal valorVenda,
            BigDecimal percentualComissao,
            BigDecimal valorComissao,
            BigDecimal valorPago,
            LocalDate dataPrevisaoPagamento,
            String status,
            String formaPagamento,
            String numeroNotaFiscal) {
        
        Comissao comissao = new Comissao();
        comissao.setEmpresaId(empresaId);
        comissao.setVendaId(vendaId);
        comissao.setCorretorId(corretorId);
        comissao.setFabricaId(fabricaId);
        comissao.setDataVenda(dataVenda);
        comissao.setValorVenda(valorVenda);
        comissao.setPercentualComissao(percentualComissao);
        comissao.setValorComissao(valorComissao);
        comissao.setValorPago(valorPago != null ? valorPago : BigDecimal.ZERO);
        comissao.setDataPrevisaoPagamento(dataPrevisaoPagamento);
        comissao.setStatus(status);
        comissao.setFormaPagamento(formaPagamento);
        comissao.setNumeroNotaFiscal(numeroNotaFiscal);
        return comissao;
    }
}
