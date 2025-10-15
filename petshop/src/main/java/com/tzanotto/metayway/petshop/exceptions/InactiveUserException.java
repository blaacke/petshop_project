package com.tzanotto.metayway.petshop.exceptions;

public class InactiveUserException extends RuntimeException {
    public InactiveUserException(String cpf) {
        super("Usuário " + cpf + " inativo, contate um administrador");
    }
}
