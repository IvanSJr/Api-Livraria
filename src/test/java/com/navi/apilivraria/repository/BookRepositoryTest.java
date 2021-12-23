package com.navi.apilivraria.repository;

import com.navi.apilivraria.domain.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro com um ISBN informado")
    public void returnTrueWhenIsbnExists() {

        String isbn = "27062001";
        Book book = Book.builder().isbn(isbn).author("Ivan").title("Livro do Ivan").build();
        testEntityManager.persist(book);

        boolean exists = bookRepository.existsByIsbn(isbn);

        assertThat(exists).isTrue();

    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um livro com um ISBN informado")
    public void returnFalseWhenIsbnDoesntExists() {

        String isbn = "27062001";
        Book book = Book.builder().isbn("27062002").author("Ivan").title("Livro do Ivan").build();
        testEntityManager.persist(book);

        boolean exists = bookRepository.existsByIsbn(isbn);

        assertThat(exists).isFalse();

    }

}
