package com.tzanotto.metayway.petshop.dto;

import com.tzanotto.metayway.petshop.enums.ContatoTagEnum;
import com.tzanotto.metayway.petshop.enums.ContatoTipoEnum;
import jakarta.validation.constraints.NotNull;

public record ContatoDTO(
        Long id,
        @NotNull
        Long clienteId,
        @NotNull
        ContatoTagEnum tag,
        @NotNull
        ContatoTipoEnum tipo,
        @NotNull
        String valor
) {}
