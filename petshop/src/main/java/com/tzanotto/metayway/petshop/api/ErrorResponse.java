package com.tzanotto.metayway.petshop.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Map;

@Schema(name = "ErrorResponse", description = "Modelo padronizado de erro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    @Schema(example = "400")
    private int status;

    @Schema(example = "Validação falhou")
    private String error;

    @Schema(example = "/api/clientes")
    private String path;

    @Schema(description = "Erros de validação por campo (quando aplicável)")
    private Map<String, String> fields;

    @Schema(example = "2025-09-25T11:15:30-03:00")
    private OffsetDateTime timestamp;

    public ErrorResponse(int status, String error, String path, Map<String, String> fields) {
        this.status = status;
        this.error = error;
        this.path = path;
        this.fields = fields;
        this.timestamp = OffsetDateTime.now();
    }
}
