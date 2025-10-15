package com.tzanotto.metayway.petshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
@Table(name = "raca")
public class Raca extends BaseModel {

    @Column(nullable = false, unique = true, length = 120)
    private String descricao;
}
