package com.tzanotto.metayway.petshop.dto;

import jakarta.validation.constraints.NotNull;

public record RacaDTO(
        Long id,
        @NotNull
        String descricao
) {}
