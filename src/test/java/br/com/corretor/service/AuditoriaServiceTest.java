package br.com.corretor.service;

import br.com.corretor.model.LogAuditoria;
import br.com.corretor.repository.LogAuditoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuditoriaServiceTest {

    @Autowired
    private AuditoriaService auditoriaService;

    @MockBean
    private LogAuditoriaRepository logAuditoriaRepository;

    @Captor
    private ArgumentCaptor<LogAuditoria> logCaptor;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        when(logAuditoriaRepository.save(any(LogAuditoria.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void registrarAcao_ComUsuarioAutenticado_DeveRegistrarLogComUsuario() {
        // Arrange
        String username = "usuario.teste";
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(username, null)
        );

        // Act
        auditoriaService.registrarAcao("CLIENTE", "CADASTRO", "Novo cliente cadastrado");

        // Assert
        verify(logAuditoriaRepository).save(logCaptor.capture());
        LogAuditoria log = logCaptor.getValue();
        
        assertEquals("CLIENTE", log.getEntidade());
        assertEquals("CADASTRO", log.getAcao());
        assertEquals(username, log.getUsuario());
        assertNotNull(log.getDataHora());
    }

    @Test
    void registrarAcao_SemUsuarioAutenticado_DeveRegistrarLogComoSistema() {
        // Act
        auditoriaService.registrarAcao("BANCO", "INTEGRACAO", "Sincronização automática");

        // Assert
        verify(logAuditoriaRepository).save(logCaptor.capture());
        LogAuditoria log = logCaptor.getValue();
        
        assertEquals("BANCO", log.getEntidade());
        assertEquals("INTEGRACAO", log.getAcao());
        assertEquals("SISTEMA", log.getUsuario());
    }

    @Test
    void registrarErro_DeveRegistrarLogComStackTrace() {
        // Arrange
        Exception erro = new RuntimeException("Erro de teste");

        // Act
        auditoriaService.registrarErro("TesteService", erro.getMessage(), erro.getStackTrace().toString());

        // Assert
        verify(logAuditoriaRepository).save(logCaptor.capture());
        LogAuditoria log = logCaptor.getValue();
        
        assertEquals("ERRO", log.getEntidade());
        assertEquals("ERRO_SISTEMA", log.getAcao());
        assertTrue(log.getDetalhes().contains("Erro de teste"));
        assertTrue(log.getDetalhes().contains("TesteService"));
    }

    @Test
    void registrarAcessoNegado_DeveRegistrarLogComDetalhes() {
        // Act
        auditoriaService.registrarAcessoNegado("/api/restrita", "Usuário sem permissão");

        // Assert
        verify(logAuditoriaRepository).save(logCaptor.capture());
        LogAuditoria log = logCaptor.getValue();
        
        assertEquals("SEGURANCA", log.getEntidade());
        assertEquals("ACESSO_NEGADO", log.getAcao());
        assertTrue(log.getDetalhes().contains("/api/restrita"));
        assertTrue(log.getDetalhes().contains("Usuário sem permissão"));
    }

    @Test
    void registrarAutenticacao_LoginSucesso_DeveRegistrarLog() {
        // Act
        auditoriaService.registrarAutenticacao("usuario.teste", true, "Login bem-sucedido");

        // Assert
        verify(logAuditoriaRepository).save(logCaptor.capture());
        LogAuditoria log = logCaptor.getValue();
        
        assertEquals("AUTENTICACAO", log.getEntidade());
        assertEquals("LOGIN_SUCESSO", log.getAcao());
        assertEquals("usuario.teste", log.getUsuario());
    }

    @Test
    void registrarAutenticacao_LoginFalha_DeveRegistrarLog() {
        // Act
        auditoriaService.registrarAutenticacao("usuario.inexistente", false, "Credenciais inválidas");

        // Assert
        verify(logAuditoriaRepository).save(logCaptor.capture());
        LogAuditoria log = logCaptor.getValue();
        
        assertEquals("AUTENTICACAO", log.getEntidade());
        assertEquals("LOGIN_FALHA", log.getAcao());
        assertEquals("usuario.inexistente", log.getUsuario());
        assertTrue(log.getDetalhes().contains("Credenciais inválidas"));
    }
}
