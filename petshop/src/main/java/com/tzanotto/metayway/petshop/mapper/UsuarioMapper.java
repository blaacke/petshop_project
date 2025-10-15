package com.tzanotto.metayway.petshop.mapper;

import com.tzanotto.metayway.petshop.dto.UsuarioDTO;
import com.tzanotto.metayway.petshop.enums.PerfilEnum;
import com.tzanotto.metayway.petshop.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper implements BaseMapper<UsuarioDTO, Usuario> {

    @Override
    public UsuarioDTO toDTO(Usuario m) {
        if (m == null) return null;
        return new UsuarioDTO(
                m.getId(),
                m.getCpf(),
                m.getNome(),
                m.getPassword(),
                m.getPerfil(),
                m.getSituacao()
        );
    }

    @Override
    public Usuario toModel(UsuarioDTO d) {
        if (d == null) return null;
        Usuario u = new Usuario();
        u.setId(d.id());
        u.setCpf(d.cpf());
        u.setNome(d.nome());
        u.setPassword(d.password());
        u.setPerfil(d.perfil());
        u.setSituacao(d.situacao());
        return u;
    }
}
