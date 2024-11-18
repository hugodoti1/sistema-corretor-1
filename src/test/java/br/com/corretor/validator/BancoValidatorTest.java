package br.com.corretor.validator;

import br.com.corretor.enums.TipoBanco;
import br.com.corretor.exception.banco.BancoIntegracaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class BancoValidatorTest {

    private BancoValidator validator;

    @BeforeEach
    void setUp() {
        validator = new BancoValidator();
    }

    @Test
    void validarConta_ContaValida_NaoLancaExcecao() {
        assertDoesNotThrow(() -> 
            validator.validarConta("12345-6", TipoBanco.BANCO_DO_BRASIL)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "123", "12345", "123456-", "12345-X", "ABCDE-1"})
    void validarConta_ContaInvalida_LancaExcecao(String conta) {
        BancoIntegracaoException exception = assertThrows(
            BancoIntegracaoException.class,
            () -> validator.validarConta(conta, TipoBanco.BANCO_DO_BRASIL)
        );
        assertEquals("CONTA_INVALIDA", exception.getCodigo());
    }

    @Test
    void validarAgencia_AgenciaValida_NaoLancaExcecao() {
        assertDoesNotThrow(() -> 
            validator.validarAgencia("1234", TipoBanco.BANCO_DO_BRASIL)
        );
        assertDoesNotThrow(() -> 
            validator.validarAgencia("1234-5", TipoBanco.BANCO_DO_BRASIL)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "123", "12345", "1234-", "1234-X", "ABCD-1"})
    void validarAgencia_AgenciaInvalida_LancaExcecao(String agencia) {
        BancoIntegracaoException exception = assertThrows(
            BancoIntegracaoException.class,
            () -> validator.validarAgencia(agencia, TipoBanco.BANCO_DO_BRASIL)
        );
        assertEquals("AGENCIA_INVALIDA", exception.getCodigo());
    }

    @Test
    void validarPeriodoConsulta_PeriodoValido_NaoLancaExcecao() {
        LocalDate hoje = LocalDate.now();
        LocalDate trintaDiasAtras = hoje.minusDays(30);
        
        assertDoesNotThrow(() -> 
            validator.validarPeriodoConsulta(trintaDiasAtras, hoje, TipoBanco.BANCO_DO_BRASIL)
        );
    }

    @Test
    void validarPeriodoConsulta_DataInicioMaiorQueDataFim_LancaExcecao() {
        LocalDate hoje = LocalDate.now();
        LocalDate ontem = hoje.minusDays(1);
        
        BancoIntegracaoException exception = assertThrows(
            BancoIntegracaoException.class,
            () -> validator.validarPeriodoConsulta(hoje, ontem, TipoBanco.BANCO_DO_BRASIL)
        );
        assertEquals("PERIODO_INVALIDO", exception.getCodigo());
    }

    @Test
    void validarPeriodoConsulta_PeriodoMuitoLongo_LancaExcecao() {
        LocalDate hoje = LocalDate.now();
        LocalDate cemDiasAtras = hoje.minusDays(100);
        
        BancoIntegracaoException exception = assertThrows(
            BancoIntegracaoException.class,
            () -> validator.validarPeriodoConsulta(cemDiasAtras, hoje, TipoBanco.BANCO_DO_BRASIL)
        );
        assertEquals("PERIODO_MUITO_LONGO", exception.getCodigo());
    }

    @Test
    void validarWebhookUrl_UrlValida_NaoLancaExcecao() {
        assertDoesNotThrow(() -> 
            validator.validarWebhookUrl("https://exemplo.com/webhook", TipoBanco.BANCO_DO_BRASIL)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "not-a-url", "ftp://exemplo.com"})
    void validarWebhookUrl_UrlInvalida_LancaExcecao(String url) {
        BancoIntegracaoException exception = assertThrows(
            BancoIntegracaoException.class,
            () -> validator.validarWebhookUrl(url, TipoBanco.BANCO_DO_BRASIL)
        );
        assertEquals("WEBHOOK_INVALIDO", exception.getCodigo());
    }

    @Test
    void validarBancos_ListaValida_NaoLancaExcecao() {
        assertDoesNotThrow(() -> 
            validator.validarBancos(Arrays.asList(TipoBanco.BANCO_DO_BRASIL, TipoBanco.BRADESCO))
        );
    }

    @Test
    void validarBancos_ListaVazia_LancaExcecao() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validarBancos(Collections.emptyList())
        );
        assertTrue(exception.getMessage().contains("não pode ser vazia"));
    }

    @Test
    void validarBancos_ListaComNull_LancaExcecao() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validarBancos(Arrays.asList(TipoBanco.BANCO_DO_BRASIL, null))
        );
        assertTrue(exception.getMessage().contains("contém valores nulos"));
    }
}
