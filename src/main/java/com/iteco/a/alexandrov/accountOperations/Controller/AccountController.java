package com.iteco.a.alexandrov.accountOperations.Controller;


import com.iteco.a.alexandrov.accountOperations.Entity.AccountEntity;
import com.iteco.a.alexandrov.accountOperations.Entity.OperationEntity;
import com.iteco.a.alexandrov.accountOperations.Service.AccountServiceImpl;
import com.iteco.a.alexandrov.accountOperations.Service.OperationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    private final AccountServiceImpl accountService;
    private final OperationServiceImpl operationService;

    @Autowired
    AccountController(AccountServiceImpl accountService, OperationServiceImpl operationService) {
        this.accountService = accountService;
        this.operationService = operationService;
    }


    @GetMapping("/accounts")
    ResponseEntity<List<AccountEntity>> readAllAccounts() {
        return accountService.findAllAccounts();
    }

    @GetMapping("/accounts/{id}")
    ResponseEntity<?> readAccountById(@PathVariable long id) {
        return accountService.readAccount(id);
    }

    @PostMapping("/accounts")
    ResponseEntity<?> createNewAccount(@RequestBody AccountEntity newAccount) {
        return accountService.createAccount(newAccount);
    }


    @PutMapping(value = "/accounts/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable long id, @RequestBody AccountEntity account) {
        return accountService.updateAccount(id, account);
    }

    @DeleteMapping(value = "/accounts/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable("id") long id) {
        return accountService.deleteAccount(id);
    }

    @DeleteMapping(value = "/accounts")
    public ResponseEntity<AccountEntity> deleteAllAccounts() {
        log.info("Deleting All Accounts");
        accountService.deleteAllAccounts();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }







    @GetMapping("/accounts/operations")
    ResponseEntity<List<OperationEntity>> readAllOperationsOfAllAccounts() {
        return operationService.findAllOperationsFromAllAccounts();
    }

    @GetMapping("/accounts/{id}/operations")
    ResponseEntity<List<OperationEntity>> readAllOperationsOfAccountId(@PathVariable long id) {
        return operationService.findAllOperationsFromAccountId(id);
    }

    @GetMapping("/accounts/operations/{id}")
    ResponseEntity<?> readOperationId(@PathVariable long id) {
        return operationService.findOperationIdFromAllAccounts(id);
    }

    @GetMapping("/accounts/{idAccount}/operations/{idOperation}")
    ResponseEntity<?> readOperationIdOfAccountId(@PathVariable long idAccount, @PathVariable long idOperation) {
        return operationService.findOperationIdFromAccountId(idAccount, idOperation);
    }


    @PostMapping("/accounts/operations")
    ResponseEntity<?> createNewOperation(@RequestBody OperationEntity operationEntity) {
        return operationService.createOperation(operationEntity);
    }



}
