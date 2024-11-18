package br.com.corretor.enums;

import lombok.Getter;

@Getter
public enum BankErrorCode {
    // Erros Gerais
    UNKNOWN_ERROR("ERR-GEN-001", "Erro desconhecido"),
    INVALID_CREDENTIALS("ERR-GEN-002", "Credenciais inválidas"),
    CONNECTION_ERROR("ERR-GEN-003", "Erro de conexão com o banco"),
    TIMEOUT_ERROR("ERR-GEN-004", "Tempo limite de conexão excedido"),
    INVALID_REQUEST("ERR-GEN-005", "Requisição inválida"),
    UNAUTHORIZED("ERR-GEN-006", "Não autorizado"),
    SERVICE_UNAVAILABLE("ERR-GEN-007", "Serviço indisponível"),
    RATE_LIMIT_EXCEEDED("ERR-GEN-008", "Limite de requisições excedido"),
    
    // Erros de Autenticação
    AUTH_TOKEN_EXPIRED("ERR-AUTH-001", "Token de autenticação expirado"),
    AUTH_INVALID_CERTIFICATE("ERR-AUTH-002", "Certificado digital inválido"),
    AUTH_CERTIFICATE_EXPIRED("ERR-AUTH-003", "Certificado digital expirado"),
    AUTH_INVALID_SCOPE("ERR-AUTH-004", "Escopo de autorização inválido"),
    AUTH_MISSING_PERMISSIONS("ERR-AUTH-005", "Permissões insuficientes"),
    
    // Erros de Conta
    ACCOUNT_NOT_FOUND("ERR-ACC-001", "Conta não encontrada"),
    ACCOUNT_INACTIVE("ERR-ACC-002", "Conta inativa"),
    ACCOUNT_BLOCKED("ERR-ACC-003", "Conta bloqueada"),
    ACCOUNT_INVALID_TYPE("ERR-ACC-004", "Tipo de conta inválido"),
    ACCOUNT_LIMIT_EXCEEDED("ERR-ACC-005", "Limite da conta excedido"),
    
    // Erros de Transação
    TRANSACTION_NOT_FOUND("ERR-TRX-001", "Transação não encontrada"),
    TRANSACTION_ALREADY_PROCESSED("ERR-TRX-002", "Transação já processada"),
    TRANSACTION_EXPIRED("ERR-TRX-003", "Transação expirada"),
    TRANSACTION_INVALID_AMOUNT("ERR-TRX-004", "Valor da transação inválido"),
    TRANSACTION_INSUFFICIENT_FUNDS("ERR-TRX-005", "Saldo insuficiente"),
    TRANSACTION_INVALID_TYPE("ERR-TRX-006", "Tipo de transação inválido"),
    TRANSACTION_LIMIT_EXCEEDED("ERR-TRX-007", "Limite de transação excedido"),
    
    // Erros de Webhook
    WEBHOOK_REGISTRATION_FAILED("ERR-WH-001", "Falha no registro do webhook"),
    WEBHOOK_INVALID_URL("ERR-WH-002", "URL do webhook inválida"),
    WEBHOOK_NOT_FOUND("ERR-WH-003", "Webhook não encontrado"),
    
    // Erros Específicos do Banco do Brasil
    BB_INVALID_BRANCH("ERR-BB-001", "Agência inválida"),
    BB_INVALID_ACCOUNT("ERR-BB-002", "Conta inválida"),
    BB_SERVICE_OFFLINE("ERR-BB-003", "Serviço do BB indisponível"),
    BB_INVALID_AGREEMENT("ERR-BB-004", "Convênio inválido"),
    
    // Erros Específicos do Banco Inter
    INTER_INVALID_CERTIFICATE("ERR-INT-001", "Certificado Inter inválido"),
    INTER_API_ERROR("ERR-INT-002", "Erro na API do Inter"),
    INTER_INVALID_SCOPE("ERR-INT-003", "Escopo Inter inválido"),
    
    // Erros Específicos da Caixa
    CAIXA_INVALID_OPERATION("ERR-CX-001", "Operação Caixa inválida"),
    CAIXA_SERVICE_ERROR("ERR-CX-002", "Erro no serviço da Caixa"),
    CAIXA_INVALID_PARAMETER("ERR-CX-003", "Parâmetro Caixa inválido");

    private final String code;
    private final String message;

    BankErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static BankErrorCode fromCode(String code) {
        for (BankErrorCode error : BankErrorCode.values()) {
            if (error.getCode().equals(code)) {
                return error;
            }
        }
        return UNKNOWN_ERROR;
    }

    public String getFullMessage() {
        return String.format("[%s] %s", code, message);
    }
}
