package com.tzanotto.metayway.petshop.dto;

import com.tzanotto.metayway.petshop.enums.PerfilEnum;
import com.tzanotto.metayway.petshop.enums.SituacaoEnum;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

public record UsuarioDTO(
        Long id,
        @NotNull
        @CPF
        String cpf,
        @NotNull
        String nome,
        @NotNull
        String password,
        @NotNull
        PerfilEnum perfil,
        @NotNull
        SituacaoEnum situacao
) {}
