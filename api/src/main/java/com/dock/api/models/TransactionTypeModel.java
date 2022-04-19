package com.dock.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionTypeModel {
    DEPOSIT("deposit"),
    WITHDRAW("withdraw");

    private String transactionTypeName;
}