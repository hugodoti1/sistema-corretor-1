package br.com.corretor.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComissaoPagamentoDTO {
    @NotNull(message = "Data de pagamento é obrigatória")
    private LocalDate dataPagamento;
    
    @NotNull(message = "Valor pago é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor pago deve ser maior que zero")
    private BigDecimal valorPago;
    
    @NotNull(message = "Forma de pagamento é obrigatória")
    @Pattern(regexp = "^(BOLETO|CHEQUE|PIX|TRANSFERENCIA|CARTAO)$", 
            message = "Forma de pagamento deve ser: BOLETO, CHEQUE, PIX, TRANSFERENCIA ou CARTAO")
    private String formaPagamento;
    
    private String observacoes;

	public LocalDate getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(LocalDate dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public BigDecimal getValorPago() {
		return valorPago;
	}

	public void setValorPago(BigDecimal valorPago) {
		this.valorPago = valorPago;
	}

	public String getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(String formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}
    
    
}
