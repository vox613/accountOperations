package com.iteco.a.alexandrov.accountOperations.Service;

import com.iteco.a.alexandrov.accountOperations.Entity.AccountEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccountService {
    Logger log = LoggerFactory.getLogger(AccountService.class);

    ResponseEntity<List<AccountEntity>> findAllAccounts();

    ResponseEntity<?> readAccount(long id);

    ResponseEntity<?> createAccount(AccountEntity newAccount);

    @Transactional
    ResponseEntity<?> updateAccount(long id, AccountEntity newAccount);

    ResponseEntity<?> deleteAccount(long id);

    ResponseEntity<?> deleteAllAccounts();

}
