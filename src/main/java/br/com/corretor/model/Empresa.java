package br.com.corretor.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "empresas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empresa extends EntidadeAuditavel {
    @Id
    private Long id;
    private String razaoSocial;
    private String cnpj;
    private String nomeFantasia;
    private String inscricaoEstadual;
    private String endereco;
    private String cidade;
    private String estado;
    private String cep;
    private String telefone;
    private String email;
    private LocalDateTime dataCadastro;
    private Boolean ativo;
    
    // Removed the getter and setter methods as they are now handled by Lombok's @Getter and @Setter annotations
}
