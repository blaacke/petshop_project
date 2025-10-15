package com.tzanotto.metayway.petshop.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ClienteDTO(
        Long id,
        @NotNull
        Long usuarioId,
        @NotNull
        String nome,
        Instant dataCadastro
) {}
