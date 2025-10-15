package com.tzanotto.metayway.petshop.mapper;

import com.tzanotto.metayway.petshop.dto.RacaDTO;
import com.tzanotto.metayway.petshop.model.Raca;
import org.springframework.stereotype.Component;

@Component
public class RacaMapper implements BaseMapper<RacaDTO, Raca> {

    @Override
    public RacaDTO toDTO(Raca m) {
        if (m == null) return null;
        return new RacaDTO(m.getId(), m.getDescricao());
    }

    @Override
    public Raca toModel(RacaDTO d) {
        if (d == null) return null;
        Raca r = new Raca();
        r.setId(d.id());
        r.setDescricao(d.descricao());
        return r;
    }
}
