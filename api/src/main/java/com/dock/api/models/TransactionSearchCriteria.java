package com.dock.api.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class TransactionSearchCriteria {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String startDate;
    private String endDate;
    private long accountId;

    public LocalDateTime getLocalDateTimeStartDate() {
        return LocalDateTime.parse(startDate, formatter);
    }

    public LocalDateTime getLocalDateTimeEndDate() {
        return LocalDateTime.parse(endDate, formatter);
    }

}