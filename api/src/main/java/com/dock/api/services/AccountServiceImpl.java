package com.dock.api.services;

import com.dock.api.dto.AccountDto;
import com.dock.api.models.AccountModel;
import com.dock.api.models.AccountTypeModel;
import com.dock.api.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;
    private final Clock clock;

    private static final BigDecimal CURRENT_ACCOUNT_LIMIT = BigDecimal.valueOf(1000.0);
    private static final BigDecimal SAVINGS_ACCOUNT_LIMIT = BigDecimal.valueOf(500.0);
    private static final BigDecimal SALARY_ACCOUNT_LIMIT = BigDecimal.valueOf(100.0);

    @Transactional
    @Override
    public AccountModel save(AccountModel account) {
        return accountRepository.save(account);
    }

    @Override
    public boolean existsByPersonId(long personId) {
        return accountRepository.existsByPersonId(personId);
    }

    @Override
    public Optional<AccountModel> findById(long accountId) {
        return accountRepository.findById(accountId);
    }

    @Override
    public AccountModel createAccountModel(AccountDto accountDto) {
        AccountTypeModel accountType = AccountTypeModel.valueOf(accountDto.getAccountType());
        BigDecimal dailyWithdrawalLimit = getDailyWithdrawalLimit(accountType);

        LocalDateTime creationUTC = LocalDateTime.now(clock);

        AccountModel accountModel = new AccountModel();
        accountModel.setDailyWithdrawalLimit(dailyWithdrawalLimit);
        accountModel.setPersonId(accountDto.getPersonId());
        accountModel.setBalance(BigDecimal.ZERO);
        accountModel.setIsActive(Boolean.TRUE);
        accountModel.setCreationUTC(creationUTC);
        accountModel.setAccountType(accountType.getAccountTypeName());
        return accountModel;
    }

    private BigDecimal getDailyWithdrawalLimit(AccountTypeModel accountTypeEnum) {
        switch (accountTypeEnum) {
            case CURRENT:
                return CURRENT_ACCOUNT_LIMIT;
            case SALARY:
                return SALARY_ACCOUNT_LIMIT;
            case SAVINGS:
                return SAVINGS_ACCOUNT_LIMIT;
            default:
                return BigDecimal.ZERO;
        }
    }
}