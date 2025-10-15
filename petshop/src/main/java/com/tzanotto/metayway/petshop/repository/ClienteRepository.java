package com.tzanotto.metayway.petshop.repository;

import com.tzanotto.metayway.petshop.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    @Query("select c.id from Cliente c")
    Page<Long> findPageIds(Pageable pageable);

    @EntityGraph(attributePaths = {
            "enderecos",
            "contatos",
            "pets",
            "pets.raca",
            "pets.atendimentos"
    })
    List<Cliente> findByIdIn(Collection<Long> ids);

    @EntityGraph(attributePaths = {
            "enderecos",
            "contatos",
            "pets",
            "pets.raca",
            "pets.atendimentos"
    })
    @Query("""
           select c
             from Cliente c
             join c.usuario u
            where u.cpf = :cpf
           """)
    Optional<Cliente> findByCpfWithDetails(@Param("cpf") String cpf);
}
