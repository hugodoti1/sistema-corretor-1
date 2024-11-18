package br.com.corretor.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "conciliacoes_bancarias")
public class ConciliacaoBancaria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "transacao_bancaria_id", nullable = false)
    private TransacaoBancaria transacaoBancaria;

    @OneToOne
    @JoinColumn(name = "transacao_sistema_id", nullable = false)
    private Transacao transacaoSistema;

    @Column(name = "data_conciliacao", nullable = false)
    private LocalDateTime dataConciliacao;

    @Column(name = "usuario_conciliacao", nullable = false)
    private String usuarioConciliacao;

    @Column(name = "observacoes")
    private String observacoes;
}
