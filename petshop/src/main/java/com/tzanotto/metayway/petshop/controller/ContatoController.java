package com.tzanotto.metayway.petshop.controller;

import com.tzanotto.metayway.petshop.dto.ContatoDTO;
import com.tzanotto.metayway.petshop.service.ContatoService;
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
@RequestMapping("/api/contatos")
@RequiredArgsConstructor
@Tag(name = "Contatos", description = "CRUD de contatos (telefone/email) de clientes")
public class ContatoController {

    private final ContatoService service;

    @Operation(summary = "Cria um contato")
    @ApiResponse(responseCode = "201", description = "Criado",
            content = @Content(schema = @Schema(implementation = ContatoDTO.class)))
    @PostMapping
    public ResponseEntity<ContatoDTO> create(@RequestBody @Valid ContatoDTO dto) {
        var saved = service.create(dto);
        return ResponseEntity.created(URI.create("/api/contatos/" + saved.id())).body(saved);
    }

    @Operation(summary = "Busca contato por ID")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = ContatoDTO.class)))
    @GetMapping("/{id}")
    public ResponseEntity<ContatoDTO> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Lista contatos paginados")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContatoDTO.class))))
    @GetMapping
    public ResponseEntity<List<ContatoDTO>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        return ResponseEntity.ok(service.findAllDTO(page, size));
    }

    @Operation(summary = "Atualiza contato por ID")
    @ApiResponse(responseCode = "200", description = "Atualizado",
            content = @Content(schema = @Schema(implementation = ContatoDTO.class)))
    @PutMapping("/{id}")
    public ResponseEntity<ContatoDTO> update(@PathVariable @Positive Long id,
                                             @RequestBody @Valid ContatoDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Remove contato por ID")
    @ApiResponse(responseCode = "204", description = "Removido")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
