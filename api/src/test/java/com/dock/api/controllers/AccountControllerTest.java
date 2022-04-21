package com.dock.api.controllers;

import com.dock.api.dto.AccountDto;
import com.dock.api.dto.TransactionDto;
import com.dock.api.models.AccountBalanceModel;
import com.dock.api.models.AccountModel;
import com.dock.api.models.TransactionModel;
import com.dock.api.services.AccountServiceImpl;
import com.dock.api.services.PersonServiceImpl;
import com.dock.api.services.TransactionServiceImpl;
import net.bytebuddy.pool.TypePool;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountControllerTest {
    private final AccountServiceImpl accountService = mock(AccountServiceImpl.class);
    private final PersonServiceImpl personService = mock(PersonServiceImpl.class);
    private final TransactionServiceImpl transactionService = mock(TransactionServiceImpl.class);

    private Clock clock = mock(Clock.class);

    private final static LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2022, 01, 13, 0, 0, 0, 0);

    private AccountController accountController = new AccountController(accountService, personService, transactionService);

    @Test
    void saveAccountShouldRunSuccessfully() {
        AccountDto accountDto = new AccountDto(1L, "CURRENT");

        AccountModel accountModel = new AccountModel();
        accountModel.setDailyWithdrawalLimit(BigDecimal.valueOf(1000.0));
        accountModel.setPersonId(accountDto.getPersonId());
        accountModel.setBalance(BigDecimal.ZERO);
        accountModel.setIsActive(Boolean.TRUE);
        accountModel.setCreationUTC(LOCAL_DATE_TIME);
        accountModel.setAccountType("current");

        AccountModel savedAccountModel = accountModel;
        savedAccountModel.setAccountId(1L);

        var expectedResponse = ResponseEntity.status(HttpStatus.CREATED).body(savedAccountModel);

        when(personService.existsById(1L)).thenReturn(true);
        when(accountService.existsByPersonId(1L)).thenReturn(false);
        when(accountService.createAccountModel(accountDto)).thenReturn(accountModel);
        when(accountService.save(accountModel)).thenReturn(savedAccountModel);

        var response = accountController.saveAccount(accountDto);
        assertThat(expectedResponse).usingRecursiveComparison().isEqualTo(response);

        verify(accountService, times(1)).existsByPersonId(1L);
        verify(personService, times(1)).existsById(1L);
    }

    @Test
    void saveAccountShouldFailByPersonAlreadyHasAnAccount() {
        AccountDto accountDto = new AccountDto(1L, "CURRENT");

        var expectedResponse = ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Person already has an account");

        when(accountService.existsByPersonId(1L)).thenReturn(true);

        var response = accountController.saveAccount(accountDto);
        assertThat(expectedResponse).usingRecursiveComparison().isEqualTo(response);

        verify(accountService, times(1)).existsByPersonId(1L);
    }

    @Test
    void saveAccountShouldFailByPersonDoesNotExist() {
        AccountDto accountDto = new AccountDto(1L, "CURRENT");

        var expectedResponse = ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Person does not exist");

        when(accountService.existsByPersonId(1L)).thenReturn(false);
        when(personService.existsById(1L)).thenReturn(false);

        var response = accountController.saveAccount(accountDto);
        assertThat(expectedResponse).usingRecursiveComparison().isEqualTo(response);

        verify(accountService, times(1)).existsByPersonId(1L);
        verify(personService, times(1)).existsById(1L);
    }

    @Test
    void deactivateAccountShouldRunSuccessfully() {
        var activeAccountId = 1L;
        AccountModel activeAccount = new AccountModel();
        activeAccount.setIsActive(true);

        AccountModel inactiveAccount = new AccountModel();
        inactiveAccount.setIsActive(false);

        var expectedResponse = ResponseEntity.status(HttpStatus.OK).body(inactiveAccount);

        when(accountService.findById(activeAccountId)).thenReturn(Optional.of(activeAccount));
        when(accountService.save(inactiveAccount)).thenReturn(inactiveAccount);

        var response = accountController.deactivateAccount(activeAccountId);
        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);

        verify(accountService, times(1)).findById(activeAccountId);
        verify(accountService, times(1)).save(inactiveAccount);
    }

    @Test
    void deactivateAccountShouldFailWhenAccountDoesNotExist() {
        var nonExistentAccountId = 1L;

        var expectedResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request: Account does not exist");

        when(accountService.findById(nonExistentAccountId)).thenReturn(Optional.empty());

        var response = accountController.deactivateAccount(nonExistentAccountId);
        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);

        verify(accountService, times(1)).findById(nonExistentAccountId);
    }

    @Test
    void deactivateAccountShouldFailWhenAccountIsInactive() {
        var inactiveAccountId = 1L;
        var inactiveAccount = new AccountModel();
        inactiveAccount.setIsActive(false);

        var expectedResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request: Account is inactive");

        when(accountService.findById(inactiveAccountId)).thenReturn(Optional.of(inactiveAccount));

        var response = accountController.deactivateAccount(inactiveAccountId);
        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);

        verify(accountService, times(1)).findById(inactiveAccountId);
    }

    @Test
    void getAccountBalanceShouldRunSuccessfully() {
        var account = new AccountModel();
        account.setBalance(BigDecimal.valueOf(1000L));

        var accountBalance = new AccountBalanceModel(account.getBalance());

        when(accountService.findById(1L)).thenReturn(Optional.of(account));

        var expectedResponse = ResponseEntity.status(HttpStatus.OK).body(accountBalance);

        var response = accountController.getAccountBalance(1L);
        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
        verify(accountService, times(1)).findById(1L);
    }

    @Test
    void getAccountBalanceShouldFailByNonExistentAccount() {
        when(accountService.findById(1L)).thenReturn(Optional.empty());

        var expectedResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request: Account does not exist");

        var response = accountController.getAccountBalance(1L);
        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
        verify(accountService, times(1)).findById(1L);
    }

    @Test
    void transactShouldDepositSuccessfully() {
        var validAccountId = 1L;
        var depositTransaction = new TransactionDto(1000, "DEPOSIT");
        var transactionModel = new TransactionModel();
        transactionModel.setTransactionTimeUTC(LOCAL_DATE_TIME);
        transactionModel.setAccountId(validAccountId);
        transactionModel.setAmount(BigDecimal.valueOf(10.00));
        var account = new AccountModel();
        account.setIsActive(true);

        when(accountService.findById(validAccountId)).thenReturn(Optional.of(account));
        doNothing().when(transactionService).createTransactionModel(depositTransaction, validAccountId);
        account.setBalance(transactionModel.getAmount());
        var expectedResponse = ResponseEntity.status(HttpStatus.OK).body(account);
        var response = accountController.transact(validAccountId, depositTransaction);
    }

    @Test
    void getAccountExtract() {
    }

}