package br.com.corretor.exception.handler;

import br.com.corretor.exception.banco.BancoAutenticacaoException;
import br.com.corretor.exception.banco.BancoIntegracaoException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class BancoExceptionHandler {

    @Data
    @AllArgsConstructor
    public static class ErroResponse {
        private LocalDateTime timestamp;
        private String banco;
        private String mensagem;
        private String codigo;
        private String detalhes;
    }

    @ExceptionHandler(BancoIntegracaoException.class)
    public ResponseEntity<ErroResponse> handleBancoIntegracaoException(BancoIntegracaoException ex) {
        ErroResponse erro = new ErroResponse(
            LocalDateTime.now(),
            ex.getBanco().name(),
            ex.getMessage(),
            ex.getCodigo(),
            ex.getDetalhes()
        );

        return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BancoAutenticacaoException.class)
    public ResponseEntity<ErroResponse> handleBancoAutenticacaoException(BancoAutenticacaoException ex) {
        ErroResponse erro = new ErroResponse(
            LocalDateTime.now(),
            ex.getBanco().name(),
            ex.getMessage(),
            ex.getCodigo(),
            ex.getDetalhes()
        );

        return new ResponseEntity<>(erro, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErroResponse erro = new ErroResponse(
            LocalDateTime.now(),
            "SISTEMA",
            ex.getMessage(),
            "PARAMETRO_INVALIDO",
            ex.getMessage()
        );

        return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
    }
}
