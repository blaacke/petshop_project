package com.tzanotto.metayway.petshop.repository;

import com.tzanotto.metayway.petshop.model.Atendimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    @EntityGraph(attributePaths = "pet")
    List<Atendimento> findByPetId(Long petId);

    @EntityGraph(attributePaths = "pet")
    Page<Atendimento> findByPetId(Long petId, Pageable pageable);

    Page<Atendimento> findByDataBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @EntityGraph(attributePaths = "pet")
    Page<Atendimento> findByPetIdAndDataBetween(Long petId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("select a.id from Atendimento a")
    Page<Long> findPageIds(Pageable pageable);

    @EntityGraph(attributePaths = {
            "pet",
            "pet.cliente",
            "pet.cliente.usuario",
            "pet.cliente.contatos"
    })
    List<Atendimento> findByIdIn(Collection<Long> ids);

    @EntityGraph(attributePaths = {
            "pet",
            "pet.cliente",
            "pet.cliente.usuario",
            "pet.cliente.contatos"
    })
    List<Atendimento> findAllByDataBetween(LocalDateTime start, LocalDateTime end);
}
