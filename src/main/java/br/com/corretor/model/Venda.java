package br.com.corretor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("vendas")
public class Venda {
    @Id
    private Long id;
    
    @Column("empresa_id")
    private Long empresaId;
    
    @Column("data_venda")
    private LocalDate dataVenda;
    
    @Column("fabrica_id")
    private Long fabricaId;
    
    @Column("cliente_id")
    private Long clienteId;
    
    @Column("corretor_id")
    private Long corretorId;
    
    @Column("tipo_venda")
    private String tipoVenda;  // 'PRONTA_ENTREGA', 'PEDIDO', 'PROMOCAO', 'PONTA_ESTOQUE'
    
    private String evento;
    
    @Column("valor_venda")
    private BigDecimal valorVenda;
    
    @Column("numero_nota_fiscal")
    private String numeroNotaFiscal;
    
    @Column("percentual_comissao")
    private BigDecimal percentualComissao;
    
    @Column("valor_comissao")
    private BigDecimal valorComissao;
    
    @Column("forma_pagamento")
    private String formaPagamento;  // 'BOLETO', 'CHEQUE', 'PIX', 'TRANSFERENCIA', 'CARTAO'
    
    @Column("quantidade_parcelas")
    private Integer quantidadeParcelas;

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

	public LocalDate getDataVenda() {
		return dataVenda;
	}

	public void setDataVenda(LocalDate dataVenda) {
		this.dataVenda = dataVenda;
	}

	public Long getFabricaId() {
		return fabricaId;
	}

	public void setFabricaId(Long fabricaId) {
		this.fabricaId = fabricaId;
	}

	public Long getClienteId() {
		return clienteId;
	}

	public void setClienteId(Long clienteId) {
		this.clienteId = clienteId;
	}

	public Long getCorretorId() {
		return corretorId;
	}

	public void setCorretorId(Long corretorId) {
		this.corretorId = corretorId;
	}

	public String getTipoVenda() {
		return tipoVenda;
	}

	public void setTipoVenda(String tipoVenda) {
		this.tipoVenda = tipoVenda;
	}

	public String getEvento() {
		return evento;
	}

	public void setEvento(String evento) {
		this.evento = evento;
	}

	public BigDecimal getValorVenda() {
		return valorVenda;
	}

	public void setValorVenda(BigDecimal valorVenda) {
		this.valorVenda = valorVenda;
	}

	public String getNumeroNotaFiscal() {
		return numeroNotaFiscal;
	}

	public void setNumeroNotaFiscal(String numeroNotaFiscal) {
		this.numeroNotaFiscal = numeroNotaFiscal;
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

	public String getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(String formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public Integer getQuantidadeParcelas() {
		return quantidadeParcelas;
	}

	public void setQuantidadeParcelas(Integer quantidadeParcelas) {
		this.quantidadeParcelas = quantidadeParcelas;
	}
    
    
}

