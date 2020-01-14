package com.iteco.a.alexandrov.accountOperations.Service;

import com.iteco.a.alexandrov.accountOperations.Entity.AccountEntity;
import com.iteco.a.alexandrov.accountOperations.Errors.CustomErrorResponse;
import com.iteco.a.alexandrov.accountOperations.Repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<AccountEntity> findAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Optional<AccountEntity> findAccountById(long id) {
        return accountRepository.findById(id);
    }

    @Override
    public AccountEntity saveAccountToDB(AccountEntity newAccount) {
        return accountRepository.save(newAccount);
    }

    @Transactional
    public int updateAccount(AccountEntity newAccount) {
        return accountRepository.updateAccountValue(newAccount.getId(), newAccount.getAccount());
    }

    @Transactional
    public int patchAccount(AccountEntity newAccount) {
        return accountRepository.patchAccount(newAccount.getId(), newAccount.getOperation(), newAccount.getOperationAmount());
    }

    @Override
    public void deleteAccountById(long id) {
        accountRepository.deleteById(id);
    }

    @Override
    public void deleteAllAccounts() {
        accountRepository.deleteAll();
    }


    @Override
    public AccountEntity executeCommand(long id, AccountEntity account) {

        return null;
    }

    @Override
    public ResponseEntity<?> checkAccountExist(long id) {
        Optional<AccountEntity> accountEntityOptional = findAccountById(id);
        if (!accountEntityOptional.isPresent()) {
            log.error("Account with id {} not found.", id);
            return new ResponseEntity<>(
                    new CustomErrorResponse(String.format("Account with id: %d not found!", id)),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(accountEntityOptional.get(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> checkAndCreateNewAccount(AccountEntity newAccount) {
        log.warn("newAccount = " + newAccount);
        if (newAccount.getAccount() < 0) {
            log.error("Account sum must bee >= 0 incoming value: " + newAccount.getAccount());
            return new ResponseEntity<>(
                    new CustomErrorResponse("Unable to create. Account cannot be negative!"),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(saveAccountToDB(newAccount), HttpStatus.CREATED);
    }


}
