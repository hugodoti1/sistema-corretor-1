package br.com.corretor.model;

import br.com.corretor.enums.TipoBanco;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bancos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banco extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long empresaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoBanco tipo;

    @Column(nullable = false)
    private String agencia;

    @Column(nullable = false)
    private String conta;

    private String chaveApi;
    
    private String tokenAcesso;
    
    @Column(nullable = false)
    private Boolean ativo;

    @Column(name = "webhook_url")
    private String webhookUrl;
    
    @Column(name = "webhook_secret")
    private String webhookSecret;
}
