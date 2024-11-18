package br.com.corretor.exception.banco;

import br.com.corretor.enums.TipoBanco;
import lombok.Getter;

@Getter
public class BancoIntegracaoException extends RuntimeException {
    private final TipoBanco banco;
    private final String codigo;
    private final String detalhes;

    public BancoIntegracaoException(TipoBanco banco, String mensagem, String codigo, String detalhes) {
        super(String.format("Erro na integração com %s: %s (Código: %s)", banco, mensagem, codigo));
        this.banco = banco;
        this.codigo = codigo;
        this.detalhes = detalhes;
    }

    public BancoIntegracaoException(TipoBanco banco, String mensagem, Throwable causa) {
        super(String.format("Erro na integração com %s: %s", banco, mensagem), causa);
        this.banco = banco;
        this.codigo = "ERRO_INTERNO";
        this.detalhes = causa.getMessage();
    }
}
