package br.com.corretor.exception;

import lombok.Getter;

@Getter
public class BancoIntegracaoException extends RuntimeException {
    private final String codigoErro;
    private final String bancoId;
    private final ErrorType tipoErro;

    public BancoIntegracaoException(String message, String codigoErro, String bancoId, ErrorType tipoErro) {
        super(message);
        this.codigoErro = codigoErro;
        this.bancoId = bancoId;
        this.tipoErro = tipoErro;
    }

    public enum ErrorType {
        AUTENTICACAO,
        COMUNICACAO,
        TIMEOUT,
        DADOS_INVALIDOS,
        CONTA_INVALIDA,
        SALDO_INSUFICIENTE,
        LIMITE_EXCEDIDO,
        HORARIO_NAO_PERMITIDO,
        SERVICO_INDISPONIVEL,
        ERRO_INTERNO
    }
}
