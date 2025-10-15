package com.tzanotto.metayway.petshop.repository;

import com.tzanotto.metayway.petshop.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCpf(String cpf);

    @Query("""
    select u from Usuario u
    where function('REPLACE',
           function('REPLACE',
             function('REPLACE',
               function('REPLACE', u.cpf, '.', ''), '-', ''
             ), '/', ''
           ), ' ', ''
         ) = :cpf
  """)
    Optional<Usuario> findByCpfNormalized(@Param("cpf") String cpf);
}
