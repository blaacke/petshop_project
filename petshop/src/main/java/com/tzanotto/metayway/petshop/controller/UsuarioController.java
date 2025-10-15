package com.tzanotto.metayway.petshop.controller;

import com.tzanotto.metayway.petshop.dto.request.SituacaoRequest;
import com.tzanotto.metayway.petshop.dto.UsuarioDTO;
import com.tzanotto.metayway.petshop.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "CRUD de usuários do sistema")
public class UsuarioController {

    private final UsuarioService service;

    @Operation(summary = "Busca usuário por ID")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = UsuarioDTO.class)))
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Lista usuários paginados")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UsuarioDTO.class))))
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        return ResponseEntity.ok(service.findAllDTO(page, size));
    }

    @Operation(summary = "Atualiza usuário por ID")
    @ApiResponse(responseCode = "200", description = "Atualizado",
            content = @Content(schema = @Schema(implementation = UsuarioDTO.class)))
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> update(@PathVariable @Positive Long id,
                                             @RequestBody @Valid UsuarioDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Remove usuário por ID")
    @ApiResponse(responseCode = "204", description = "Removido")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ativa ou inativa um usuário")
    @ApiResponse(responseCode = "200", description = "ativa ou invativa ou usuario, baseado na id do mesmo",
                    content = @Content(schema = @Schema(implementation = SituacaoRequest.class)))
    @PatchMapping("/situacao/{doc}")
    public ResponseEntity<Void> updateSituacao(@PathVariable String doc, @RequestBody SituacaoRequest req) {
        service.updateSit(doc, req);
        return ResponseEntity.ok().build();
    }
}
