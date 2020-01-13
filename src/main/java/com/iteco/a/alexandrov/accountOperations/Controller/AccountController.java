package com.iteco.a.alexandrov.accountOperations.Controller;


import com.iteco.a.alexandrov.accountOperations.Entity.AccountEntity;
import com.iteco.a.alexandrov.accountOperations.Service.AccountServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    private final AccountServiceImpl service;

    @Autowired
    AccountController(AccountServiceImpl service) {
        this.service = service;
    }


    @GetMapping("/accounts")
    ResponseEntity<List<AccountEntity>> allAccounts() {
        List<AccountEntity> accounts = service.findAllAccounts();
        if (accounts.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/accounts/{id}")
    ResponseEntity<AccountEntity> accountById(@PathVariable long id) {
        Optional<AccountEntity> accountEntityOptional = service.findAccountById(id);
        if (!accountEntityOptional.isPresent()) {
            log.error("Account with id {} not found.", id);
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(accountEntityOptional.get(), HttpStatus.OK);
    }

    @PostMapping("/accounts")
    AccountEntity newEmployee(@RequestBody AccountEntity newAccount) {
        log.warn("newAccount = " + newAccount);
        return service.saveAccountToDB(newAccount);
    }

    @PutMapping(value = "/accounts/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @RequestBody AccountEntity account) {
        log.info("Updating Account with id {}", id);

        Optional<AccountEntity> optionalAccountEntity = service.findAccountById(id);

        if (!optionalAccountEntity.isPresent()) {
            log.error("Unable to update. Account with id {} not found.", id);
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        AccountEntity accountEntity = optionalAccountEntity.get();

        accountEntity.setAccount(account.getAccount());

        //service.updateAccount(accountEntity);
        return new ResponseEntity<>(accountEntity, HttpStatus.OK);
    }

    // ------------------- Delete a User-----------------------------------------

    @DeleteMapping(value = "/accounts/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {
        log.info("Fetching & Deleting User with id {}", id);

        Optional<AccountEntity> optionalAccountEntity = service.findAccountById(id);
        if (!optionalAccountEntity.isPresent()) {
            log.error("Unable to delete. Account with id {} not found.", id);
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
            service.deleteAccountById(id);
            return new ResponseEntity<AccountEntity>(HttpStatus.NO_CONTENT);
    }

    // ------------------- Delete All Users-----------------------------

    @DeleteMapping(value = "/accounts/")
    public ResponseEntity<AccountEntity> deleteAllUsers() {
        log.info("Deleting All Accounts");
        service.deleteAllAccounts();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
