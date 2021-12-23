package com.navi.apilivraria.service;

import com.navi.apilivraria.domain.Book;
import com.navi.apilivraria.repository.BookRepository;
import com.navi.apilivraria.resource.exceptions.DuplicatedIsbnException;
import com.navi.apilivraria.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository bookRepository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl(bookRepository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void registerBook(){
        Book book = resgisterNewBook();
        Mockito.when(bookRepository.save(book))
                .thenReturn(
                        Book.builder()
                                .id(1l).title("A volta dos que não foram").author("Ivan").isbn("27062001").build());
        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getAuthor()).isEqualTo("Ivan");
        assertThat(savedBook.getTitle()).isEqualTo("A volta dos que não foram");
        assertThat(savedBook.getIsbn()).isEqualTo("27062001");
    }

    @Test
    @DisplayName("Deve lançar uma mensagem de erro se o ISBN já estiver cadastrado")
    public void shouldNotSaveABookWithDuplicatadeIsbn() {
        Book book = resgisterNewBook();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        assertThat(exception)
                .isInstanceOf(DuplicatedIsbnException.class)
                .hasMessage("ISBN já cadastrada.");
        Mockito.verify(bookRepository, Mockito.never()).save(book);
    }

    private Book resgisterNewBook() {
        return Book.builder().author("Ivan").title("A volta dos que não foram").isbn("27062001").build();
    }

}
