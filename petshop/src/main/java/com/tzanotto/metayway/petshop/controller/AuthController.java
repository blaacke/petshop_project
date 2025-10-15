package com.tzanotto.metayway.petshop.controller;

import com.tzanotto.metayway.petshop.dto.auth.LoginRequest;
import com.tzanotto.metayway.petshop.dto.auth.LoginResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtEncoder jwtEncoder;
    private final long expirationMs;

    public AuthController(AuthenticationManager authManager,
                          JwtEncoder jwtEncoder,
                          @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.authManager = authManager;
        this.jwtEncoder = jwtEncoder;
        this.expirationMs = expirationMs;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        String cpf = req.getCpf() == null ? "" : req.getCpf().replaceAll("\\D", "");
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(cpf, req.getPassword())
            );

            var now = Instant.now();
            var claims = JwtClaimsSet.builder()
                    .subject(auth.getName())
                    .issuedAt(now)
                    .expiresAt(now.plusMillis(expirationMs))
                    .claim("roles", auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority).toList())
                    .build();

            var headers = JwsHeader.with(MacAlgorithm.HS256).build();
            String token = jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
            return ResponseEntity.ok(new LoginResponse(token, expirationMs, "Bearer"));

        } catch (BadCredentialsException | UsernameNotFoundException e) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials " + e.getMessage()));
        } catch (DisabledException e) {
            return ResponseEntity.status(403).body(Map.of("error", "user_disabled"));
        }
    }

}
