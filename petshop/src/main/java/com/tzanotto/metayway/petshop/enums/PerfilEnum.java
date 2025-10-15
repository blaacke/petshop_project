package com.tzanotto.metayway.petshop.enums;

public enum PerfilEnum {
    cliente, admin;

    public boolean isCliente() {
        return this == cliente;
    }

    public boolean isAdmin() {
        return this == admin;
    }
}
