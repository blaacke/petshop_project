package com.tzanotto.metayway.petshop.model;

import com.tzanotto.metayway.petshop.enums.ContatoTagEnum;
import com.tzanotto.metayway.petshop.enums.ContatoTipoEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "contato")
public class Contato extends BaseModel {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_contato_cliente"))
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContatoTagEnum tag;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContatoTipoEnum tipo;

    @Column(nullable = false, length = 180)
    private String valor;
}
