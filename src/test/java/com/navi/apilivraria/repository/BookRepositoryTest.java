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

import java.util.Optional;

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
        Book book = registerABook(isbn);
        testEntityManager.persist(book);

        boolean exists = bookRepository.existsByIsbn(isbn);

        assertThat(exists).isTrue();

    }

    private Book registerABook(String isbn) {
        return Book.builder().isbn(isbn).author("Ivan").title("Livro do Ivan").build();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um livro com um ISBN informado")
    public void returnFalseWhenIsbnDoesntExists() {

        String isbn = "27062001";
        Book book = registerABook(isbn);
        testEntityManager.persist(book);

        boolean exists = bookRepository.existsByIsbn("27052001");

        assertThat(exists).isFalse();

    }

    @Test
    @DisplayName("Deve obter um livro pelo seu id")
    public void findByIdTest(){
        Book book = registerABook("555");
        testEntityManager.persist(book);

        Optional<Book> foundBook = bookRepository.findById(book.getId());

        assertThat(foundBook).isPresent();

    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        Book book = registerABook("123");

        Book savedBook = bookRepository.save(book);

        assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){
        Book book = registerABook("458");
        testEntityManager.persist(book);

        Book foundBook = testEntityManager.find(Book.class, book.getId());
        bookRepository.delete(foundBook);

        Book deletedBook = testEntityManager.find(Book.class, book.getId());

        assertThat(deletedBook).isNull();
    }

}
