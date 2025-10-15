package com.tzanotto.metayway.petshop.service;

import com.tzanotto.metayway.petshop.mapper.BaseMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractService<R extends JpaRepository<M, ID>, M , D, ID> {

    public AbstractService() {
    }

    public AbstractService(R repository, BaseMapper<D, M> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    protected R repository;
    protected BaseMapper<D, M> mapper;

    public D create(D dto) {
        M model = mapper.toModel(dto);
        return mapper.toDTO(repository.save(model));
    }


    public List<D> findAllDTO() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<D> findAllDTO(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<M> result = repository.findAll(pageable);
        return result.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public D findById(ID id) {
        M model = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não encontrado: " + id));
        return mapper.toDTO(model);
    }

    public D update(ID id, D dto) {
        M existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não encontrado: " + id));
        M toSave = mapper.toModel(dto);
        return mapper.toDTO(repository.save(toSave));
    }

    public void delete(ID id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Não encontrado: " + id);
        }
        repository.deleteById(id);
    }
}