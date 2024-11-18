package br.com.corretor.controller;

import br.com.corretor.dto.LoginDTO;
import br.com.corretor.dto.TokenDTO;
import br.com.corretor.dto.UsuarioDTO;
import br.com.corretor.model.Usuario;
import br.com.corretor.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
	
	@Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Mono<ResponseEntity<TokenDTO>> login(@Valid @RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping("/registrar")
    public Mono<ResponseEntity<Usuario>> registrar(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        return authService.registrar(usuarioDTO)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @GetMapping("/validar")
    public Mono<ResponseEntity<Void>> validarToken(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        return authService.validarToken(token)
            .then(Mono.just(ResponseEntity.ok().<Void>build()))
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().<Void>build()));
    }
}
