package com.tzanotto.metayway.petshop.controller;

import com.tzanotto.metayway.petshop.dto.NewUserDTO;
import com.tzanotto.metayway.petshop.dto.UsuarioCreateResponse;
import com.tzanotto.metayway.petshop.service.CadastroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Validated
@RestController
@RequestMapping("/api/cadastro")
@RequiredArgsConstructor
@Tag(name = "Cadastros", description = "CRUD para cadastros (Usuario, Cliente, Pet, Contato")
public class CadastroController {

    private final CadastroService cadastroService;

    @PostMapping
    @Operation(
            summary = "Cria um novo usuario",
            description = "Cria um novo usuário. Se perfil == ADMIN, retorna link de aprovação, se CLIENTE, tb cria CLIENTE, PET e CONTATO"
    )
    @ApiResponse(responseCode = "201", description = "Criado",
            content = @Content(schema = @Schema(implementation = UsuarioCreateResponse.class)))
    @ApiResponse(responseCode = "409", description = "CPF já existe na base de dados")
    public ResponseEntity<UsuarioCreateResponse> cadastrar(@Valid @RequestBody NewUserDTO newUserDTO) {
        var resp = cadastroService.cadastrar(newUserDTO);
        URI location = URI.create("/api/usuarios/" + resp.getUsuario().id());
        return ResponseEntity.created(location).body(resp);
    }
}
