package com.dock.api.services;

import com.dock.api.models.AccountModel;

import java.util.Optional;

public interface AccountService {

    public AccountModel save(AccountModel account);

    public AccountModel createAccountModel(AccountDto accountDto);

    public boolean existsByPersonId(long personId);

}