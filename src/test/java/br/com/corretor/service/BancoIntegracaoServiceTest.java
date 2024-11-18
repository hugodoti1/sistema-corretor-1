package br.com.corretor.service;

import br.com.corretor.exception.banco.BBException;
import br.com.corretor.exception.banco.ItauException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BancoIntegracaoServiceTest {

    @InjectMocks
    private BancoIntegracaoService bancoIntegracaoService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void deveGerarErroBBQuandoContaInvalida() {
        BBException exception = assertThrows(BBException.class, () -> 
            bancoIntegracaoService.processarTransacao("001", "", 100.0)
        );
        
        assertEquals(BBException.BBErrorCode.BB004, exception.getBbErrorCode());
        assertEquals("001", exception.getBancoId());
    }

    @Test
    void deveGerarErroBBQuandoValorInvalido() {
        BBException exception = assertThrows(BBException.class, () -> 
            bancoIntegracaoService.processarTransacao("001", "12345", -100.0)
        );
        
        assertEquals(BBException.BBErrorCode.BB010, exception.getBbErrorCode());
        assertEquals("001", exception.getBancoId());
    }

    @Test
    void deveGerarErroBBQuandoTimeout() {
        BBException exception = assertThrows(BBException.class, () -> 
            bancoIntegracaoService.processarTransacao("001", "12345", 200000.0)
        );
        
        assertEquals(BBException.BBErrorCode.BB009, exception.getBbErrorCode());
        assertEquals("001", exception.getBancoId());
    }

    @Test
    void deveGerarErroItauQuandoContaInvalida() {
        ItauException exception = assertThrows(ItauException.class, () -> 
            bancoIntegracaoService.processarTransacao("341", "", 100.0)
        );
        
        assertEquals(ItauException.ItauErrorCode.ITAU004, exception.getItauErrorCode());
        assertEquals("341", exception.getBancoId());
    }

    @Test
    void deveGerarErroItauQuandoValorInvalido() {
        ItauException exception = assertThrows(ItauException.class, () -> 
            bancoIntegracaoService.processarTransacao("341", "12345", -100.0)
        );
        
        assertEquals(ItauException.ItauErrorCode.ITAU010, exception.getItauErrorCode());
        assertEquals("341", exception.getBancoId());
    }

    @Test
    void deveGerarErroItauQuandoCertificadoExpirado() {
        ItauException exception = assertThrows(ItauException.class, () -> 
            bancoIntegracaoService.processarTransacao("341", "12345", 200000.0)
        );
        
        assertEquals(ItauException.ItauErrorCode.ITAU002, exception.getItauErrorCode());
        assertEquals("341", exception.getBancoId());
    }

    @Test
    void deveGerarErroQuandoBancoNaoSuportado() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            bancoIntegracaoService.processarTransacao("999", "12345", 100.0)
        );
        
        assertTrue(exception.getMessage().contains("Banco não suportado"));
    }
}
