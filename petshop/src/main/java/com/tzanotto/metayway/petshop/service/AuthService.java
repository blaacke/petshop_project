package com.tzanotto.metayway.petshop.service;

import com.tzanotto.metayway.petshop.dto.auth.LoginRequest;
import com.tzanotto.metayway.petshop.dto.auth.LoginResponse;
import com.tzanotto.metayway.petshop.model.Usuario;
import com.tzanotto.metayway.petshop.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtEncoder jwtEncoder;
    private final UsuarioRepository usuarioRepository;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    public LoginResponse login(LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getCpf(), req.getPassword())
        );

        UserDetails principal = (UserDetails) auth.getPrincipal();

        Usuario u = usuarioRepository.findByCpf(req.getCpf()).orElse(null);

        if (u == null || !u.getSituacao().isAtivo()) {
            throw new BadCredentialsException("Usuário inativo/bloqueado.");
        }

        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .subject(principal.getUsername())
                .issuedAt(now)
                .expiresAt(now.plusMillis(expirationMs))
                .claims(_getClaims(u))
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return LoginResponse.builder()
                .token(token)
                .expiresIn(expirationMs)
                .tokenType("Bearer")
                .build();
    }

    private Consumer<Map<String, Object>> _getClaims(Usuario u) {
        return claims -> {
            if (u == null) return;
            claims.put("perfil",   u.getPerfil().name());
            claims.put("situacao", u.getSituacao().name());
            claims.put("cpf",  u.getCpf());
            claims.put("nome", u.getNome());
            claims.put("id", u.getId());
        };
    }
}
