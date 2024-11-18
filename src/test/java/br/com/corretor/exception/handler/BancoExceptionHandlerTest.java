package br.com.corretor.exception.handler;

import br.com.corretor.enums.TipoBanco;
import br.com.corretor.exception.banco.BancoAutenticacaoException;
import br.com.corretor.exception.banco.BancoIntegracaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class BancoExceptionHandlerTest {

    private BancoExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new BancoExceptionHandler();
    }

    @Test
    void handleBancoIntegracaoException_RetornaBadRequest() {
        BancoIntegracaoException exception = new BancoIntegracaoException(
            TipoBanco.BANCO_DO_BRASIL,
            "Erro de teste",
            "TESTE_001",
            "Detalhes do erro de teste"
        );

        ResponseEntity<BancoExceptionHandler.ErroResponse> response = 
            exceptionHandler.handleBancoIntegracaoException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TipoBanco.BANCO_DO_BRASIL.name(), response.getBody().getBanco());
        assertEquals("TESTE_001", response.getBody().getCodigo());
        assertEquals("Detalhes do erro de teste", response.getBody().getDetalhes());
    }

    @Test
    void handleBancoAutenticacaoException_RetornaUnauthorized() {
        BancoAutenticacaoException exception = new BancoAutenticacaoException(
            TipoBanco.BANCO_DO_BRASIL,
            "Erro de autenticação",
            "AUTH_001",
            "Credenciais inválidas"
        );

        ResponseEntity<BancoExceptionHandler.ErroResponse> response = 
            exceptionHandler.handleBancoAutenticacaoException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TipoBanco.BANCO_DO_BRASIL.name(), response.getBody().getBanco());
        assertEquals("AUTH_001", response.getBody().getCodigo());
        assertTrue(response.getBody().getMensagem().contains("Erro de autenticação"));
    }

    @Test
    void handleIllegalArgumentException_RetornaBadRequest() {
        IllegalArgumentException exception = new IllegalArgumentException("Parâmetro inválido");

        ResponseEntity<BancoExceptionHandler.ErroResponse> response = 
            exceptionHandler.handleIllegalArgumentException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("SISTEMA", response.getBody().getBanco());
        assertEquals("PARAMETRO_INVALIDO", response.getBody().getCodigo());
        assertEquals("Parâmetro inválido", response.getBody().getMensagem());
    }

    @Test
    void erroResponse_ContemTimestamp() {
        BancoIntegracaoException exception = new BancoIntegracaoException(
            TipoBanco.BANCO_DO_BRASIL,
            "Erro de teste",
            "TESTE_001",
            "Detalhes do erro de teste"
        );

        ResponseEntity<BancoExceptionHandler.ErroResponse> response = 
            exceptionHandler.handleBancoIntegracaoException(exception);

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleBancoIntegracaoException_ComDetalhesNulos_RetornaBadRequest() {
        BancoIntegracaoException exception = new BancoIntegracaoException(
            TipoBanco.BANCO_DO_BRASIL,
            "Erro de teste",
            null,
            null
        );

        ResponseEntity<BancoExceptionHandler.ErroResponse> response = 
            exceptionHandler.handleBancoIntegracaoException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TipoBanco.BANCO_DO_BRASIL.name(), response.getBody().getBanco());
        assertNotNull(response.getBody().getCodigo());
        assertNotNull(response.getBody().getMensagem());
    }

    @Test
    void handleBancoAutenticacaoException_ComMensagemLonga_RetornaUnauthorized() {
        String mensagemLonga = "a".repeat(1000);
        BancoAutenticacaoException exception = new BancoAutenticacaoException(
            TipoBanco.BANCO_DO_BRASIL,
            mensagemLonga,
            "AUTH_002",
            "Detalhes do erro"
        );

        ResponseEntity<BancoExceptionHandler.ErroResponse> response = 
            exceptionHandler.handleBancoAutenticacaoException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMensagem().length() <= 500);
    }

    @Test
    void handleBancoIntegracaoException_ComCaracteresEspeciais_RetornaBadRequest() {
        BancoIntegracaoException exception = new BancoIntegracaoException(
            TipoBanco.BANCO_DO_BRASIL,
            "Erro com caracteres especiais: !@#$%¨&*()",
            "TESTE_002",
            "Detalhes com acentuação: áéíóú"
        );

        ResponseEntity<BancoExceptionHandler.ErroResponse> response = 
            exceptionHandler.handleBancoIntegracaoException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMensagem().contains("!@#$%¨&*()"));
        assertTrue(response.getBody().getDetalhes().contains("áéíóú"));
    }

    @Test
    void handleMultipleExceptions_VerificaConsistencia() {
        BancoIntegracaoException exception1 = new BancoIntegracaoException(
            TipoBanco.BANCO_DO_BRASIL,
            "Primeiro erro",
            "ERR_001",
            "Detalhes do primeiro erro"
        );

        BancoIntegracaoException exception2 = new BancoIntegracaoException(
            TipoBanco.BANCO_DO_BRASIL,
            "Segundo erro",
            "ERR_002",
            "Detalhes do segundo erro"
        );

        ResponseEntity<BancoExceptionHandler.ErroResponse> response1 = 
            exceptionHandler.handleBancoIntegracaoException(exception1);
        ResponseEntity<BancoExceptionHandler.ErroResponse> response2 = 
            exceptionHandler.handleBancoIntegracaoException(exception2);

        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        assertNotEquals(response1.getBody().getTimestamp(), response2.getBody().getTimestamp());
    }

}
