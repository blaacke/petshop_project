package com.tzanotto.metayway.petshop.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminApprovalService {
    private final JwtEncoder jwtEncoder;

    @Value("${approval.hours}")
    private long ttlHours;

    public String gerarTokenAprovacao(String cpf) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .subject(cpf)
                .issuedAt(now)
                .expiresAt(now.plus(ttlHours, ChronoUnit.HOURS))
                .claim("kind", "admin-approval")
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
