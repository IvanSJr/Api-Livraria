package com.navi.apilivraria.resource;

import com.navi.apilivraria.domain.Book;
import com.navi.apilivraria.domain.Loan;
import com.navi.apilivraria.dto.LoanDTO;
import com.navi.apilivraria.service.BookService;
import com.navi.apilivraria.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO loanDTO){
        Book book = bookService.getBookByIsbn(loanDTO.getIsbn())
                .orElseThrow( ()-> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
        Loan loan = Loan.builder()
                .book(book)
                .customer(loanDTO.getCustomer())
                .localDate(LocalDate.now())
                .build();

        loan = loanService.save(loan);
        return loan.getId();
    }

}
