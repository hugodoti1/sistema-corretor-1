package br.com.corretor.exception.banco;

import br.com.corretor.exception.BancoIntegracaoException;
import lombok.Getter;

@Getter
public class ItauException extends BancoIntegracaoException {
    private final ItauErrorCode itauErrorCode;

    public ItauException(String message, ItauErrorCode itauErrorCode, String bancoId) {
        super(message, itauErrorCode.getCode(), bancoId, itauErrorCode.getErrorType());
        this.itauErrorCode = itauErrorCode;
    }

    public enum ItauErrorCode {
        ITAU001("ITAU001", "Falha na autenticação do certificado", ErrorType.AUTENTICACAO),
        ITAU002("ITAU002", "Certificado digital expirado", ErrorType.AUTENTICACAO),
        ITAU003("ITAU003", "Chave de acesso inválida", ErrorType.AUTENTICACAO),
        ITAU004("ITAU004", "Agência/conta não encontrada", ErrorType.CONTA_INVALIDA),
        ITAU005("ITAU005", "Saldo insuficiente", ErrorType.SALDO_INSUFICIENTE),
        ITAU006("ITAU006", "Limite de transações excedido", ErrorType.LIMITE_EXCEDIDO),
        ITAU007("ITAU007", "Operação fora do horário permitido", ErrorType.HORARIO_NAO_PERMITIDO),
        ITAU008("ITAU008", "Falha de comunicação", ErrorType.COMUNICACAO),
        ITAU009("ITAU009", "Timeout na requisição", ErrorType.TIMEOUT),
        ITAU010("ITAU010", "Dados inválidos na requisição", ErrorType.DADOS_INVALIDOS),
        ITAU011("ITAU011", "Serviço em manutenção", ErrorType.SERVICO_INDISPONIVEL),
        ITAU999("ITAU999", "Erro interno", ErrorType.ERRO_INTERNO);

        private final String code;
        private final String description;
        private final ErrorType errorType;

        ItauErrorCode(String code, String description, ErrorType errorType) {
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

        public static ItauErrorCode fromCode(String code) {
            for (ItauErrorCode errorCode : values()) {
                if (errorCode.getCode().equals(code)) {
                    return errorCode;
                }
            }
            return ITAU999;
        }
    }
}
