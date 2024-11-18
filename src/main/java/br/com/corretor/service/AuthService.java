package br.com.corretor.service;

import br.com.corretor.dto.LoginDTO;
import br.com.corretor.dto.TokenDTO;
import br.com.corretor.dto.UsuarioDTO;
import br.com.corretor.model.Usuario;
import br.com.corretor.repository.UsuarioRepository;
import br.com.corretor.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class AuthService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;

    public Mono<TokenDTO> login(LoginDTO loginDTO) {
        return usuarioRepository.findByUsuarioWithPermissoes(loginDTO.getUsuario())
            .filter(usuario -> passwordEncoder.matches(loginDTO.getSenha(), usuario.getSenha()))
            .filter(Usuario::getAtivo)
            .<TokenDTO>map(usuario -> {
                return TokenDTO.builder()
                    .token(jwtService.generateToken(usuario.getUsuario(), usuario.getPermissoes()))
                    .tipo("Bearer")
                    .expiracao(System.currentTimeMillis() + 86400000) // 24 horas
                    .usuario(usuario.getUsuario())
                    .permissoes(usuario.getPermissoes())
                    .build();
            })
            .switchIfEmpty(Mono.error(new RuntimeException("Usuário ou senha inválidos")));
    }

    public Mono<Usuario> registrar(UsuarioDTO usuarioDTO) {
        return Mono.just(usuarioDTO)
            .filterWhen(dto -> usuarioRepository.existsByUsuario(dto.getUsuario()).map(exists -> !exists))
            .switchIfEmpty(Mono.error(new RuntimeException("Usuário já existe")))
            .filterWhen(dto -> usuarioRepository.existsByEmail(dto.getEmail()).map(exists -> !exists))
            .switchIfEmpty(Mono.error(new RuntimeException("Email já cadastrado")))
            .<Usuario>map(dto -> {
                return Usuario.builder()
                    .empresaId(dto.getEmpresaId())
                    .nome(dto.getNome())
                    .email(dto.getEmail())
                    .usuario(dto.getUsuario())
                    .senha(passwordEncoder.encode(dto.getSenha()))
                    .ativo(true)
                    .dataCadastro(LocalDateTime.now())
                    .permissoes(new HashSet<>(dto.getPermissoes()))
                    .build();
            })
            .flatMap(usuarioRepository::save);
    }

    public Mono<Void> validarToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            return usuarioRepository.findByUsuario(username)
                .filter(usuario -> jwtService.validateToken(token, usuario.getUsuario()))
                .switchIfEmpty(Mono.error(new RuntimeException("Token inválido")))
                .then();
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Token inválido"));
        }
    }

    public Mono<UserDetails> buscarUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsuarioWithPermissoes(username)
            .filter(Usuario::getAtivo)
            .<UserDetails>map(usuario -> new User(
                usuario.getUsuario(),
                usuario.getSenha(),
                usuario.getPermissoes().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList())
            ))
            .switchIfEmpty(Mono.error(new RuntimeException("Usuário não encontrado")));
    }
}
