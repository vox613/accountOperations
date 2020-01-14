package com.iteco.a.alexandrov.accountOperations.Controller;


import com.iteco.a.alexandrov.accountOperations.Entity.AccountEntity;
import com.iteco.a.alexandrov.accountOperations.Errors.CustomErrorResponse;
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
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/accounts/{id}")
    ResponseEntity<?> accountById(@PathVariable long id) {
        return service.checkAccountExist(id);
    }

    @PostMapping("/accounts")
    ResponseEntity<?> newAccount(@RequestBody AccountEntity newAccount) {
        return service.checkAndCreateNewAccount(newAccount);
    }

    @PutMapping(value = "/accounts/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable long id, @RequestBody AccountEntity account) {
        log.info("Updating Account with id {}", id);

        Optional<AccountEntity> optionalAccountEntity = service.findAccountById(id);

        if (!optionalAccountEntity.isPresent()) {
            log.error("Unable to update. Account with id {} not found.", id);
            return new ResponseEntity<>(
                    new CustomErrorResponse(String.format("Unable to update. Account with id: %d not found!", id)),
                    HttpStatus.NOT_FOUND);
        }

        AccountEntity accountEntity = optionalAccountEntity.get();
        if (account.getAccount() < 0) {
            log.error("Account sum must bee >= 0 incoming value: " + account.getAccount());
            return new ResponseEntity<>(
                    String.format(
                            "Unable to update. Account sum must be positive! Incoming value: %d",
                            account.getAccount()
                    ), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        accountEntity.setAccount(account.getAccount());

        int oldAccountValue = service.updateAccount(accountEntity);
        return new ResponseEntity<>(accountEntity, HttpStatus.OK);
    }


    @DeleteMapping(value = "/accounts/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable("id") long id) {
        log.info("Fetching & Deleting User with id {}", id);

        Optional<AccountEntity> optionalAccountEntity = service.findAccountById(id);

        if (!optionalAccountEntity.isPresent()) {
            log.error("Unable to delete. Account with id {} not found.", id);
            return new ResponseEntity<>(
                    new CustomErrorResponse(String.format("Account with id: %d not found!", id)),
                    HttpStatus.NOT_FOUND);
        }
        service.deleteAccountById(id);
        return new ResponseEntity<>(optionalAccountEntity.get(), HttpStatus.OK);
    }


    @DeleteMapping(value = "/accounts")
    public ResponseEntity<AccountEntity> deleteAllAccounts() {
        log.info("Deleting All Accounts");
        service.deleteAllAccounts();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    @PatchMapping(value = "/accounts/{id}")
    public ResponseEntity<?> accountOperation(@PathVariable long id, @RequestBody AccountEntity account) {
        log.info("Operation with Account with id {}", id);

        Optional<AccountEntity> optionalAccountEntity = service.findAccountById(id);

        if (!optionalAccountEntity.isPresent()) {
            log.error("Unable to update. Account with id {} not found.", id);
            return new ResponseEntity<>(
                    new CustomErrorResponse(String.format("Unable to update. Account with id: %d not found!", id)),
                    HttpStatus.NOT_FOUND);
        }
        AccountEntity accountEntity = optionalAccountEntity.get();

        if (account.getOperationAmount() < 0) {
            return new ResponseEntity<>(
                    String.format(
                            "Amount operations must be positive! Incoming value: %d",
                            account.getOperationAmount()
                    ), HttpStatus.UNPROCESSABLE_ENTITY);
        }




        String operation = account.getOperation();
        System.out.println("operation = " + operation);
        if (operation.isEmpty()) {
            return new ResponseEntity<>(
                    String.format(
                            "Operation is empty or uncorrect! - %s",
                            account.getOperation()
                    ), HttpStatus.UNPROCESSABLE_ENTITY);
        }else if(operation.equals("sum")){
            log.info("Operation SUM with Account with id {}", id);
            accountEntity.setAccount(accountEntity.getAccount() + account.getOperationAmount());
        }else if(operation.equals("sub")){
            log.info("Operation SUB with Account with id {}", id);
            accountEntity.setAccount(accountEntity.getAccount() - account.getOperationAmount());
        }

//        accountEntity.setAccount(account.getAccount());
        accountEntity.setOperation(account.getOperation());
        accountEntity.setOperationAmount(account.getOperationAmount());
        service.patchAccount(accountEntity);

        service.updateAccount(accountEntity);

//        int oldAccountValue = service.updateAccount(accountEntity);
        return new ResponseEntity<>(accountEntity, HttpStatus.OK);
    }
}
