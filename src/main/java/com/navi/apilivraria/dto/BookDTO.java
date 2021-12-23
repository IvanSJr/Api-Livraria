package com.navi.apilivraria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long id;

    @NotEmpty
    @NotNull
    @Length(min = 1)
    private String title;
    @NotEmpty
    @NotNull
    @Length(min = 1)
    private String author;

    @NotEmpty
    @NotNull
    private String isbn;
}
