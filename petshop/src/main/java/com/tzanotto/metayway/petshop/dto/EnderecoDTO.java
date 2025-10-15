package com.tzanotto.metayway.petshop.dto;

import com.tzanotto.metayway.petshop.enums.TipoEnderecoEnum;
import jakarta.validation.constraints.NotNull;

public record EnderecoDTO(
        Long id,
        @NotNull
        Long clienteId,
        @NotNull
        String logradouro,
        @NotNull
        String cidade,
        @NotNull
        String bairro,
        String complemento,
        String numero,
        @NotNull
        TipoEnderecoEnum tag
) {}
