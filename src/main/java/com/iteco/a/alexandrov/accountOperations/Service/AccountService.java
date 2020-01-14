package com.iteco.a.alexandrov.accountOperations.Service;

import com.iteco.a.alexandrov.accountOperations.Controller.AccountController;
import com.iteco.a.alexandrov.accountOperations.Entity.AccountEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    Logger log = LoggerFactory.getLogger(AccountService.class);

    List<AccountEntity> findAllAccounts();
    Optional<AccountEntity> findAccountById(long id);
    AccountEntity saveAccountToDB(AccountEntity newAccount);
    void deleteAccountById(long id);
    void deleteAllAccounts();
    AccountEntity executeCommand(long id, AccountEntity account);

    ResponseEntity<?> checkAccountExist(long id);
    ResponseEntity<?> checkAndCreateNewAccount(AccountEntity newAccount);

}
