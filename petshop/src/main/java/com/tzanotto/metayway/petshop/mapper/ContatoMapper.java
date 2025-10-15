package com.tzanotto.metayway.petshop.mapper;

import com.tzanotto.metayway.petshop.dto.ContatoDTO;
import com.tzanotto.metayway.petshop.enums.ContatoTagEnum;
import com.tzanotto.metayway.petshop.enums.ContatoTipoEnum;
import com.tzanotto.metayway.petshop.model.Cliente;
import com.tzanotto.metayway.petshop.model.Contato;
import org.springframework.stereotype.Component;

@Component
public class ContatoMapper implements BaseMapper<ContatoDTO, Contato> {

    @Override
    public ContatoDTO toDTO(Contato m) {
        if (m == null) return null;
        Long clienteId = m.getCliente() != null ? m.getCliente().getId() : null;
        ContatoTagEnum tag = m.getTag();
        ContatoTipoEnum tipo = m.getTipo();
        return new ContatoDTO(m.getId(), clienteId, tag, tipo, m.getValor());
    }

    @Override
    public Contato toModel(ContatoDTO d) {
        if (d == null) return null;
        Contato c = new Contato();
        c.setId(d.id());
        if (d.clienteId() != null) {
            Cliente cli = new Cliente();
            cli.setId(d.clienteId());
            c.setCliente(cli);
        }
        c.setTag(d.tag());
        c.setTipo(d.tipo());
        c.setValor(d.valor());
        return c;
    }
}
