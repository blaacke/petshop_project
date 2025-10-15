package com.tzanotto.metayway.petshop.mapper;

import com.tzanotto.metayway.petshop.dto.EnderecoDTO;
import com.tzanotto.metayway.petshop.enums.TipoEnderecoEnum;
import com.tzanotto.metayway.petshop.model.Cliente;
import com.tzanotto.metayway.petshop.model.Endereco;
import org.springframework.stereotype.Component;

@Component
public class EnderecoMapper implements BaseMapper<EnderecoDTO, Endereco> {

    @Override
    public EnderecoDTO toDTO(Endereco m) {
        if (m == null) return null;
        Long clienteId = m.getCliente() != null ? m.getCliente().getId() : null;
        TipoEnderecoEnum tag = m.getTag();
        return new EnderecoDTO(m.getId(), clienteId, m.getLogradouro(), m.getCidade(),
                m.getBairro(), m.getComplemento(),m.getNumero(), tag);
    }

    @Override
    public Endereco toModel(EnderecoDTO d) {
        if (d == null) return null;
        Endereco e = new Endereco();
        e.setId(d.id());
        if (d.clienteId() != null) {
            Cliente c = new Cliente();
            c.setId(d.clienteId());
            e.setCliente(c);
        }
        e.setLogradouro(d.logradouro());
        e.setCidade(d.cidade());
        e.setBairro(d.bairro());
        e.setComplemento(d.complemento());
        e.setNumero(d.numero());
        e.setTag(d.tag());
        return e;
    }
}
