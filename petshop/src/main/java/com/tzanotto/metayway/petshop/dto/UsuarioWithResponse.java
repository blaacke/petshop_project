package com.tzanotto.metayway.petshop.dto;

import com.tzanotto.metayway.petshop.model.Usuario;

public record UsuarioWithResponse(UsuarioCreateResponse response, Usuario entity) {}
