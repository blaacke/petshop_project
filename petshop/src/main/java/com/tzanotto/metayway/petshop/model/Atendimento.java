package com.tzanotto.metayway.petshop.model;

import com.tzanotto.metayway.petshop.enums.AtendimentoSitEnum;
import com.tzanotto.metayway.petshop.enums.AtendimentoTipoEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "atendimento")
public class Atendimento extends BaseModel {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_atendimento_pet"))
    private Pet pet;

    @Column(nullable = false, length = 400)
    private String descricao;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDateTime data;

    @Column(name = "tipo", nullable = false)
    @Enumerated(EnumType.STRING)
    private AtendimentoTipoEnum tipo;

    @Column(name = "situacao", nullable = false)
    @Enumerated(EnumType.STRING)
    private AtendimentoSitEnum situacao;
}
