package com.tzanotto.metayway.petshop.service;

import com.tzanotto.metayway.petshop.dto.RacaDTO;
import com.tzanotto.metayway.petshop.mapper.RacaMapper;
import com.tzanotto.metayway.petshop.model.Raca;
import com.tzanotto.metayway.petshop.repository.RacaRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RacaService extends AbstractService<RacaRepository, Raca, RacaDTO, Long> {

    public RacaService(RacaRepository repository, RacaMapper mapper) {
        super(repository, mapper);
    }

    public List<RacaDTO> findAll() {
        return this.findAllDTO();
    }

    @Transactional
    public Raca resolveRaca(@NotNull String descricao) {
        var toSave = new Raca();
        toSave.setDescricao(descricao);
        return repository.findByDescricaoIgnoreCase(descricao.trim()).orElseGet(() -> repository.save(toSave));
    }
}
