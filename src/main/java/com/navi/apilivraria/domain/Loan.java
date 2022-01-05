package com.navi.apilivraria.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    private Long id;
    private String customer;
    private Book book;
    private LocalDate localDate;
    private Boolean returned;

}
