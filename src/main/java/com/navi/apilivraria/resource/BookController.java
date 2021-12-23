package com.navi.apilivraria.resource;

import com.navi.apilivraria.domain.Book;
import com.navi.apilivraria.dto.BookDTO;
import com.navi.apilivraria.resource.exceptions.ApiErrors;
import com.navi.apilivraria.resource.exceptions.DuplicatedIsbnException;
import com.navi.apilivraria.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;

    private ModelMapper modelMapper;

    public BookController(BookService bookService, ModelMapper modelMapper) {
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO resgisteringBook(@RequestBody @Valid BookDTO bookDTO){
        Book entity = modelMapper.map(bookDTO, Book.class);
        entity = bookService.save(entity);
        return modelMapper.map(entity, BookDTO.class);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO getBookById(@PathVariable Long id){
        Book book = bookService.getById(id).get();
        return modelMapper.map(book, BookDTO.class);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException exception){
        BindingResult bindingResult = exception.getBindingResult();
        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(DuplicatedIsbnException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleDuplicatedIsbnException(DuplicatedIsbnException exception){
        return new ApiErrors(exception);
    }

}
