package com.iteco.a.alexandrov.accountOperations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The application allows you to create and edit wallets with an initial amount in the account, carry out transactional
 * operations for withdrawing / crediting funds to accounts
 */
@SpringBootApplication
public class AccountOperationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountOperationsApplication.class, args);
    }

}
