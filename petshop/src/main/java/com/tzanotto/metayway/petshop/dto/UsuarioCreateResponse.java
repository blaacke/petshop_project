package com.tzanotto.metayway.petshop.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCreateResponse {
    private UsuarioDTO usuario;
    private boolean required;
    private String link;
    private Instant expiresAt;
}
