package com.tzanotto.metayway.petshop.repository;

import com.tzanotto.metayway.petshop.model.Pet;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    @Query("""
       select distinct p
       from Pet p
       left join fetch p.raca r
       left join fetch p.atendimentos a
    """)
    List<Pet> findAllWithRacaAndAtendimentos();

    @Query("""
       select distinct p
       from Pet p
       left join fetch p.raca r
       left join fetch p.atendimentos a
       where p.id = :id
    """)
    Optional<Pet> findByIdWithRacaAndAtendimentos(@Param("id") Long id);
}
