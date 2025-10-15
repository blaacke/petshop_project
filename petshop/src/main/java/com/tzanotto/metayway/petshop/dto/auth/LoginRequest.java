package com.tzanotto.metayway.petshop.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String cpf;
    @NotBlank
    private String password;
}
