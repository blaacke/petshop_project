package com.tzanotto.metayway.petshop.model;

import com.tzanotto.metayway.petshop.enums.PerfilEnum;
import com.tzanotto.metayway.petshop.enums.SituacaoEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "usuario", indexes = {@Index(name = "index_usuario_cpf", columnList = "cpf")})
public class Usuario  extends BaseModel{

    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    private String cpf;
    @Column(length = 300, nullable = false)
    private String nome;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PerfilEnum perfil;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoEnum situacao;
    @Column(length = 1000)
    private String observacao;
}
