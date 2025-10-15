package com.tzanotto.metayway.petshop.controller;

import com.tzanotto.metayway.petshop.dto.ClienteDTO;
import com.tzanotto.metayway.petshop.dto.ClienteDetailsDTO;
import com.tzanotto.metayway.petshop.service.ClienteService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "CRUD de clientes; services podem carregar contatos/endereços/pets")
@Slf4j
public class ClienteController {

    private final ClienteService service;

    @Operation(summary = "Busca cliente com todas as dependencias pelo tokenJwt dele")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = ClienteDetailsDTO.class)))
    @GetMapping("/me/full")
    public ResponseEntity<ClienteDetailsDTO> getMyFull(@AuthenticationPrincipal Jwt jwt) {
        String cpf = jwt.getClaimAsString("sub");
        if (cpf == null || cpf.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.findByDocumentoWithDetails(cpf));
    }

    @Operation(summary = "Lista clientes paginados")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClienteDTO.class))))
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        return ResponseEntity.ok(service.findAllDTO(page, size));
    }

    @Operation(summary = "Atualiza cliente por ID")
    @ApiResponse(responseCode = "200", description = "Atualizado",
            content = @Content(schema = @Schema(implementation = ClienteDTO.class)))
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> update(@PathVariable @Positive Long id,
                                             @RequestBody @Valid ClienteDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Remove cliente por ID")
    @ApiResponse(responseCode = "204", description = "Removido")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca todos os clientes cadastrados")
    @ApiResponse(responseCode = "200", description = "lista os clientes com todas as entidades relacionadas de forma paginada",
                    content = @Content(schema = @Schema(implementation = ClienteDetailsDTO.class)))
    @GetMapping("/full")
    public Page<ClienteDetailsDTO> listFull(Pageable pageable) {
        return service.findAllWithDetails(pageable);
    }
}
