package com.iteco.a.alexandrov.accountOperations.Service;

import com.iteco.a.alexandrov.accountOperations.Entity.TransactionEntity;
import com.iteco.a.alexandrov.accountOperations.Model.TransactionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TransactionsService {
    Logger log = LoggerFactory.getLogger(TransactionsService.class);

    ResponseEntity<List<TransactionEntity>> findAllTransactionsFromAllWallets();
    ResponseEntity<List<TransactionEntity>> findAllTransactionsFromWalletId(long id);
    ResponseEntity<?> findTransactionIdFromAllWallets(long id);
    ResponseEntity<?> findTransactionIdFromWalletId(long idWallet, long idTransaction);
    ResponseEntity<?> createTransaction(TransactionModel transactionModel);

    // TODO: 16.01.2020 put unity methods here
}
