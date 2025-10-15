package com.tzanotto.metayway.petshop.mapper;

public interface BaseMapper<D, M> {
    D toDTO(M model);
    M toModel(D dto);
}
