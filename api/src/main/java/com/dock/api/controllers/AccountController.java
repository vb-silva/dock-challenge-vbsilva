package com.dock.api.controllers;

import com.dock.api.dto.AccountDto;
import com.dock.api.dto.TransactionDto;
import com.dock.api.models.*;
import com.dock.api.services.AccountServiceImpl;
import com.dock.api.services.PersonServiceImpl;
import com.dock.api.services.TransactionService;
import com.dock.api.services.TransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountController {

    final private AccountServiceImpl accountService;
    final private PersonServiceImpl personService;

    final private TransactionServiceImpl transactionService;

    @RequestMapping(value = "/account", method = RequestMethod.POST)
    public ResponseEntity<Object> saveAccount(@RequestBody @Valid AccountDto accountDto) {
        long personId = accountDto.getPersonId();
        if (accountService.existsByPersonId(personId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Person already has an account");
        }

        if (!personService.existsById(personId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Person does not exist");
        }

        AccountModel accountModel = accountService.createAccountModel(accountDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.save(accountModel));
    }

    @RequestMapping(value = "/account/{accountId}/deactivate", method = RequestMethod.PUT)
    public ResponseEntity<Object> deactivateAccount(@PathVariable long accountId) {
        if (accountId < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request: Invalid account id");
        }

        Optional<AccountModel> optionalAccountModel = accountService.findById(accountId);
        if (!optionalAccountModel.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request: Account does not exist");
        }

        AccountModel accountModel = optionalAccountModel.get();
        if (!accountModel.getIsActive()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request: Account is inactive");
        }

        accountModel.setIsActive(false);
        return ResponseEntity.status(HttpStatus.OK).body(accountService.save(accountModel));
    }

    @RequestMapping(value = "/account/{accountId}/balance", method = RequestMethod.GET)
    public ResponseEntity<Object> getAccountBalance(@PathVariable long accountId) {
        if (accountId < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request: Invalid account id");
        }

        Optional<AccountModel> optionalAccountModel = accountService.findById(accountId);
        if (!optionalAccountModel.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request: Account does not exist");
        }

        AccountBalanceModel accountBalanceModel = new AccountBalanceModel(optionalAccountModel.get().getBalance());
        return ResponseEntity.status(HttpStatus.OK).body(accountBalanceModel);
    }

    @Transactional
    @RequestMapping(value = "/account/{accountId}/transaction", method = RequestMethod.PUT)
    public ResponseEntity<Object> depositAmount(
            @PathVariable long accountId,
            @RequestBody @Valid TransactionDto transactionDto
    ) {
        Optional<AccountModel> optionalAccountModel = accountService.findById(accountId);
        if (!optionalAccountModel.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request: Account does not exist");
        }

        AccountModel accountModel = optionalAccountModel.get();
        if (!accountModel.getIsActive()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request: Account is inactive");
        }

        TransactionTypeModel transactionType = TransactionTypeModel.valueOf(transactionDto.getTransactionType());
        if (transactionType == TransactionTypeModel.WITHDRAW) {
            BigDecimal amount = BigDecimal.valueOf(transactionDto.getAmount()/100.0);
            if (amount.compareTo(accountModel.getDailyWithdrawalLimit()) > 0 ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request: Amount is greater than account withdrawal limit");
            }

            if (amount.compareTo(accountModel.getBalance()) > 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request: Amount is greater than account balance");
            }
        }

        TransactionModel transactionModel = transactionService.createTransactionModel(transactionDto, accountId);
        transactionService.save(transactionModel);

        BigDecimal newBalance = accountModel.getBalance().add(transactionModel.getAmount());
        accountModel.setBalance(newBalance);
        return ResponseEntity.status(HttpStatus.OK).body(accountService.save(accountModel));
    }
}