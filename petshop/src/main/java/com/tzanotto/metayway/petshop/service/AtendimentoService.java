package com.tzanotto.metayway.petshop.service;

import com.tzanotto.metayway.petshop.dto.AtendimentoAdminDTO;
import com.tzanotto.metayway.petshop.dto.AtendimentoDTO;
import com.tzanotto.metayway.petshop.enums.AtendimentoSitEnum;
import com.tzanotto.metayway.petshop.mapper.AtendimentoMapper;
import com.tzanotto.metayway.petshop.model.Atendimento;
import com.tzanotto.metayway.petshop.model.Cliente;
import com.tzanotto.metayway.petshop.model.Contato;
import com.tzanotto.metayway.petshop.repository.AtendimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AtendimentoService extends  AbstractService<AtendimentoRepository, Atendimento, AtendimentoDTO, Long> {

    public AtendimentoService(AtendimentoRepository repository, AtendimentoMapper mapper) {
        super(repository, mapper);
    }

    public List<AtendimentoDTO> findAllByDateRange(LocalDateTime start, LocalDateTime end, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return repository.findByDataBetween(start, end, pageable)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<LocalDateTime> getBookedDateTimes(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.atTime(LocalTime.MAX);
        return repository.findAllByDataBetween(start, end)
                .stream().map(Atendimento::getData).toList();
    }

    public List<AtendimentoAdminDTO> findAdminByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.atTime(LocalTime.MAX);
        List<Atendimento> list = repository.findAllByDataBetween(start, end);
        return list.stream().map(this::toAdminDTO).toList();
    }

    private AtendimentoAdminDTO toAdminDTO(Atendimento a) {
        var p = a.getPet();
        var c = p != null ? p.getCliente() : null;
        var u = c != null ? c.getUsuario() : null;

        AtendimentoAdminDTO.PetDTO pet = new AtendimentoAdminDTO.PetDTO(
                p != null ? p.getId() : null,
                p != null ? p.getNome() : null
        );

        AtendimentoAdminDTO.ContatoDTO contato = pickContato(c);

        AtendimentoAdminDTO.TutorDTO tutor = new AtendimentoAdminDTO.TutorDTO(
                c != null ? c.getId() : null,
                c != null ? c.getNome() : (u != null ? u.getNome() : null),
                u != null ? u.getCpf() : null,
                contato
        );

        return new AtendimentoAdminDTO(
                a.getId(),
                a.getData(),
                a.getTipo(),
                a.getValor(),
                a.getSituacao(),
                pet,
                tutor
        );
    }

    private AtendimentoAdminDTO.ContatoDTO pickContato(Cliente c) {
        if (c == null || c.getContatos() == null || c.getContatos().isEmpty()) return null;
        Contato chosen = c.getContatos().stream()
                .filter(ct -> ct.getTag() != null && ct.getTag().isPrincipal())
                .findFirst()
                .orElse(c.getContatos().stream().findFirst().get());

        return new AtendimentoAdminDTO.ContatoDTO(
                chosen.getTipo() != null ? chosen.getTipo().name() : null,
                chosen.getValor()
        );
    }

    @Transactional
    public void updateSituacao(Long id, AtendimentoSitEnum situacao) {
        var at = repository.findById(id).orElseThrow();
        at.setSituacao(situacao);
        repository.save(at);
    }
}
