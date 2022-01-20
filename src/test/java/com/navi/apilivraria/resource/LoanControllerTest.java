package com.navi.apilivraria.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navi.apilivraria.domain.Book;
import com.navi.apilivraria.domain.Loan;
import com.navi.apilivraria.dto.LoanDTO;
import com.navi.apilivraria.service.BookService;
import com.navi.apilivraria.service.LoanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    private BookService bookService;
    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("Deve realizar um emprestimo")
    public void makeLoan() throws Exception {
        LoanDTO loanDTO = LoanDTO.builder().isbn("27062001").customer("Ivan").build();
        String json = new ObjectMapper().writeValueAsString(loanDTO);

        Book book = Book.builder().id(1L).isbn("27062001").build();
        BDDMockito.given(bookService.getBookByIsbn(loanDTO.getIsbn()))
                .willReturn(Optional.of(book));

        Loan loan = Loan.builder()
                .id(1L).customer("Ivan").book(book).localDate(LocalDate.now()).build();
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(requestBuilder)
                .andExpect( status().isCreated() )
                .andExpect( content().string("1"));
    }

}
