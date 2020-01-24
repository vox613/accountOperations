package com.iteco.a.alexandrov.accountOperations.Service;

import com.iteco.a.alexandrov.accountOperations.Entity.TransactionEntity;
import com.iteco.a.alexandrov.accountOperations.Entity.WalletEntity;
import com.iteco.a.alexandrov.accountOperations.Enum.AvailableTransactions;
import com.iteco.a.alexandrov.accountOperations.Exceptions.Error.CustomErrorResponse;
import com.iteco.a.alexandrov.accountOperations.Exceptions.MyTransactionException;
import com.iteco.a.alexandrov.accountOperations.Exceptions.MyWalletException;
import com.iteco.a.alexandrov.accountOperations.Model.TransactionModel;
import com.iteco.a.alexandrov.accountOperations.Repository.TransactionsRepository;
import com.iteco.a.alexandrov.accountOperations.Repository.WalletsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
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
@EnableTransactionManagement
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
    public ResponseEntity<List<TransactionEntity>> findAllTransactionsFromWalletId(long id) throws MyWalletException {
        if (walletsService.readWallet(id).getStatusCode().equals(HttpStatus.OK)) {
            return new ResponseEntity<>(transactionsRepository.findAllByWalletId(id), HttpStatus.OK);
        } else {
            throw new MyWalletException("Wallet by ID not found!", HttpStatus.NOT_FOUND);
        }
    }


    @Override
    public ResponseEntity<TransactionEntity> findTransactionIdFromAllWallets(long id) throws MyTransactionException {
        Optional<TransactionEntity> operationEntityOptional = transactionsRepository.findById(id);
        if (operationEntityOptional.isPresent()) {
            return new ResponseEntity<>(operationEntityOptional.get(), HttpStatus.OK);
        } else {
            log.error("Operation with id {} not found.", id);
            throw new MyTransactionException(String.format("Operation with id: %d not found!", id), HttpStatus.NOT_FOUND);
        }
    }

    @Autowired
    EntityManager entityManager;


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = MyTransactionException.class)
    public ResponseEntity<CustomErrorResponse> createTransaction(TransactionModel transactionModel) throws MyTransactionException {

//        WalletEntity walletEntity = entityManager.find(WalletEntity.class, transactionModel.getWalletId(), LockModeType.PESSIMISTIC_WRITE);
        WalletEntity walletEntity = checkWalletExist(transactionModel.getWalletId());
//        entityManager.lock(walletEntity, LockModeType.PESSIMISTIC_WRITE);


        String operation = transactionModel.getTransactionType().toLowerCase();
        checkCorrectTransactionOperation(operation);

        if (operation.equals(AvailableTransactions.SUB.getValue())) {
            subTransactionExecution(walletEntity, transactionModel);
        } else if (operation.equals(AvailableTransactions.SUM.getValue())) {
            sumTransactionExecution(walletEntity, transactionModel);
        }

        createJournalLog(walletEntity, transactionModel);

        entityManager.lock(walletEntity, LockModeType.NONE);
        return walletsService.responseCreater("Success transaction!", HttpStatus.CREATED);
    }


    private WalletEntity checkWalletExist(Long walletId) throws MyTransactionException {
        return Optional.ofNullable(entityManager.find(WalletEntity.class, walletId, LockModeType.PESSIMISTIC_WRITE))
                .orElseThrow(() ->
                        new MyTransactionException("Wallet by ID not found!", HttpStatus.NOT_FOUND)
                );
    }

    private void checkCorrectTransactionOperation(String transactionType) throws MyTransactionException {
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

    private void subTransactionExecution(WalletEntity walletEntity, TransactionModel transactionModel) throws MyTransactionException {
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
