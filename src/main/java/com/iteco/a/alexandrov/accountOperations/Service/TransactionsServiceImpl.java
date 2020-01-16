package com.iteco.a.alexandrov.accountOperations.Service;

import com.iteco.a.alexandrov.accountOperations.Entity.TransactionEntity;
import com.iteco.a.alexandrov.accountOperations.Entity.WalletEntity;
import com.iteco.a.alexandrov.accountOperations.Enum.AvailableTransactions;
import com.iteco.a.alexandrov.accountOperations.Error.CustomErrorResponse;
import com.iteco.a.alexandrov.accountOperations.Model.TransactionModel;
import com.iteco.a.alexandrov.accountOperations.Repository.TransactionsRepository;
import com.iteco.a.alexandrov.accountOperations.Repository.WalletsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionsServiceImpl implements TransactionsService {
    TransactionsRepository transactionsRepository;
    WalletsRepository walletsRepository;
    WalletsServiceImpl walletsService;

    @Autowired
    public TransactionsServiceImpl(TransactionsRepository transactionsRepository, WalletsRepository walletsRepository, WalletsServiceImpl walletsService) {
        this.transactionsRepository = transactionsRepository;
        this.walletsRepository = walletsRepository;
        this.walletsService = walletsService;
    }


    @Override
    public ResponseEntity<List<TransactionEntity>> findAllTransactionsFromAllWallets() {
        return new ResponseEntity<>(transactionsRepository.findAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<TransactionEntity>> findAllTransactionsFromWalletId(long id) {
        return new ResponseEntity<>(transactionsRepository.findAllByWalletId(id), HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> findTransactionIdFromAllWallets(long id) {
        Optional<TransactionEntity> operationEntityOptional = transactionsRepository.findById(id);
        if (!operationEntityOptional.isPresent()) {
            log.error("Operation with id {} not found.", id);
            return new ResponseEntity<>(
                    new CustomErrorResponse(String.format("Operation with id: %d not found!", id)),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(operationEntityOptional.get(), HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> findTransactionIdFromWalletId(long idWallet, long idTransaction) {
        Optional<TransactionEntity> transactionEntityOptional = transactionsRepository.findTransactionEntitiesByIdAndWalletId(idTransaction, idWallet);

        if (!transactionEntityOptional.isPresent()) {
            log.error("Operation with id {} for account {} not found.", idTransaction, idWallet);
            return new ResponseEntity<>(
                    new CustomErrorResponse(String.format("Operation with id: %d for account %d not found!", idTransaction, idWallet)),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(transactionEntityOptional.get(), HttpStatus.OK);
    }


    private WalletEntity checkWalletExist(Long walletId) throws Exception {
        Optional<WalletEntity> walletEntityOptional = walletsRepository.findById(walletId);
        return walletEntityOptional.orElseThrow(() -> new Exception("Wallet by ID not found!"));
    }

    private void checkCorrectTransactionOperation(String transactionType) throws Exception {
        if (Arrays.stream(AvailableTransactions.values()).anyMatch(x -> x.toString().equals(transactionType))) {
            throw new Exception("Uncorrected operation!");
        }
    }

    // TODO: 16.01.2020 Group methods to same functionality
    private boolean checkWalletEnoughFunds(BigDecimal operationFunds, BigDecimal walletFunds) {
        return walletFunds.compareTo(operationFunds) > 0;
    }

    private void sumTransactionExecution(WalletEntity walletEntity, TransactionModel transactionModel) {
        walletEntity.setAccount(walletEntity.getAccount().add(transactionModel.getTransactionAmount()));
        walletsRepository.updateWallet(walletEntity.getId(), walletEntity.getAccount(), walletEntity.getWalletName());
    }

    private void subTransactionExecution(WalletEntity walletEntity, TransactionModel transactionModel) throws Exception {
        if (checkWalletEnoughFunds(transactionModel.getTransactionAmount(), walletEntity.getAccount())) {
            walletEntity.setAccount(walletEntity.getAccount().subtract(transactionModel.getTransactionAmount()));
            walletsRepository.updateWallet(walletEntity.getId(), walletEntity.getAccount(), walletEntity.getWalletName());
        } else {
            throw new Exception("Not enough money for operation!");
            // TODO: 16.01.2020 Personal exceptions for each variant
        }
    }

    private void createJournalLog(WalletEntity walletEntity, TransactionModel transactionModel) {
        transactionsRepository.save(new TransactionEntity(
                walletEntity.getId(),
                walletEntity.getWalletName(),
                transactionModel.getTransactionType(),
                transactionModel.getTransactionAmount(),
                walletEntity.getAccount()
        ));
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    // TODO: 16.01.2020 Understand how to work with transactionals
    public ResponseEntity<?> createTransaction(TransactionModel transactionModel) {
        String responseBody = "";
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        try {
            // check wallet with idWallet exist
            WalletEntity walletEntity = checkWalletExist(transactionModel.getWalletId());

            // check correct operation
            String operation = transactionModel.getTransactionType().toLowerCase(); // TODO: 16.01.2020 Make field as ENUM element instead String
            checkCorrectTransactionOperation(operation);

            // determine operation
            if (operation.equals(AvailableTransactions.SUB.getValue())) {
                subTransactionExecution(walletEntity, transactionModel);
            } else if (operation.equals(AvailableTransactions.SUM.getValue())) {
                sumTransactionExecution(walletEntity, transactionModel);
            }
            createJournalLog(walletEntity, transactionModel);
            responseBody = "Success transaction!";
            responseStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            responseBody = ex.getMessage();
            responseStatus = HttpStatus.NOT_FOUND;
        } finally {
            return walletsService.responseCreater(responseBody, responseStatus);
            // TODO: 16.01.2020 Why return in finally is badPractice?
        }
    }
}
