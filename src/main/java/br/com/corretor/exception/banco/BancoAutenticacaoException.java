package br.com.corretor.exception.banco;

import br.com.corretor.enums.TipoBanco;

public class BancoAutenticacaoException extends BancoIntegracaoException {
    public BancoAutenticacaoException(TipoBanco banco, String mensagem, String codigo, String detalhes) {
        super(banco, "Erro de autenticação: " + mensagem, codigo, detalhes);
    }

    public BancoAutenticacaoException(TipoBanco banco, String mensagem, Throwable causa) {
        super(banco, "Erro de autenticação: " + mensagem, causa);
    }
}
