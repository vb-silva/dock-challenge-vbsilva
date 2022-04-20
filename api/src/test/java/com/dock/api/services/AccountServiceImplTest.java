package com.dock.api.services;

import com.dock.api.dto.AccountDto;
import com.dock.api.models.AccountModel;
import com.dock.api.models.AccountTypeModel;
import com.dock.api.repositories.AccountRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {
    private AccountRepository accountRepository = mock(AccountRepository.class);
    private Clock clock = mock(Clock.class);
    private final static LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2022, 01, 13, 0, 0, 0, 0);

    private AccountServiceImpl accountService = new AccountServiceImpl(accountRepository, clock);

    @Test
    void save() {
        var validAccountModel = new AccountModel();
        validAccountModel.setIsActive(true);
        validAccountModel.setAccountType("CURRENT");
        validAccountModel.setBalance(BigDecimal.ZERO);
        validAccountModel.setDailyWithdrawalLimit(BigDecimal.valueOf(1000L));
        validAccountModel.setCreationUTC(LOCAL_DATE_TIME);
        validAccountModel.setPersonId(1L);

        var expectedAccountModel = validAccountModel;
        expectedAccountModel.setAccountId(1L);

        doReturn(LOCAL_DATE_TIME.toInstant(ZoneOffset.UTC)).when(clock).instant();
        doReturn(ZoneOffset.UTC).when(clock).getZone();

        when(accountRepository.save(eq(validAccountModel))).thenReturn(validAccountModel);
        var accountModel = accountService.save(validAccountModel);
        assertThat(expectedAccountModel).usingRecursiveComparison().ignoringFields("accountId").isEqualTo(accountModel);

        verify(accountRepository, times(1)).save(validAccountModel);
    }

    @Test
    void existsByPersonIdShouldReturnTrue() {
        var validPersonId = 1L;

        when(accountRepository.existsByPersonId(validPersonId)).thenReturn(true);

        assertTrue(accountService.existsByPersonId(validPersonId));
        verify(accountRepository, times(1)).existsByPersonId(validPersonId);
    }

    @Test
    void existsByPersonIdShouldReturnFalse() {
        var nonExistentPersonId = 1L;

        when(accountRepository.existsByPersonId(nonExistentPersonId)).thenReturn(false);

        assertFalse(accountService.existsByPersonId(nonExistentPersonId));
        verify(accountRepository, times(1)).existsByPersonId(nonExistentPersonId);
    }

    @Test
    void findByIdShouldFindAccount() {
        var validAccountId = 1L;
        var optionalAccount = Optional.of(new AccountModel());

        when(accountRepository.findById(validAccountId)).thenReturn(optionalAccount);
        var account = accountService.findById(validAccountId);

        assertThat(account).isPresent();
        assertThat(optionalAccount.get()).usingRecursiveComparison().isEqualTo(account.get());
        verify(accountRepository, times(1)).findById(validAccountId);
    }

    @Test
    void findByIdShouldNotFindAccount() {
        var validAccountId = 1L;

        when(accountRepository.findById(validAccountId)).thenReturn(Optional.empty());
        var account = accountService.findById(validAccountId);

        assertThat(account).isEmpty();
        verify(accountRepository, times(1)).findById(validAccountId);
    }

    @Test
    void createCurrentAccountModelShouldRunSuccessfully() {
        var accountDto = new AccountDto(1L, "CURRENT");

        BigDecimal dailyWithdrawalLimit = BigDecimal.valueOf(1000.0);
        AccountModel expectedAccountModel = new AccountModel();
        expectedAccountModel.setDailyWithdrawalLimit(dailyWithdrawalLimit);
        expectedAccountModel.setPersonId(1L);
        expectedAccountModel.setBalance(BigDecimal.ZERO);
        expectedAccountModel.setIsActive(Boolean.TRUE);
        expectedAccountModel.setCreationUTC(LOCAL_DATE_TIME);
        expectedAccountModel.setAccountType("current");

        doReturn(LOCAL_DATE_TIME.toInstant(ZoneOffset.UTC)).when(clock).instant();
        doReturn(ZoneOffset.UTC).when(clock).getZone();
        var accountModel = accountService.createAccountModel(accountDto);

        assertThat(expectedAccountModel).usingRecursiveComparison().isEqualTo(accountModel);
    }

    @Test
    void createSalaryAccountModelShouldRunSuccessfully() {
        var accountDto = new AccountDto(1L, "SALARY");

        BigDecimal dailyWithdrawalLimit = BigDecimal.valueOf(100.0);
        AccountModel expectedAccountModel = new AccountModel();
        expectedAccountModel.setDailyWithdrawalLimit(dailyWithdrawalLimit);
        expectedAccountModel.setPersonId(1L);
        expectedAccountModel.setBalance(BigDecimal.ZERO);
        expectedAccountModel.setIsActive(Boolean.TRUE);
        expectedAccountModel.setCreationUTC(LOCAL_DATE_TIME);
        expectedAccountModel.setAccountType("salary");

        doReturn(LOCAL_DATE_TIME.toInstant(ZoneOffset.UTC)).when(clock).instant();
        doReturn(ZoneOffset.UTC).when(clock).getZone();
        var accountModel = accountService.createAccountModel(accountDto);

        assertThat(expectedAccountModel).usingRecursiveComparison().isEqualTo(accountModel);
    }

    @Test
    void createSavingsAccountModelShouldRunSuccessfully() {
        var accountDto = new AccountDto(1L, "SAVINGS");

        BigDecimal dailyWithdrawalLimit = BigDecimal.valueOf(500.0);
        AccountModel expectedAccountModel = new AccountModel();
        expectedAccountModel.setDailyWithdrawalLimit(dailyWithdrawalLimit);
        expectedAccountModel.setPersonId(1L);
        expectedAccountModel.setBalance(BigDecimal.ZERO);
        expectedAccountModel.setIsActive(Boolean.TRUE);
        expectedAccountModel.setCreationUTC(LOCAL_DATE_TIME);
        expectedAccountModel.setAccountType("savings");

        doReturn(LOCAL_DATE_TIME.toInstant(ZoneOffset.UTC)).when(clock).instant();
        doReturn(ZoneOffset.UTC).when(clock).getZone();
        var accountModel = accountService.createAccountModel(accountDto);

        assertThat(expectedAccountModel).usingRecursiveComparison().isEqualTo(accountModel);
    }
}