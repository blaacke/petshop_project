package com.tzanotto.metayway.petshop.service;

import com.tzanotto.metayway.petshop.dto.PetCreateDTO;
import com.tzanotto.metayway.petshop.dto.PetDTO;
import com.tzanotto.metayway.petshop.mapper.PetMapper;
import com.tzanotto.metayway.petshop.model.Cliente;
import com.tzanotto.metayway.petshop.model.Pet;
import com.tzanotto.metayway.petshop.model.Raca;
import com.tzanotto.metayway.petshop.repository.ClienteRepository;
import com.tzanotto.metayway.petshop.repository.PetRepository;
import com.tzanotto.metayway.petshop.repository.RacaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PetService extends  AbstractService<PetRepository, Pet, PetDTO, Long> {

    private final ClienteRepository clienteRepository;
    private final RacaRepository racaRepository;

    public PetService(PetRepository repository, PetMapper mapper, ClienteRepository clienteRepository, RacaRepository racaRepository) {
        super(repository, mapper);
        this.clienteRepository = clienteRepository;
        this.racaRepository = racaRepository;
    }

    public List<PetDTO> findAll() {
        return this.findAllDTO();
    }

    public List<Pet> findAllWithRacaAndAtendimentos() {
        return repository.findAllWithRacaAndAtendimentos();
    }

    public Pet findByIdWithRacaAndAtendimentos(Long id) {
        return repository.findByIdWithRacaAndAtendimentos(id).orElse(null);
    }

    @Transactional
    public void criarNovoPet(Pet pet) {
        repository.save(pet);
    }

    @Transactional
    public PetCreateDTO createNewPet(Long clienteId, @Valid PetCreateDTO body) {
        var cliente = clienteRepository.findById(clienteId)
                        .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado: " + clienteId));

        String desc = body.getDescricao().trim();
        Raca raca = racaRepository.findByDescricaoIgnoreCase(desc)
                .orElseGet(() -> {
                    Raca nova = new Raca();
                    nova.setDescricao(desc);
                    return racaRepository.save(nova);
                });

        Pet pet = new Pet();
        pet.setNome(body.getNome().trim());
        pet.setRaca(raca);
        pet.setCliente(cliente);
        pet.setDob(body.getDob());

        Pet saved = repository.save(pet);

        return new PetCreateDTO(saved.getNome(),
                saved.getDob(), raca.getDescricao());
    }
}
