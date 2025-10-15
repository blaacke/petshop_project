package com.tzanotto.metayway.petshop.service;

import com.tzanotto.metayway.petshop.dto.ClienteDTO;
import com.tzanotto.metayway.petshop.dto.ClienteDetailsDTO;
import com.tzanotto.metayway.petshop.enums.SituacaoEnum;
import com.tzanotto.metayway.petshop.mapper.ClienteMapper;
import com.tzanotto.metayway.petshop.model.Cliente;
import com.tzanotto.metayway.petshop.model.Usuario;
import com.tzanotto.metayway.petshop.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ClienteService extends AbstractService<ClienteRepository, Cliente, ClienteDTO, Long>{

    private final ClienteMapper clienteMapper;

    public ClienteService(ClienteRepository repository, ClienteMapper mapper) {
        super(repository, mapper);
        this.clienteMapper = mapper;
    }

    public List<ClienteDTO> findAll() {
        return this.findAllDTO();
    }

    public Page<ClienteDetailsDTO> findAllWithDetails(Pageable pageable) {
        Page<Long> idsPage = repository.findPageIds(pageable);
        if (idsPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Cliente> clientes = repository.findByIdIn(idsPage.getContent());

        // mapeia por id e preserva a ordem dos ids paginados
        Map<Long, Cliente> byId = clientes.stream()
                .collect(Collectors.toMap(Cliente::getId, Function.identity()));

        List<ClienteDetailsDTO> content = idsPage.getContent().stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .map(clienteMapper::toDetailsDTO)
                .toList();

        return new PageImpl<>(content, pageable, idsPage.getTotalElements());
    }

    public ClienteDetailsDTO findByDocumentoWithDetails(String cpf) {
        var cliente = repository.findByCpfWithDetails(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado para CPF: " + cpf));
        return clienteMapper.toDetailsDTO(cliente);
    }

    @Transactional
    public Cliente criarNovoCliente(Usuario usuario) {
        var cliente = new Cliente();
        cliente.setNome(usuario.getNome());
        cliente.setUsuario(usuario);

        return repository.save(cliente);
    }

}
