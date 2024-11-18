package br.com.corretor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComissaoResumoDTO {
    private Long totalComissoes;
    private BigDecimal valorTotalComissoes;
    private BigDecimal valorTotalPago;
    private BigDecimal valorTotalPendente;
    
    
    private Long totalPendente;
    private Long totalPago;
    private Long totalCancelado;
    
    private Long comissoesPendentes;
    private Long comissoesPagas;
    private Long comissoesCanceladas;
    private Long comissoesVencidas;
    
    private BigDecimal valorComissoesPendentes;
    private BigDecimal valorComissoesPagas;
    private BigDecimal valorComissoesCanceladas;
    private BigDecimal valorComissoesVencidas;
    
    private BigDecimal percentualPago;
    private BigDecimal percentualPendente;
    private BigDecimal percentualCancelado;
    private BigDecimal percentualVencido;
	public Long getTotalComissoes() {
		return totalComissoes;
	}
	public void setTotalComissoes(Long totalComissoes) {
		this.totalComissoes = totalComissoes;
	}
	public BigDecimal getValorTotalComissoes() {
		return valorTotalComissoes;
	}
	public void setValorTotalComissoes(BigDecimal valorTotalComissoes) {
		this.valorTotalComissoes = valorTotalComissoes;
	}
	public BigDecimal getValorTotalPago() {
		return valorTotalPago;
	}
	public void setValorTotalPago(BigDecimal valorTotalPago) {
		this.valorTotalPago = valorTotalPago;
	}
	public BigDecimal getValorTotalPendente() {
		return valorTotalPendente;
	}
	public void setValorTotalPendente(BigDecimal valorTotalPendente) {
		this.valorTotalPendente = valorTotalPendente;
	}
	public Long getComissoesPendentes() {
		return comissoesPendentes;
	}
	public void setComissoesPendentes(Long comissoesPendentes) {
		this.comissoesPendentes = comissoesPendentes;
	}
	public Long getComissoesPagas() {
		return comissoesPagas;
	}
	public void setComissoesPagas(Long comissoesPagas) {
		this.comissoesPagas = comissoesPagas;
	}
	public Long getComissoesCanceladas() {
		return comissoesCanceladas;
	}
	public void setComissoesCanceladas(Long comissoesCanceladas) {
		this.comissoesCanceladas = comissoesCanceladas;
	}
	public Long getComissoesVencidas() {
		return comissoesVencidas;
	}
	public void setComissoesVencidas(Long comissoesVencidas) {
		this.comissoesVencidas = comissoesVencidas;
	}
	public BigDecimal getValorComissoesPendentes() {
		return valorComissoesPendentes;
	}
	public void setValorComissoesPendentes(BigDecimal valorComissoesPendentes) {
		this.valorComissoesPendentes = valorComissoesPendentes;
	}
	public BigDecimal getValorComissoesPagas() {
		return valorComissoesPagas;
	}
	public void setValorComissoesPagas(BigDecimal valorComissoesPagas) {
		this.valorComissoesPagas = valorComissoesPagas;
	}
	public BigDecimal getValorComissoesCanceladas() {
		return valorComissoesCanceladas;
	}
	public void setValorComissoesCanceladas(BigDecimal valorComissoesCanceladas) {
		this.valorComissoesCanceladas = valorComissoesCanceladas;
	}
	public BigDecimal getValorComissoesVencidas() {
		return valorComissoesVencidas;
	}
	public void setValorComissoesVencidas(BigDecimal valorComissoesVencidas) {
		this.valorComissoesVencidas = valorComissoesVencidas;
	}
	public BigDecimal getPercentualPago() {
		return percentualPago;
	}
	public void setPercentualPago(BigDecimal percentualPago) {
		this.percentualPago = percentualPago;
	}
	public BigDecimal getPercentualPendente() {
		return percentualPendente;
	}
	public void setPercentualPendente(BigDecimal percentualPendente) {
		this.percentualPendente = percentualPendente;
	}
	public BigDecimal getPercentualCancelado() {
		return percentualCancelado;
	}
	public void setPercentualCancelado(BigDecimal percentualCancelado) {
		this.percentualCancelado = percentualCancelado;
	}
	public BigDecimal getPercentualVencido() {
		return percentualVencido;
	}
	public void setPercentualVencido(BigDecimal percentualVencido) {
		this.percentualVencido = percentualVencido;
	}
	public Long getTotalPendente() {
		return totalPendente;
	}
	public void setTotalPendente(Long totalPendente) {
		this.totalPendente = totalPendente;
	}
	public Long getTotalPago() {
		return totalPago;
	}
	public void setTotalPago(Long totalPago) {
		this.totalPago = totalPago;
	}
	public Long getTotalCancelado() {
		return totalCancelado;
	}
	public void setTotalCancelado(Long totalCancelado) {
		this.totalCancelado = totalCancelado;
	}
    
    
    
}

