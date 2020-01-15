package com.iteco.a.alexandrov.accountOperations.Service;

import com.iteco.a.alexandrov.accountOperations.Entity.AccountEntity;
import com.iteco.a.alexandrov.accountOperations.Errors.CustomErrorResponse;
import com.iteco.a.alexandrov.accountOperations.Repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Override
    public ResponseEntity<List<AccountEntity>> findAllAccounts() {
        return new ResponseEntity<>(accountRepository.findAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> readAccount(long id) {
        Optional<AccountEntity> accountEntityOptional = accountRepository.findById(id);
        if (!accountEntityOptional.isPresent()) {
            log.error("Account with id {} not found.", id);
            return new ResponseEntity<>(
                    new CustomErrorResponse(String.format("Account with id: %d not found!", id)),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(accountEntityOptional.get(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> createAccount(AccountEntity newAccount) {
        log.warn("newAccount = " + newAccount);

        if (newAccount.getAccount() < 0) {
            log.error("Account sum must bee >= 0 incoming value: " + newAccount.getAccount());
            return new ResponseEntity<>(
                    new CustomErrorResponse("Unable to create. Account cannot be negative!"),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(accountRepository.save(newAccount), HttpStatus.CREATED);
    }


    private ResponseEntity<?> responseCreater(String responseBody, HttpStatus status) {
        return new ResponseEntity<>(new CustomErrorResponse(responseBody), status);
    }


    @Override
    public ResponseEntity<?> updateAccount(long id, AccountEntity newAccount) {
        log.info("Updating Account with id {}", id);

        Optional<AccountEntity> optionalOldAccountEntity = accountRepository.findById(id);

        if (!optionalOldAccountEntity.isPresent()) {
            log.error("Unable to update. Account with id {} not found.", id);
            return responseCreater(
                    String.format("Unable to update. Account with id: %d not found!", id),
                    HttpStatus.NOT_FOUND
            );
        }

        AccountEntity oldAccountEntity = optionalOldAccountEntity.get();

        if (newAccount.getAccount() < 0) {
            log.error("Account sum must bee >= 0 incoming value: " + newAccount.getAccount());
            return responseCreater(
                    String.format(
                            "Unable to update. Account sum must be positive! Incoming value: %d",
                            newAccount.getAccount()),
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }

        oldAccountEntity.setAccount(newAccount.getAccount());
        oldAccountEntity.setAccountName(newAccount.getAccountName());

        accountRepository.updateAccount(oldAccountEntity.getId(), oldAccountEntity.getAccount(), oldAccountEntity.getAccountName());

        return new ResponseEntity<>(oldAccountEntity, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> deleteAccount(long id) {
        log.info("Fetching & Deleting User with id {}", id);

        Optional<AccountEntity> optionalAccountEntity = accountRepository.findById(id);

        if (!optionalAccountEntity.isPresent()) {
            log.error("Unable to delete. Account with id {} not found.", id);
            return responseCreater(String.format("Account with id: %d not found!", id),
                    HttpStatus.NOT_FOUND);
        }
        accountRepository.deleteById(id);
        return new ResponseEntity<>(optionalAccountEntity.get(), HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> deleteAllAccounts() {
        log.info("Deleting All Accounts");
        accountRepository.deleteAll();
        return responseCreater("", HttpStatus.NO_CONTENT);
    }


}
