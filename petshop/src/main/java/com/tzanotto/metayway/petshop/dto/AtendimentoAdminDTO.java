
package com.tzanotto.metayway.petshop.dto;



import com.tzanotto.metayway.petshop.enums.AtendimentoSitEnum;
import com.tzanotto.metayway.petshop.enums.AtendimentoTipoEnum;

import java.time.LocalDateTime;

public record AtendimentoAdminDTO(
        Long id,
        LocalDateTime data,
        AtendimentoTipoEnum tipo,
        java.math.BigDecimal valor,
        AtendimentoSitEnum situacao,

        PetDTO pet,
        TutorDTO tutor
) {
    public record PetDTO(Long id, String nome) {}
    public record TutorDTO(
            Long id, String nome, String documento,
            ContatoDTO contato
    ) {}
    public record ContatoDTO(String tipo, String valor) {}
}
