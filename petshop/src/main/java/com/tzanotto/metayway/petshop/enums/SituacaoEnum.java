package com.tzanotto.metayway.petshop.enums;

public enum SituacaoEnum {
    ATIVO, INATIVO, PENDENTE;

    public boolean isAtivo() {
        return this == ATIVO;
    }

    public boolean isInativo() {
        return this == INATIVO;
    }

    public boolean isPendente() {
        return this == PENDENTE;
    }
}
