package br.com.corretor.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "conciliacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conciliacao extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long empresaId;

    @Column(nullable = false)
    private Long bancoId;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDateTime dataFim;

    @Column(nullable = false)
    private Boolean concluida;

    @Column(name = "total_transacoes")
    private Integer totalTransacoes;

    @Column(name = "transacoes_conciliadas")
    private Integer transacoesConciliadas;

    @Column(name = "transacoes_pendentes")
    private Integer transacoesPendentes;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(length = 4000)
    private String observacoes;

    @Column(name = "arquivo_relatorio")
    private String arquivoRelatorio;
}
