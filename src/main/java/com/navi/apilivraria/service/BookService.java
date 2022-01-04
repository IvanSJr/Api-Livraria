package com.navi.apilivraria.service;

import com.navi.apilivraria.domain.Book;

import java.util.Optional;

public interface BookService {

    Book save(Book book);

    Optional<Book> getById(Long id);

    void delete(Book book);

    Book update(Book book);
}
