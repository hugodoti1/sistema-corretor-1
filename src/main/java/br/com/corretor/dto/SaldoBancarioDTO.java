package br.com.corretor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaldoBancarioDTO {
    private BigDecimal saldoDisponivel;
    private BigDecimal saldoTotal;
    private BigDecimal limiteCreditoDisponivel;
    private LocalDateTime dataConsulta;
    private String moeda;
}
