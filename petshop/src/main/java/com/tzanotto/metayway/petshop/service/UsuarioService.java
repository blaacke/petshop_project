package com.tzanotto.metayway.petshop.service;

import com.tzanotto.metayway.petshop.dto.request.SituacaoRequest;
import com.tzanotto.metayway.petshop.dto.UsuarioCreateResponse;
import com.tzanotto.metayway.petshop.dto.UsuarioDTO;
import com.tzanotto.metayway.petshop.dto.UsuarioWithResponse;
import com.tzanotto.metayway.petshop.enums.SituacaoEnum;
import com.tzanotto.metayway.petshop.mapper.UsuarioMapper;
import com.tzanotto.metayway.petshop.model.Usuario;
import com.tzanotto.metayway.petshop.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class UsuarioService extends AbstractService<UsuarioRepository, Usuario, UsuarioDTO, Long> {
    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Value("${front.base.url}")
    private String frontUrl;

    @Value("${approval.hours}")
    private long ttlHours;

    public UsuarioService(UsuarioRepository repository, UsuarioMapper mapper, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder,
                          UsuarioRepository usuarioRepository) {
        super(repository, mapper);
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioDTO> findAll() {
        return this.findAllDTO();
    }

    public Optional<UsuarioDTO> findByCpf(String cpf) {
        return repository.findByCpf(cpf).map((u) -> mapper.toDTO(u));
    }

    @Transactional
    public UsuarioWithResponse createWithApprovalFlow(UsuarioDTO dto) {
        var isAdmin = dto.perfil().isAdmin();

        var entity = mapper.toModel(dto);
        entity.setSituacao(isAdmin ? SituacaoEnum.PENDENTE : SituacaoEnum.ATIVO);
        entity.setPassword(passwordEncoder.encode(dto.password()));
        var saved = repository.save(entity);
        var out = mapper.toDTO(saved);

        UsuarioCreateResponse response;
        if (isAdmin) {
            var tokenData = gerarApprovalToken(saved.getCpf());
            var link = frontUrl + "/admin-approval?token=" + tokenData.token();

            response = UsuarioCreateResponse.builder()
                    .usuario(out)
                    .link(link)
                    .required(true)
                    .expiresAt(tokenData.expiresAt())
                    .build();
        } else {
            response = UsuarioCreateResponse.builder()
                    .usuario(out)
                    .required(false)
                    .build();
        }

        return new UsuarioWithResponse(response, saved);
    }


    private ApprovalToken gerarApprovalToken(String cpf) {
        var now = Instant.now();
        var exp = now.plus(ttlHours, ChronoUnit.HOURS);

        var claims = JwtClaimsSet.builder()
                .subject(cpf)
                .issuedAt(now)
                .expiresAt(exp)
                .claim("kind", "admin-approval")
                .build();


        var headers = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
        return new ApprovalToken(token, exp);
    }

    @Transactional
    public void updateSit(String doc, SituacaoRequest req) {
        Usuario u = repository.findByCpf(doc)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + doc));

        u.setSituacao(req.situacao());
        repository.save(u);
    }

    private record ApprovalToken(String token, Instant expiresAt) {}
}
