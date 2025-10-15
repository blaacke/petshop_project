package com.tzanotto.metayway.petshop.controller;

import com.tzanotto.metayway.petshop.dto.auth.DecideRequest;
import com.tzanotto.metayway.petshop.enums.SituacaoEnum;
import com.tzanotto.metayway.petshop.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public/admin-approval")
@RequiredArgsConstructor
public class AdminApprovalController {
    private final JwtDecoder jwtDecoder;
    private final UsuarioRepository usuarioRepo;

    @Value("${approval.secret}")
    private String adminApprovalSecret;

    @Operation(summary = "Decide se um usuário vai ser aceito como admin", description = "Através de um body autenticado, decide se o usuario vai ser aceito para ser admin. É necessário que a senha de confirmação seja a mesma que a apontada no .env")
    @ApiResponse(responseCode = "200", description = "usuário aprovado,negado ou que já foi avaliado",
            content = @Content(schema = @Schema(implementation = DecideRequest.class)))
    @ApiResponse(responseCode = "401", description = "senha inválida")
    @ApiResponse(responseCode = "400", description = "token inválido")
    @PostMapping
    @Transactional
    public ResponseEntity<?> decidir(@RequestBody DecideRequest body) {
        if (!constantTimeEquals(body.secret(), adminApprovalSecret)) {
            return ResponseEntity.status(401).body(Map.of("message","Senha inválida"));
        }

        Jwt jwt = jwtDecoder.decode(body.token());
        if (!"admin-approval".equals(jwt.getClaimAsString("kind"))) {
            return ResponseEntity.badRequest().body(Map.of("message","Token inválido"));
        }

        String subject = jwt.getSubject();
        var u = usuarioRepo.findByCpf(subject)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (u.getSituacao().isAtivo() && body.approve()) {
            return ResponseEntity.ok(Map.of("message","Já estava ATIVO"));
        }
        if (u.getSituacao().isInativo() && !body.approve()) {
            return ResponseEntity.ok(Map.of("message","Já estava NEGADO"));
        }

        u.setSituacao(body.approve() ? SituacaoEnum.ATIVO : SituacaoEnum.INATIVO);
        usuarioRepo.save(u);

        return ResponseEntity.ok(Map.of("message", body.approve() ? "Aprovado" : "Negado"));
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        var aa = a.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        var bb = b.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return java.security.MessageDigest.isEqual(aa, bb);
    }
}
