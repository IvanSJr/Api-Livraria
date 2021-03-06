package com.navi.apilivraria.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navi.apilivraria.domain.Book;
import com.navi.apilivraria.dto.BookDTO;
import com.navi.apilivraria.resource.exceptions.DuplicatedIsbnException;
import com.navi.apilivraria.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;

    @Test
    @DisplayName("Deve cadastrar um novo livro")
    public void registeringBook() throws Exception{

        BookDTO bookDTO = createBookDTO();

        Book savedBook = Book.builder()
                .id(1L).author("Ivan J??nior").title("Codando para o n??cleo").isbn("27062001").build();
        BDDMockito.given(bookService.save(Mockito.any(Book.class)))
                .willReturn(savedBook);

        String json = new ObjectMapper().writeValueAsString(bookDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("title").value(bookDTO.getTitle()))
                .andExpect(jsonPath("author").value(bookDTO.getAuthor()))
                .andExpect(jsonPath("isbn").value(bookDTO.getIsbn()));
    }

    @Test
    @DisplayName("Deve lan??ar um erro de valida????o quando os requisitos de cadastro n??o forem cumpridos")
    public void registeringInvalidBook() throws Exception{

        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(6)));

    }

    @Test
    @DisplayName("Deve lan??ar um erro se tentarmos registrar um livro com uma ISBN j?? cadastrada")
    public void registireingBookInvalidIsbn() throws Exception{
        String msgError = "ISBN j?? cadastrada.";
        BookDTO bookDTO = createBookDTO();
        String json = new ObjectMapper().writeValueAsString(bookDTO);
        BDDMockito.given(bookService.save(Mockito.any(Book.class)))
                .willThrow(new DuplicatedIsbnException(msgError));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(msgError));

    }

    @Test
    @DisplayName("Deve buscar um livro pelo seu id")
    public void getBookDetailById() throws Exception{
        Long id = 1L;
        Book book = Book.builder()
                .id(id)
                .title(createBookDTO().getTitle())
                .isbn(createBookDTO().getIsbn())
                .author(createBookDTO().getAuthor()).build();

        BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder bookRequest = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(bookRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createBookDTO().getTitle()))
                .andExpect(jsonPath("author").value(createBookDTO().getAuthor()))
                .andExpect(jsonPath("isbn").value(createBookDTO().getIsbn()));

    }

    @Test
    @DisplayName("Deve retornar resource not found se o id do livro n??o existe")
    public void getBookDetailByIdNoExist() throws Exception{
        Long id = 213213213L;

        BDDMockito.given(bookService.getById(id)).willReturn(Optional.empty());

        MockHttpServletRequestBuilder bookRequest = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(bookRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar resourcer not found se o id do livro n??o existe")
    public void deleteBookByIdNoExist() throws Exception{
        Long id = 1L;

        BDDMockito.given(bookService.getById(id)).willReturn(Optional.empty());

        MockHttpServletRequestBuilder bookRequest = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + id));

        mvc.perform(bookRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um livro pelo seu id")
    public void updateBookById() throws Exception{
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createBookDTO());
        Book book = Book.builder()
               .id(id)
               .title("Codando para o n??cleo de desenvolvimento da unime")
               .author("Ivan Santos de Jesus J??nior")
               .isbn("27062001").build();
        BDDMockito.given(bookService.getById(anyLong())).willReturn(Optional.of(book));
        Book updatedBook = Book.builder().id(id).author("Ivan J??nior").title("Codando para o n??cleo").isbn("27062001")
                .build();
        BDDMockito.given(bookService.update(book)).willReturn(updatedBook);

        MockHttpServletRequestBuilder bookRequest = MockMvcRequestBuilders
               .put(BOOK_API.concat("/"+id))
               .content(json)
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(bookRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createBookDTO().getTitle()))
                .andExpect(jsonPath("author").value(createBookDTO().getAuthor()))
                .andExpect(jsonPath("isbn").value(createBookDTO().getIsbn()));
    }

    @Test
    @DisplayName("Deve retornar 404 se o id do livro n??o existir")
    public void noUpdateBookByUnexcitingId() throws Exception{
        String json = new ObjectMapper().writeValueAsString(createBookDTO());
        BDDMockito.given(bookService.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder bookRequest = MockMvcRequestBuilders
                .put(BOOK_API.concat("/"+1L))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(bookRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve filtrar os livros de acordo com o parametro passado")
    public void filterBooksTest() throws Exception{
        Long id = 1L;

        Book book = Book.builder().id(id)
                .isbn(createBookDTO().getIsbn())
                .author(createBookDTO().getAuthor())
                .title(createBookDTO().getTitle()).build();

        BDDMockito.given(bookService.findBook(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));

        String queryString = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    private BookDTO createBookDTO() {
        return BookDTO.builder()
                .author("Ivan J??nior").title("Codando para o n??cleo").isbn("27062001")
                .build();
    }

}
