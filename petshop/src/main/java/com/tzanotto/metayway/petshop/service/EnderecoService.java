package com.tzanotto.metayway.petshop.service;

import com.tzanotto.metayway.petshop.dto.EnderecoDTO;
import com.tzanotto.metayway.petshop.mapper.EnderecoMapper;
import com.tzanotto.metayway.petshop.model.Endereco;
import com.tzanotto.metayway.petshop.repository.EnderecoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EnderecoService extends AbstractService<EnderecoRepository, Endereco, EnderecoDTO, Long> {

    public EnderecoService(EnderecoRepository repository, EnderecoMapper mapper) {
        super(repository, mapper);
    }

    public List<EnderecoDTO> findAll() {
        return this.findAllDTO();
    }

    public List<Endereco> findByClienteId(Long clienteId) {
        return repository.findByClienteIdWithCliente(clienteId);
    }
}
