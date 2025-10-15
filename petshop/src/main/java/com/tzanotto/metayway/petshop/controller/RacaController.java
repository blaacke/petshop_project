package com.tzanotto.metayway.petshop.controller;

import com.tzanotto.metayway.petshop.dto.RacaDTO;
import com.tzanotto.metayway.petshop.service.RacaService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/racas")
@RequiredArgsConstructor
@Tag(name = "Raças", description = "CRUD de raças de pets")
public class RacaController {

    private final RacaService service;

    @Operation(summary = "Cria uma raça")
    @ApiResponse(responseCode = "201", description = "Criado",
            content = @Content(schema = @Schema(implementation = RacaDTO.class)))
    @PostMapping
    public ResponseEntity<RacaDTO> create(@RequestBody @Valid RacaDTO dto) {
        var saved = service.create(dto);
        return ResponseEntity.created(URI.create("/api/racas/" + saved.id())).body(saved);
    }

    @Operation(summary = "Busca raça por ID")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = RacaDTO.class)))
    @GetMapping("/{id}")
    public ResponseEntity<RacaDTO> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Lista raças paginadas")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = RacaDTO.class))))
    @GetMapping
    public ResponseEntity<List<RacaDTO>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        return ResponseEntity.ok(service.findAllDTO(page, size));
    }

    @Operation(summary = "Atualiza raça por ID")
    @ApiResponse(responseCode = "200", description = "Atualizado",
            content = @Content(schema = @Schema(implementation = RacaDTO.class)))
    @PutMapping("/{id}")
    public ResponseEntity<RacaDTO> update(@PathVariable @Positive Long id,
                                          @RequestBody @Valid RacaDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Remove raça por ID")
    @ApiResponse(responseCode = "204", description = "Removido")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
