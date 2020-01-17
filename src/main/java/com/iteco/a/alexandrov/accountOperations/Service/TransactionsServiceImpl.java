package com.iteco.a.alexandrov.accountOperations.Service;

import com.iteco.a.alexandrov.accountOperations.Entity.TransactionEntity;
import com.iteco.a.alexandrov.accountOperations.Entity.WalletEntity;
import com.iteco.a.alexandrov.accountOperations.Enum.AvailableTransactions;
import com.iteco.a.alexandrov.accountOperations.Exceptions.MyTransactionException;
import com.iteco.a.alexandrov.accountOperations.Model.TransactionModel;
import com.iteco.a.alexandrov.accountOperations.Repository.TransactionsRepository;
import com.iteco.a.alexandrov.accountOperations.Repository.WalletsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionsServiceImpl implements TransactionsService {
    private TransactionsRepository transactionsRepository;
    private WalletsRepository walletsRepository;
    private WalletsServiceImpl walletsService;

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
    public ResponseEntity<?> findTransactionIdFromAllWallets(long id) throws Throwable {
        Optional<TransactionEntity> operationEntityOptional = transactionsRepository.findById(id);
        if (!operationEntityOptional.isPresent()) {
            log.error("Operation with id {} not found.", id);
            throw new MyTransactionException(String.format("Operation with id: %d not found!", id), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(operationEntityOptional.get(), HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> findTransactionIdFromWalletId(long idWallet, long idTransaction) throws Throwable {
        Optional<TransactionEntity> transactionEntityOptional = transactionsRepository.findTransactionEntitiesByIdAndWalletId(idTransaction, idWallet);

        if (!transactionEntityOptional.isPresent()) {
            log.error("Operation with id {} for account {} not found.", idTransaction, idWallet);
            throw new MyTransactionException(String.format("Operation with id: %d for account %d not found!", idTransaction, idWallet), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(transactionEntityOptional.get(), HttpStatus.OK);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Throwable.class)
    public ResponseEntity<?> createTransaction(TransactionModel transactionModel) throws Throwable {
        WalletEntity walletEntity = checkWalletExist(transactionModel.getWalletId());

        String operation = transactionModel.getTransactionType().toLowerCase();
        checkCorrectTransactionOperation(operation);

        if (operation.equals(AvailableTransactions.SUB.getValue())) {
            subTransactionExecution(walletEntity, transactionModel);
        } else if (operation.equals(AvailableTransactions.SUM.getValue())) {
            sumTransactionExecution(walletEntity, transactionModel);
        }

        createJournalLog(walletEntity, transactionModel);
        return walletsService.responseCreater("Success transaction!", HttpStatus.CREATED);
    }

    private void throwEx() throws Throwable {
        throw new MyTransactionException("throwEx", HttpStatus.BAD_REQUEST);
    }


    private WalletEntity checkWalletExist(Long walletId) throws Throwable {
        Optional<WalletEntity> walletEntityOptional = walletsRepository.findById(walletId);
        return walletEntityOptional.orElseThrow(() -> new MyTransactionException("Wallet by ID not found!", HttpStatus.NOT_FOUND));
    }

    private void checkCorrectTransactionOperation(String transactionType) throws Throwable {
        if (Arrays.stream(AvailableTransactions.values()).noneMatch(x -> x.getValue().equals(transactionType))) {
            throw new MyTransactionException("Uncorrected operation!", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private boolean checkWalletEnoughFunds(BigDecimal operationFunds, BigDecimal walletFunds) {
        return walletFunds.compareTo(operationFunds) > 0;
    }

    private void sumTransactionExecution(WalletEntity walletEntity, TransactionModel transactionModel) {
        walletEntity.setAccount(walletEntity.getAccount().add(transactionModel.getTransactionAmount()));
        walletsRepository.updateWallet(walletEntity.getId(), walletEntity.getAccount(), walletEntity.getWalletName());
    }

    private void subTransactionExecution(WalletEntity walletEntity, TransactionModel transactionModel) throws Throwable {
        if (checkWalletEnoughFunds(transactionModel.getTransactionAmount(), walletEntity.getAccount())) {
            walletEntity.setAccount(walletEntity.getAccount().subtract(transactionModel.getTransactionAmount()));
            walletsRepository.updateWallet(walletEntity.getId(), walletEntity.getAccount(), walletEntity.getWalletName());
        } else {
            throw new MyTransactionException("Not enough money for operation!", HttpStatus.UNPROCESSABLE_ENTITY);
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
}
