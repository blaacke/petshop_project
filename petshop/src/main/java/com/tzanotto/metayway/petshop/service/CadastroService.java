package com.tzanotto.metayway.petshop.service;

import com.tzanotto.metayway.petshop.dto.NewUserDTO;
import com.tzanotto.metayway.petshop.dto.UsuarioCreateResponse;
import com.tzanotto.metayway.petshop.exceptions.DuplicatedCpfException;
import com.tzanotto.metayway.petshop.mapper.ContatoMapper;
import com.tzanotto.metayway.petshop.mapper.PetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CadastroService {

    private final UsuarioService usuarioService;
    private final ClienteService clienteService;
    private final ContatoService contatoService;
    private final PetService petService;
    private final RacaService racaService;
    private final ContatoMapper contatoMapper;
    private final PetMapper petMapper;

    @Transactional
    public UsuarioCreateResponse cadastrar(NewUserDTO req) {
        var documento = req.getUsuario().cpf();
        var userOpt = usuarioService.findByCpf(documento);
        if(userOpt.isPresent()) {
            throw new DuplicatedCpfException(documento);
        }
        var response = usuarioService.createWithApprovalFlow(req.getUsuario());

        var usuario = req.getUsuario();
        var perfil = usuario.perfil();


        if (perfil.isCliente()) {
            var contato = req.getContato();
            var pet = req.getPet();
            var cliente = clienteService.criarNovoCliente(response.entity());

            if (contato != null) {
                var contatoModel = contatoMapper.toModel(contato);
                contatoModel.setCliente(cliente);
                contatoService.criarNovoContato(contatoModel);
            }

            if (pet != null) {
                var raca =racaService.resolveRaca(pet.getDescricao());
                var petModel = petMapper.toModelFromFront(pet);
                petModel.setRaca(raca);
                petModel.setCliente(cliente);
                petService.criarNovoPet(petModel);
            }
        }

       return response.response();
    }
}
