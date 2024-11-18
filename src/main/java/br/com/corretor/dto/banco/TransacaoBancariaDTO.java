package br.com.corretor.dto.banco;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransacaoBancariaDTO {
    private String id;
    private LocalDateTime data;
    private String descricao;
    private BigDecimal valor;
    private String tipo; // CREDITO ou DEBITO
    private String documento;
    private String categoria;
    private String status;
    private String idExterno; // ID da transação no banco
}
