package com.tzanotto.metayway.petshop.service;

import com.tzanotto.metayway.petshop.dto.ContatoDTO;
import com.tzanotto.metayway.petshop.mapper.ContatoMapper;
import com.tzanotto.metayway.petshop.model.Cliente;
import com.tzanotto.metayway.petshop.model.Contato;
import com.tzanotto.metayway.petshop.repository.ContatoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ContatoService extends AbstractService<ContatoRepository, Contato, ContatoDTO, Long>{

    public ContatoService(ContatoRepository repository, ContatoMapper mapper) {
        super(repository, mapper);
    }

    public List<Contato> findByClienteId(Long clienteId) {
        return repository.findByClienteIdWithCliente(clienteId);
    }

    @Transactional
    public void criarNovoContato( Contato contato) {
        repository.save(contato);
    }
}
