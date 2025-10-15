
package com.tzanotto.metayway.petshop.dto;

import com.tzanotto.metayway.petshop.enums.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ClienteDetailsDTO(
        Long id,
        Long usuarioId,
        String nome,
        String documento,
        SituacaoEnum situacao,
        Instant dataCadastro,
        List<EnderecoDTO> enderecos,
        List<ContatoDTO> contatos,
        List<PetDTO> pets
) {

    public record PetDTO(
            Long id,
            String nome,
            LocalDate dob,
            RacaDTO raca,
            List<AtendimentoDTO> atendimentos
    ) {}

    public record RacaDTO(String descricao) {}

    public record AtendimentoDTO(
            Long id,
            String descricao,
            BigDecimal valor,
            LocalDateTime data,
            AtendimentoTipoEnum tipo,
            AtendimentoSitEnum situacao
    ) {}
}
