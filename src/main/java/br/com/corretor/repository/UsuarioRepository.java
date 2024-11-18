package br.com.corretor.repository;

import br.com.corretor.model.Usuario;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UsuarioRepository extends R2dbcRepository<Usuario, Long> {
    Mono<Usuario> findByUsuario(String usuario);
    Mono<Usuario> findByEmail(String email);
    Mono<Boolean> existsByUsuario(String usuario);
    Mono<Boolean> existsByEmail(String email);
    Flux<Usuario> findByEmpresaId(Long empresaId);
    
    @Query("SELECT u.*, p.nome as permissao FROM usuarios u " +
           "LEFT JOIN usuarios_permissoes up ON u.id = up.usuario_id " +
           "LEFT JOIN permissoes p ON up.permissao_id = p.id " +
           "WHERE u.usuario = :usuario")
    Mono<Usuario> findByUsuarioWithPermissoes(String usuario);
}
