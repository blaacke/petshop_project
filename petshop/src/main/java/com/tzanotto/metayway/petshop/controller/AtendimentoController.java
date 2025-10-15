package com.tzanotto.metayway.petshop.controller;

import com.tzanotto.metayway.petshop.dto.AtendimentoAdminDTO;
import com.tzanotto.metayway.petshop.dto.AtendimentoDTO;
import com.tzanotto.metayway.petshop.dto.request.AtendimentoSitRequest;
import com.tzanotto.metayway.petshop.service.AtendimentoService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/atendimentos")
@RequiredArgsConstructor
@Tag(name = "Atendimentos", description = "CRUD de atendimentos (serviços realizados nos pets)")
public class AtendimentoController {

    private final AtendimentoService service;

    @Operation(summary = "Cria um atendimento")
    @ApiResponse(responseCode = "201", description = "Criado",
            content = @Content(schema = @Schema(implementation = AtendimentoDTO.class)))
    @PostMapping
    public ResponseEntity<AtendimentoDTO> create(@RequestBody @Valid AtendimentoDTO dto) {
        var saved = service.create(dto);
        return ResponseEntity.created(URI.create("/api/atendimentos/" + saved.id())).body(saved);
    }

    @Operation(summary = "Busca atendimento por ID")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = AtendimentoDTO.class)))
    @GetMapping("/{id}")
    public ResponseEntity<AtendimentoDTO> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Lista atendimentos paginados", description = "Filtro opcional por intervalo de datas (from/to).")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AtendimentoDTO.class))))
    @GetMapping
    public ResponseEntity<List<AtendimentoDTO>> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime to) {
        return ResponseEntity.ok(service.findAllByDateRange( from, to, page, size));
    }

    @Operation(summary = "Atualiza atendimento por ID")
    @ApiResponse(responseCode = "200", description = "Atualizado",
            content = @Content(schema = @Schema(implementation = AtendimentoDTO.class)))
    @PutMapping("/{id}")
    public ResponseEntity<AtendimentoDTO> update(@PathVariable @Positive Long id,
                                                 @RequestBody @Valid AtendimentoDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Remove atendimento por ID")
    @ApiResponse(responseCode = "204", description = "Removido")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "busca os agendamentos para determinado dia")
    @ApiResponse(responseCode = "200", description = "ok")
    @GetMapping("/day")
    public ResponseEntity<List<LocalDateTime>> getBookedForDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(service.getBookedDateTimes(date));
    }

    @GetMapping("/admin/day")
    public List<AtendimentoAdminDTO> listAdminByDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.findAdminByDate(date);
    }


    @PatchMapping("/{id}/situacao")
    public void updateSituacao(@PathVariable Long id, @RequestBody AtendimentoSitRequest body) {
        service.updateSituacao(id, body.situacao());
    }
}
