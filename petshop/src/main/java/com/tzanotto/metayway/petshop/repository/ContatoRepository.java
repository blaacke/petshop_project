package com.tzanotto.metayway.petshop.repository;

import com.tzanotto.metayway.petshop.model.Contato;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContatoRepository extends JpaRepository<Contato, Long> {

    @Query("""
       select c
       from Contato c
       join fetch c.cliente cli
       where cli.id = :clienteId
    """)
    List<Contato> findByClienteIdWithCliente(@Param("clienteId") Long clienteId);
}
