package com.tzanotto.metayway.petshop.service;

import com.tzanotto.metayway.petshop.dto.UsuarioCreateResponse;
import com.tzanotto.metayway.petshop.dto.UsuarioDTO;
import com.tzanotto.metayway.petshop.dto.UsuarioWithResponse;
import com.tzanotto.metayway.petshop.enums.PerfilEnum;
import com.tzanotto.metayway.petshop.enums.SituacaoEnum;
import com.tzanotto.metayway.petshop.model.Usuario;
import com.tzanotto.metayway.petshop.repository.UsuarioRepository;
import com.tzanotto.metayway.petshop.mapper.UsuarioMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testa o fluxo de criação de usuário quando perfil=ADMIN:
 * - deve nascer PENDENTE
 * - deve retornar link de aprovação com token
 * - deve codificar a senha
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceApprovalFlowTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private UsuarioMapper usuarioMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtEncoder jwtEncoder;

    @InjectMocks
    private UsuarioService service;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service, "frontUrl", "http://localhost:4200");
        ReflectionTestUtils.setField(service, "ttlHours", 24L);
    }

    @Test
    void createWithApprovalFlow_admin_deveRetornarApprovalLink_eSalvarPendente() {
        UsuarioDTO dto = new UsuarioDTO(
                null,
                "12345678901",
                "Adm Teste",
                "t3ste123!",
                PerfilEnum.admin,
                null);

        Usuario entity = new Usuario();
        entity.setCpf("12345678901");
        entity.setNome("Admin Teste");
        entity.setPerfil(PerfilEnum.admin);
        when(usuarioMapper.toModel(dto)).thenReturn(entity);

        when(passwordEncoder.encode("t3ste123!")).thenReturn("ENCODED");

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            ReflectionTestUtils.setField(u, "id", 220L);
            return u;
        });

        UsuarioDTO dtoSaida = mock(UsuarioDTO.class);
        when(usuarioMapper.toDTO(any(Usuario.class))).thenReturn(dtoSaida);

        Jwt fakeJwt = Jwt.withTokenValue("APPROVAL_TOKEN")
                .header("alg", "HS256")
                .subject("12345678901")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
                .claim("kind", "admin-approval")
                .build();
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(fakeJwt);

        UsuarioWithResponse userWithResp = service.createWithApprovalFlow(dto);
        var resp = userWithResp.response();

        assertNotNull(resp);
        assertTrue(resp.isRequired(), "required deve ser true para ADMIN");
        assertNotNull(resp.getLink(), "link deve existir");
        assertTrue(resp.getLink().startsWith("http://localhost:4200/admin-approval?token="));
        assertTrue(resp.getLink().endsWith("APPROVAL_TOKEN"),
                "link deve conter o token retornado pelo JwtEncoder");
        assertNotNull(resp.getExpiresAt(), "approval.expiresAt deve existir");

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, atLeastOnce()).save(captor.capture());
        Usuario salvo = captor.getValue();
        assertEquals(SituacaoEnum.PENDENTE, salvo.getSituacao(), "ADMIN deve nascer PENDENTE");
        assertEquals("ENCODED", salvo.getPassword(), "password deve ser codificado");

        verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
    }
}
