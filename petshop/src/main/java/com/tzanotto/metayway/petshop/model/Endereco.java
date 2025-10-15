package com.tzanotto.metayway.petshop.model;

import com.tzanotto.metayway.petshop.enums.TipoEnderecoEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "endereco")
public class Endereco extends BaseModel {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_endereco_cliente"))
    private Cliente cliente;

    @Column(nullable = false, length = 200)
    private String logradouro;

    @Column(nullable = false, length = 120)
    private String cidade;

    @Column(nullable = false, length = 120)
    private String bairro;

    @Column(length = 200)
    private String complemento;

    @Column(length = 50)
    private String numero;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoEnderecoEnum tag;
}

