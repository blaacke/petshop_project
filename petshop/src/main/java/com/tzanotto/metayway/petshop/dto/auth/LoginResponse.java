package com.tzanotto.metayway.petshop.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private long   expiresIn;
    private String tokenType;
}
