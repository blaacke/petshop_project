package com.tzanotto.metayway.petshop.controller;

import com.tzanotto.metayway.petshop.dto.EnderecoDTO;
import com.tzanotto.metayway.petshop.service.EnderecoService;
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
@RequestMapping("/api/enderecos")
@RequiredArgsConstructor
@Tag(name = "Endereços", description = "CRUD de endereços (relacionados a Cliente)")
public class EnderecoController {

    private final EnderecoService service;

    @Operation(summary = "Cria um endereço")
    @ApiResponse(responseCode = "201", description = "Criado",
            content = @Content(schema = @Schema(implementation = EnderecoDTO.class)))
    @PostMapping
    public ResponseEntity<EnderecoDTO> create(@RequestBody @Valid EnderecoDTO dto) {
        var saved = service.create(dto);
        return ResponseEntity.created(URI.create("/api/enderecos/" + saved.id())).body(saved);
    }

    @Operation(summary = "Busca endereço por ID")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = EnderecoDTO.class)))
    @GetMapping("/{id}")
    public ResponseEntity<EnderecoDTO> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Lista endereços paginados")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = EnderecoDTO.class))))
    @GetMapping
    public ResponseEntity<List<EnderecoDTO>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        return ResponseEntity.ok(service.findAllDTO(page, size));
    }

    @Operation(summary = "Atualiza endereço por ID")
    @ApiResponse(responseCode = "200", description = "Atualizado",
            content = @Content(schema = @Schema(implementation = EnderecoDTO.class)))
    @PutMapping("/{id}")
    public ResponseEntity<EnderecoDTO> update(@PathVariable @Positive Long id,
                                              @RequestBody @Valid EnderecoDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Remove endereço por ID")
    @ApiResponse(responseCode = "204", description = "Removido")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
