package br.com.corretor.model;

import br.com.corretor.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transacao extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bancoId;

    @Column(nullable = false)
    private Long empresaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDateTime dataTransacao;

    private String descricao;

    @Column(name = "id_transacao_banco")
    private String idTransacaoBanco;

    @Column(name = "dados_adicionais", length = 4000)
    private String dadosAdicionais;

    @Column(nullable = false)
    private Boolean conciliada;

    @Column(name = "data_conciliacao")
    private LocalDateTime dataConciliacao;
}
