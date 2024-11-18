package br.com.corretor.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transacoes_bancarias")
public class TransacaoBancaria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conta_bancaria_id", nullable = false)
    private ContaBancaria contaBancaria;

    @Column(nullable = false)
    private LocalDateTime data;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @Column(nullable = false)
    private String descricao;

    private String categoria;

    @Column(name = "id_transacao_banco")
    private String idTransacaoBanco;

    private boolean conciliado;

    @OneToOne(mappedBy = "transacaoBancaria")
    private ConciliacaoBancaria conciliacao;

    public enum TipoTransacao {
        CREDITO,
        DEBITO
    }
}
