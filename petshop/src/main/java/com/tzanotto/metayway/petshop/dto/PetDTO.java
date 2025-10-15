package com.tzanotto.metayway.petshop.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PetDTO(
        Long id,
        @NotNull
        Long clienteId,
        @NotNull
        Long racaId,
        LocalDate dob,
        @NotNull
        String nome
) {}
