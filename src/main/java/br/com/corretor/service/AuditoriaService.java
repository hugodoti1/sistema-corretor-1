package br.com.corretor.service;

import br.com.corretor.model.LogAuditoria;
import br.com.corretor.repository.LogAuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final LogAuditoriaRepository logAuditoriaRepository;

    public void registrarAcao(String entidade, String acao, String detalhes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuario = (auth != null && auth.isAuthenticated()) ? auth.getName() : "SISTEMA";

        LogAuditoria log = LogAuditoria.builder()
                .entidade(entidade)
                .acao(acao)
                .detalhes(detalhes)
                .usuario(usuario)
                .dataHora(LocalDateTime.now())
                .build();

        logAuditoriaRepository.save(log);
    }

    public void registrarAcessoNegado(String recurso, String detalhes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuario = (auth != null) ? auth.getName() : "ANONIMO";

        LogAuditoria log = LogAuditoria.builder()
                .entidade("SEGURANCA")
                .acao("ACESSO_NEGADO")
                .detalhes(String.format("Recurso: %s, Detalhes: %s", recurso, detalhes))
                .usuario(usuario)
                .dataHora(LocalDateTime.now())
                .build();

        logAuditoriaRepository.save(log);
    }

    public void registrarErro(String origem, String erro, String stackTrace) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuario = (auth != null && auth.isAuthenticated()) ? auth.getName() : "SISTEMA";

        LogAuditoria log = LogAuditoria.builder()
                .entidade("ERRO")
                .acao("ERRO_SISTEMA")
                .detalhes(String.format("Origem: %s, Erro: %s, Stack: %s", origem, erro, stackTrace))
                .usuario(usuario)
                .dataHora(LocalDateTime.now())
                .build();

        logAuditoriaRepository.save(log);
    }

    public void registrarAutenticacao(String usuario, boolean sucesso, String detalhes) {
        LogAuditoria log = LogAuditoria.builder()
                .entidade("AUTENTICACAO")
                .acao(sucesso ? "LOGIN_SUCESSO" : "LOGIN_FALHA")
                .detalhes(detalhes)
                .usuario(usuario)
                .dataHora(LocalDateTime.now())
                .build();

        logAuditoriaRepository.save(log);
    }
}
