package com.dock.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class TransactionDto {

    @NotNull
    @Range(min = 1L, max = 1000000000000L, message = "Bad Request: Invalid amount")
    private long amount;

    @NotNull
    private String transactionType;
}