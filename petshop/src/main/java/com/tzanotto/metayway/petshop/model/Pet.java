package com.tzanotto.metayway.petshop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "pet")
public class Pet extends BaseModel {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pet_cliente"))
    private Cliente cliente;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "raca_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pet_raca"))
    private Raca raca;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(nullable = false, length = 120)
    private String nome;

    @OneToMany(mappedBy = "pet", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Atendimento> atendimentos = new java.util.LinkedHashSet<>();
}
