package com.tzanotto.metayway.petshop.repository;

import com.tzanotto.metayway.petshop.model.Raca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RacaRepository extends JpaRepository<Raca, Long> {
    Optional<Raca> findByDescricaoIgnoreCase(String descricao);
}
