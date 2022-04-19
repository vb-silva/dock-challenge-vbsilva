package com.dock.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountTypeModel {
    CURRENT("current"),
    SAVINGS("savings"),
    SALARY("salary");

    private String AccountTypeName;
}