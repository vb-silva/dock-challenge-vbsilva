package com.dock.api.controllers;

import com.dock.api.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountController {

    @RequestMapping(value = "/account", method = RequestMethod.POST)
    public ResponseEntity<Object> saveAccount(@RequestBody @Valid AccountDto accountDto) {
        long personId = accountDto.getPersonId();
        if (accountService.existsByPersonId(personId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Person already has an account");
        }

        if (!personService.existsByPersonId(personId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Person does not exist");
        }

        AccountModel accountModel = accountService.createAccountModel(accountDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.save(accountModel));
    }
}