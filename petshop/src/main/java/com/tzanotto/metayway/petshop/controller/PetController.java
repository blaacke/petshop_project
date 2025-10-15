package com.tzanotto.metayway.petshop.controller;

import com.tzanotto.metayway.petshop.dto.PetCreateDTO;
import com.tzanotto.metayway.petshop.dto.PetDTO;
import com.tzanotto.metayway.petshop.service.PetService;
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
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@Tag(name = "Pets", description = "CRUD de pets; services podem carregar atendimentos")
public class PetController {

    private final PetService service;

    @Operation(summary = "Cria um pet, procura pela descrição da raça para ver se há uma igual, caso nao haja, adiciona uma nova")
    @ApiResponse(responseCode = "201", description = "Criado",
            content = @Content(schema = @Schema(implementation = PetCreateDTO.class)))
    @PostMapping
    public ResponseEntity<PetCreateDTO> create(
            @RequestParam("clienteId") Long clienteId,
            @RequestBody @Valid PetCreateDTO body
    ) {
        PetCreateDTO pet = service.createNewPet(clienteId, body);

        return ResponseEntity
                .created(URI.create("/api/pets/"))
                .body(pet);
    }

    @Operation(summary = "Busca pet por ID")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = PetDTO.class)))
    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Lista pets paginados")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PetDTO.class))))
    @GetMapping
    public ResponseEntity<List<PetDTO>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        return ResponseEntity.ok(service.findAllDTO(page, size));
    }

    @Operation(summary = "Atualiza pet por ID")
    @ApiResponse(responseCode = "200", description = "Atualizado",
            content = @Content(schema = @Schema(implementation = PetDTO.class)))
    @PutMapping("/{id}")
    public ResponseEntity<PetDTO> update(@PathVariable @Positive Long id,
                                         @RequestBody @Valid PetDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Remove pet por ID")
    @ApiResponse(responseCode = "204", description = "Removido")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
