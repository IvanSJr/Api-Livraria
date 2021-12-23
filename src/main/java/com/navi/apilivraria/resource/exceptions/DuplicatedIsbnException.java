package com.navi.apilivraria.resource.exceptions;

import com.navi.apilivraria.domain.Book;

public class DuplicatedIsbnException extends RuntimeException{
    public DuplicatedIsbnException(String s) {
        super(s);
    }
}
