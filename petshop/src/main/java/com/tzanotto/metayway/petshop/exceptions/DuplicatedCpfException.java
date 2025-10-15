package com.tzanotto.metayway.petshop.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class DuplicatedCpfException extends RuntimeException{
    public DuplicatedCpfException(String cpf) {
        super("CPF já cadastrado: " + cpf);
    }
}
