package br.com.corretor.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario extends EntidadeAuditavel {
    @Id
    private Long id;
    private Long empresaId;
    private String nome;
    private String email;
    private String usuario;
    private String senha;
    private Boolean ativo;
    private LocalDateTime dataCadastro;
    private Set<String> permissoes;

    public static UsuarioBuilder builder() {
        return new UsuarioBuilder();
    }

    public static class UsuarioBuilder {
        private Long id;
        private Long empresaId;
        private String nome;
        private String email;
        private String usuario;
        private String senha;
        private Boolean ativo;
        private LocalDateTime dataCadastro;
        private Set<String> permissoes;

        UsuarioBuilder() {
        }

        public UsuarioBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UsuarioBuilder empresaId(Long empresaId) {
            this.empresaId = empresaId;
            return this;
        }

        public UsuarioBuilder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public UsuarioBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UsuarioBuilder usuario(String usuario) {
            this.usuario = usuario;
            return this;
        }

        public UsuarioBuilder senha(String senha) {
            this.senha = senha;
            return this;
        }

        public UsuarioBuilder ativo(Boolean ativo) {
            this.ativo = ativo;
            return this;
        }

        public UsuarioBuilder dataCadastro(LocalDateTime dataCadastro) {
            this.dataCadastro = dataCadastro;
            return this;
        }

        public UsuarioBuilder permissoes(Set<String> permissoes) {
            this.permissoes = permissoes;
            return this;
        }

        public Usuario build() {
            return new Usuario(id, empresaId, nome, email, usuario, senha, ativo, dataCadastro, permissoes);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Set<String> getPermissoes() {
        return permissoes;
    }

    public void setPermissoes(Set<String> permissoes) {
        this.permissoes = permissoes;
    }
}
