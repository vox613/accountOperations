package com.iteco.a.alexandrov.accountOperations.Service;

import com.iteco.a.alexandrov.accountOperations.Entity.TransactionEntity;
import com.iteco.a.alexandrov.accountOperations.Exceptions.Error.CustomErrorResponse;
import com.iteco.a.alexandrov.accountOperations.Model.TransactionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TransactionsService {
    Logger log = LoggerFactory.getLogger(TransactionsService.class);

    ResponseEntity<List<TransactionEntity>> findAllTransactionsFromAllWallets();

    ResponseEntity<List<TransactionEntity>> findAllTransactionsFromWalletId(long id);

    ResponseEntity<TransactionEntity> findTransactionIdFromAllWallets(long id);

    ResponseEntity<CustomErrorResponse> createTransaction(TransactionModel transactionModel);

}
