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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

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
        Book book = registerNewBook();
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
    public void shouldNotSaveABookWithDuplicatedIsbn() {
        Book book = registerNewBook();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        assertThat(exception)
                .isInstanceOf(DuplicatedIsbnException.class)
                .hasMessage("ISBN já cadastrada.");
        Mockito.verify(bookRepository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve obter um livro pelo seu id")
    public void shouldGetABookById() {
        Long id = 1L;
        Book book = registerNewBook();
        book.setId(id);
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = service.getById(id);
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por id quando ele não existe na base")
    public void shouldNotGetABookById() {
        Long id = 1L;
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Book> foundBook = service.getById(id);
        assertThat(foundBook).isEmpty();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteABook(){
        Long id = 1L;
        Book book = registerNewBook();
        book.setId(id);

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(()->service.delete(book));

        Mockito.verify(bookRepository, Mockito.times(1)).delete(book);

    }

    @Test
    @DisplayName("Deve retornar exception ao tentar deletar um livro invalido")
    public void deleteAInvalidBook(){
        Book book = registerNewBook();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, ()-> service.delete(book));

        Mockito.verify(bookRepository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateABook(){
        Long id = 1L;
        Book updatingBook = Book.builder().id(id).build();

        Book updatedBook = registerNewBook();
        updatedBook.setId(id);

        Mockito.when(bookRepository.save(updatingBook)).thenReturn(updatedBook);

        Book book = service.update(updatingBook);

        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
    }

    @Test
    @DisplayName("Deve retornar exception ao tentar atualizar um livro invalido")
    public void updateAInvalidBook(){
        Book book = registerNewBook();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, ()-> service.update(book));

        Mockito.verify(bookRepository, Mockito.never()).save(book);
    }

    private Book registerNewBook() {
        return Book.builder().author("Ivan").title("A volta dos que não foram").isbn("27062001").build();
    }

}
