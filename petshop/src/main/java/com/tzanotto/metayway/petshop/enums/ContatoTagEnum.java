package com.tzanotto.metayway.petshop.enums;

public enum ContatoTagEnum {
    principal("Principal"), emergencia("Emergência"), trabalho("Trabalho"), outro("Outro");

    ContatoTagEnum(String label) {
    }

    public boolean isPrincipal() {
        return this == principal;
    }
}
