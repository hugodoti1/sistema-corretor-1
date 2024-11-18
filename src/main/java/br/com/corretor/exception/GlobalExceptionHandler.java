package br.com.corretor.exception;

import br.com.corretor.exception.banco.BBException;
import br.com.corretor.exception.banco.ItauException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return Mono.just(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        ErrorResponse response = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return Mono.just(response);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ValidationErrorResponse> handleValidationException(WebExchangeBindException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage(),
                        (error1, error2) -> error1
                ));

        ValidationErrorResponse response = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                LocalDateTime.now(),
                errors
        );
        return Mono.just(response);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Mono<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse response = new ErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return Mono.just(response);
    }

    @ExceptionHandler(BancoIntegracaoException.class)
    public ResponseEntity<ErrorResponse> handleBancoIntegracaoException(BancoIntegracaoException ex) {
        HttpStatus status = mapErrorTypeToStatus(ex.getTipoErro());
        
        ErrorResponse errorResponse = new ErrorResponse(
            status.value(),
            ex.getMessage(),
            ex.getCodigoErro(),
            ex.getBancoId(),
            ex.getTipoErro().toString(),
            LocalDateTime.now()
        );
        
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(BBException.class)
    public ResponseEntity<ErrorResponse> handleBBException(BBException ex) {
        HttpStatus status = mapErrorTypeToStatus(ex.getTipoErro());
        
        ErrorResponse errorResponse = new ErrorResponse(
            status.value(),
            ex.getMessage(),
            ex.getBbErrorCode().getCode(),
            ex.getBancoId(),
            ex.getBbErrorCode().getDescription(),
            LocalDateTime.now()
        );
        
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(ItauException.class)
    public ResponseEntity<ErrorResponse> handleItauException(ItauException ex) {
        HttpStatus status = mapErrorTypeToStatus(ex.getTipoErro());
        
        ErrorResponse errorResponse = new ErrorResponse(
            status.value(),
            ex.getMessage(),
            ex.getItauErrorCode().getCode(),
            ex.getBancoId(),
            ex.getItauErrorCode().getDescription(),
            LocalDateTime.now()
        );
        
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse response = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro interno do servidor",
            LocalDateTime.now()
        );
        return Mono.just(response);
    }

    private HttpStatus mapErrorTypeToStatus(BancoIntegracaoException.ErrorType errorType) {
        return switch (errorType) {
            case AUTENTICACAO -> HttpStatus.UNAUTHORIZED;
            case COMUNICACAO, TIMEOUT -> HttpStatus.SERVICE_UNAVAILABLE;
            case DADOS_INVALIDOS, CONTA_INVALIDA -> HttpStatus.BAD_REQUEST;
            case SALDO_INSUFICIENTE, LIMITE_EXCEDIDO -> HttpStatus.FORBIDDEN;
            case HORARIO_NAO_PERMITIDO -> HttpStatus.FORBIDDEN;
            case SERVICO_INDISPONIVEL -> HttpStatus.SERVICE_UNAVAILABLE;
            case ERRO_INTERNO -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private int status;
        private String message;
        private String errorCode;
        private String bancoId;
        private String errorDescription;
        private LocalDateTime timestamp;
    }
}
