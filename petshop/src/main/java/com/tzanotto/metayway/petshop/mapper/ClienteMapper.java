package com.tzanotto.metayway.petshop.mapper;

import com.tzanotto.metayway.petshop.dto.ClienteDTO;
import com.tzanotto.metayway.petshop.dto.ClienteDetailsDTO;
import com.tzanotto.metayway.petshop.dto.ContatoDTO;
import com.tzanotto.metayway.petshop.dto.EnderecoDTO;
import com.tzanotto.metayway.petshop.model.*;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ClienteMapper implements BaseMapper<ClienteDTO, Cliente> {

    @Override
    public ClienteDTO toDTO(Cliente m) {
        if (m == null) return null;
        Long usuarioId = m.getUsuario() != null ? m.getUsuario().getId() : null;
        return new ClienteDTO(m.getId(), usuarioId, m.getNome(), m.getDataCadastro());
    }

    @Override
    public Cliente toModel(ClienteDTO d) {
        if (d == null) return null;
        Cliente c = new Cliente();
        c.setId(d.id());
        c.setNome(d.nome());
        c.setDataCadastro(d.dataCadastro());
        if (d.usuarioId() != null) {
            Usuario u = new Usuario();
            u.setId(d.usuarioId());
            c.setUsuario(u);
        }
        return c;
    }

    public ClienteDetailsDTO toDetailsDTO(Cliente c) {
        var usuario = c.getUsuario();
        String cpf = usuario != null ? usuario.getCpf() : null;
        var situacao = usuario != null ? usuario.getSituacao() : null;
        Long usuarioId = usuario != null ? usuario.getId() : null;

        List<EnderecoDTO> enderecos = c.getEnderecos().stream()
                .sorted(Comparator.comparing(Endereco::getId))
                .map(e -> new EnderecoDTO(
                        e.getId(), e.getCliente().getId(),
                        e.getLogradouro(),
                        e.getCidade(), e.getBairro(), e.getComplemento(), e.getNumero(),
                        e.getTag()
                ))
                .toList();

        List<ContatoDTO> contatos = c.getContatos().stream()
                .sorted(Comparator.comparing(Contato::getId))
                .map(ct -> new ContatoDTO(
                        ct.getId(),
                        ct.getCliente().getId(),
                        ct.getTag(), ct.getTipo(), ct.getValor()
                ))
                .toList();

        List<ClienteDetailsDTO.PetDTO> pets = c.getPets().stream()
                .sorted(Comparator.comparing(Pet::getId))
                .map(p -> new ClienteDetailsDTO.PetDTO(
                        p.getId(),
                        p.getNome(),
                        p.getDob(),
                        new ClienteDetailsDTO.RacaDTO(
                                p.getRaca() != null ? p.getRaca().getDescricao() : null
                        ),
                        p.getAtendimentos().stream()
                                .sorted(Comparator.comparing(Atendimento::getData).reversed())
                                .map(a -> new ClienteDetailsDTO.AtendimentoDTO(
                                        a.getId(), a.getDescricao(), a.getValor(), a.getData(), a.getTipo(), a.getSituacao()
                                ))
                                .toList()
                ))
                .toList();

        return new ClienteDetailsDTO(
                c.getId(),
                usuarioId,
                c.getNome(),
                cpf,
                situacao,
                c.getDataCadastro(),
                enderecos,
                contatos,
                pets
        );
    }
}
