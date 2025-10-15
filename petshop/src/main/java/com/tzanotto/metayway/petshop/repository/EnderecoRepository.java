package com.tzanotto.metayway.petshop.repository;

import com.tzanotto.metayway.petshop.model.Endereco;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    @Query("""
       select e
       from Endereco e
       join fetch e.cliente c
       where c.id = :clienteId
    """)
    List<Endereco> findByClienteIdWithCliente(@Param("clienteId") Long clienteId);
}
