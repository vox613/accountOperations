package com.iteco.a.alexandrov.accountOperations.Service;

import com.iteco.a.alexandrov.accountOperations.Entity.AccountEntity;
import com.iteco.a.alexandrov.accountOperations.Repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService{

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

//    public AccountEntity updateAccount(AccountEntity newAccount) {
//        return accountRepository.updateAccount(newAccount.getId(), newAccount.getAccount());
//    }

    @Override
    public void deleteAccountById(long id) {
        accountRepository.deleteById(id);
    }

    @Override
    public void deleteAllAccounts() {
        accountRepository.deleteAll();
    }


}
