package com.tzanotto.metayway.petshop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "cliente")
public class Cliente extends BaseModel {

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_cliente_usuario"))
    private Usuario usuario;

    @Column(nullable = false, length = 300)
    private String nome;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private Instant dataCadastro;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Endereco> enderecos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Pet> pets = new java.util.LinkedHashSet<>();

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Contato> contatos = new java.util.LinkedHashSet<>();

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        this.dataCadastro = Instant.now();
    }
}
