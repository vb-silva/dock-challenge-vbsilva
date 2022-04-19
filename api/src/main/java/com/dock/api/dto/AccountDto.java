package com.dock.api.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class AccountDto {

    @NotNull
    private long personId;

    private BigDecimal balance;

    private BigDecimal dailyWithdrawalLimit;

    private Boolean isActive;

    @NotNull
    private String accountType;

    public AccountDto(long personId, String accountType) {
        this.personId = personId;
        this.accountType = accountType;
    }
}