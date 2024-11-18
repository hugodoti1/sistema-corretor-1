package br.com.corretor.exception;

import br.com.corretor.enums.BankErrorCode;
import lombok.Getter;

@Getter
public class BankIntegrationException extends RuntimeException {
    private final BankErrorCode errorCode;
    private final String bank;
    private final String details;

    public BankIntegrationException(BankErrorCode errorCode, String bank) {
        super(errorCode.getFullMessage());
        this.errorCode = errorCode;
        this.bank = bank;
        this.details = null;
    }

    public BankIntegrationException(BankErrorCode errorCode, String bank, String details) {
        super(errorCode.getFullMessage());
        this.errorCode = errorCode;
        this.bank = bank;
        this.details = details;
    }

    public BankIntegrationException(BankErrorCode errorCode, String bank, Throwable cause) {
        super(errorCode.getFullMessage(), cause);
        this.errorCode = errorCode;
        this.bank = bank;
        this.details = cause.getMessage();
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder();
        message.append(String.format("[%s] %s", bank, errorCode.getFullMessage()));
        if (details != null) {
            message.append(String.format(" - Detalhes: %s", details));
        }
        return message.toString();
    }
}
