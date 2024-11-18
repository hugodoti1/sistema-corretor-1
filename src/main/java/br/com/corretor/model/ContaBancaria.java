package br.com.corretor.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "contas_bancarias")
public class ContaBancaria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String banco;

    @Column(nullable = false)
    private String agencia;

    @Column(nullable = false)
    private String conta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConta tipo;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Column(name = "ultima_sincronizacao")
    private LocalDateTime ultimaSincronizacao;

    @Column(name = "token_acesso")
    private String tokenAcesso;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "token_expiracao")
    private LocalDateTime tokenExpiracao;

    public enum TipoConta {
        CORRENTE,
        POUPANCA
    }
}
