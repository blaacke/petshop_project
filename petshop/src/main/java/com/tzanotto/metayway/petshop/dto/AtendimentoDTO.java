package com.tzanotto.metayway.petshop.dto;

import com.tzanotto.metayway.petshop.enums.AtendimentoSitEnum;
import com.tzanotto.metayway.petshop.enums.AtendimentoTipoEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AtendimentoDTO(
        Long id,
        @NotNull
        Long petId,
        String descricao,
        @NotNull
        @Positive
        BigDecimal valor,
        @NotNull
        LocalDateTime data,
        @NotNull
        AtendimentoTipoEnum tipo,
        @NotNull
        AtendimentoSitEnum situacao
) {}