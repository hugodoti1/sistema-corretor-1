package br.com.corretor.exception.banco;

import br.com.corretor.exception.BancoIntegracaoException;
import lombok.Getter;

@Getter
public class BBException extends BancoIntegracaoException {
    private final BBErrorCode bbErrorCode;

    public BBException(String message, BBErrorCode bbErrorCode, String bancoId) {
        super(message, bbErrorCode.getCode(), bancoId, bbErrorCode.getErrorType());
        this.bbErrorCode = bbErrorCode;
    }

    public enum BBErrorCode {
        BB001("BB001", "Erro de autenticação OAuth", ErrorType.AUTENTICACAO),
        BB002("BB002", "Token expirado", ErrorType.AUTENTICACAO),
        BB003("BB003", "Chave J inválida", ErrorType.AUTENTICACAO),
        BB004("BB004", "Conta inválida ou não encontrada", ErrorType.CONTA_INVALIDA),
        BB005("BB005", "Saldo insuficiente para operação", ErrorType.SALDO_INSUFICIENTE),
        BB006("BB006", "Limite diário excedido", ErrorType.LIMITE_EXCEDIDO),
        BB007("BB007", "Horário não permitido para operação", ErrorType.HORARIO_NAO_PERMITIDO),
        BB008("BB008", "Erro de comunicação com servidor", ErrorType.COMUNICACAO),
        BB009("BB009", "Timeout na operação", ErrorType.TIMEOUT),
        BB010("BB010", "Dados da transação inválidos", ErrorType.DADOS_INVALIDOS),
        BB011("BB011", "Serviço temporariamente indisponível", ErrorType.SERVICO_INDISPONIVEL),
        BB999("BB999", "Erro interno do servidor", ErrorType.ERRO_INTERNO);

        private final String code;
        private final String description;
        private final ErrorType errorType;

        BBErrorCode(String code, String description, ErrorType errorType) {
            this.code = code;
            this.description = description;
            this.errorType = errorType;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public ErrorType getErrorType() {
            return errorType;
        }

        public static BBErrorCode fromCode(String code) {
            for (BBErrorCode errorCode : values()) {
                if (errorCode.getCode().equals(code)) {
                    return errorCode;
                }
            }
            return BB999;
        }
    }
}
