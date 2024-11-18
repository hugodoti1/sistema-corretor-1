package br.com.corretor.dto;

import java.util.Set;

public class TokenDTO {
    private String token;
    private String tipo;
    private Long expiracao;
    private String usuario;
    private Set<String> permissoes;

    public TokenDTO() {
    }

    public TokenDTO(String token, String tipo, Long expiracao, String usuario, Set<String> permissoes) {
        this.token = token;
        this.tipo = tipo;
        this.expiracao = expiracao;
        this.usuario = usuario;
        this.permissoes = permissoes;
    }

    public static TokenDTOBuilder builder() {
        return new TokenDTOBuilder();
    }

    public static class TokenDTOBuilder {
        private String token;
        private String tipo;
        private Long expiracao;
        private String usuario;
        private Set<String> permissoes;

        TokenDTOBuilder() {
        }

        public TokenDTOBuilder token(String token) {
            this.token = token;
            return this;
        }

        public TokenDTOBuilder tipo(String tipo) {
            this.tipo = tipo;
            return this;
        }

        public TokenDTOBuilder expiracao(Long expiracao) {
            this.expiracao = expiracao;
            return this;
        }

        public TokenDTOBuilder usuario(String usuario) {
            this.usuario = usuario;
            return this;
        }

        public TokenDTOBuilder permissoes(Set<String> permissoes) {
            this.permissoes = permissoes;
            return this;
        }

        public TokenDTO build() {
            return new TokenDTO(token, tipo, expiracao, usuario, permissoes);
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getExpiracao() {
        return expiracao;
    }

    public void setExpiracao(Long expiracao) {
        this.expiracao = expiracao;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Set<String> getPermissoes() {
        return permissoes;
    }

    public void setPermissoes(Set<String> permissoes) {
        this.permissoes = permissoes;
    }
}
