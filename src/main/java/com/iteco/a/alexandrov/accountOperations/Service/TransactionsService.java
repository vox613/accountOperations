package com.iteco.a.alexandrov.accountOperations.Service;

import com.iteco.a.alexandrov.accountOperations.Entity.TransactionEntity;
import com.iteco.a.alexandrov.accountOperations.Exceptions.CustomResponse.CustomErrorResponse;
import com.iteco.a.alexandrov.accountOperations.Exceptions.MyTransactionException;
import com.iteco.a.alexandrov.accountOperations.Exceptions.MyWalletException;
import com.iteco.a.alexandrov.accountOperations.Model.TransactionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TransactionsService {
    Logger log = LoggerFactory.getLogger(TransactionsService.class);

    ResponseEntity<List<TransactionEntity>> findAllTransactionsFromAllWallets();

    ResponseEntity<List<TransactionEntity>> findAllTransactionsFromWalletId(long id) throws MyWalletException;

    ResponseEntity<TransactionEntity> findTransactionIdFromAllWallets(long id) throws MyTransactionException;

    ResponseEntity<CustomErrorResponse> createTransaction(TransactionModel transactionModel) throws MyTransactionException;

}
