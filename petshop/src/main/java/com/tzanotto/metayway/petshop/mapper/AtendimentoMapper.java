package com.tzanotto.metayway.petshop.mapper;

import com.tzanotto.metayway.petshop.dto.AtendimentoDTO;
import com.tzanotto.metayway.petshop.model.Atendimento;
import com.tzanotto.metayway.petshop.model.Pet;
import org.springframework.stereotype.Component;

@Component
public class AtendimentoMapper implements BaseMapper<AtendimentoDTO, Atendimento> {

    @Override
    public AtendimentoDTO toDTO(Atendimento m) {
        if (m == null) return null;
        Long petId = m.getPet() != null ? m.getPet().getId() : null;
        return new AtendimentoDTO(m.getId(), petId, m.getDescricao(), m.getValor(), m.getData(), m.getTipo(), m.getSituacao());
    }

    @Override
    public Atendimento toModel(AtendimentoDTO d) {
        if (d == null) return null;
        Atendimento a = new Atendimento();
        a.setId(d.id());
        if (d.petId() != null) {
            Pet p = new Pet();
            p.setId(d.petId());
            a.setPet(p);
        }
        a.setDescricao(d.descricao());
        a.setValor(d.valor());
        a.setData(d.data());
        a.setTipo(d.tipo());
        a.setSituacao(d.situacao());
        return a;
    }
}
