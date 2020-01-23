package com.iteco.a.alexandrov.accountOperations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class AccountOperationsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountOperationsApplication.class, args);
	}

}
