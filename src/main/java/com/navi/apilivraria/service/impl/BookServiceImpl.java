package com.navi.apilivraria.service.impl;

import com.navi.apilivraria.domain.Book;
import com.navi.apilivraria.repository.BookRepository;
import com.navi.apilivraria.resource.exceptions.DuplicatedIsbnException;
import com.navi.apilivraria.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;

public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())){
            throw new DuplicatedIsbnException("ISBN já cadastrada.");
        }
        return bookRepository.save(book);
    }
}
