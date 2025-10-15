package com.tzanotto.metayway.petshop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewUserDTO {
    @NotNull
    private UsuarioDTO usuario;
    private ContatoDTO contato;
    private PetCreateDTO pet;
}
