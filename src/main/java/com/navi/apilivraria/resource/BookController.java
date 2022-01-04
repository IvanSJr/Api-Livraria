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
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    private final ModelMapper modelMapper;

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
        return bookService.getById(id).map(
               book ->  modelMapper.map(book, BookDTO.class)
        ).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookById(@PathVariable Long id){
        Book book = bookService.getById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        bookService.delete(book);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO updateBookById(@PathVariable Long id, BookDTO dto){
        return bookService.getById(id).map(book -> {
            book.setAuthor(dto.getAuthor());
            book.setTitle(dto.getTitle());
            book = bookService.update(book);
            return modelMapper.map(book, BookDTO.class);
        }).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
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
