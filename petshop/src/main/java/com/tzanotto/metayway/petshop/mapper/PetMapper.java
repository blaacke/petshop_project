package com.tzanotto.metayway.petshop.mapper;

import com.tzanotto.metayway.petshop.dto.PetCreateDTO;
import com.tzanotto.metayway.petshop.dto.PetDTO;
import com.tzanotto.metayway.petshop.model.Cliente;
import com.tzanotto.metayway.petshop.model.Pet;
import com.tzanotto.metayway.petshop.model.Raca;
import org.springframework.stereotype.Component;

@Component
public class PetMapper implements BaseMapper<PetDTO, Pet> {

    @Override
    public PetDTO toDTO(Pet m) {
        if (m == null) return null;
        Long clienteId = m.getCliente() != null ? m.getCliente().getId() : null;
        Long racaId = m.getRaca() != null ? m.getRaca().getId() : null;
        return new PetDTO(m.getId(), clienteId, racaId, m.getDob(), m.getNome());
    }

    @Override
    public Pet toModel(PetDTO d) {
        if (d == null) return null;
        Pet p = new Pet();
        p.setId(d.id());
        if (d.clienteId() != null) {
            Cliente c = new Cliente();
            c.setId(d.clienteId());
            p.setCliente(c);
        }
        if (d.racaId() != null) {
            Raca r = new Raca();
            r.setId(d.racaId());
            p.setRaca(r);
        }
        p.setDob(d.dob());
        p.setNome(d.nome());
        return p;
    }

    public Pet toModelFromFront(PetCreateDTO d) {
        if(d == null) {
            return  null;
        }

        var pet = new Pet();
        pet.setNome(d.getNome());
        pet.setDob(d.getDob());

        return pet;
    }
}
