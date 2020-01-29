package com.iteco.a.alexandrov.accountOperations.Controller;


import com.iteco.a.alexandrov.accountOperations.Entity.TransactionEntity;
import com.iteco.a.alexandrov.accountOperations.Entity.WalletEntity;
import com.iteco.a.alexandrov.accountOperations.Exceptions.CustomResponse.CustomErrorResponse;
import com.iteco.a.alexandrov.accountOperations.Exceptions.MyTransactionException;
import com.iteco.a.alexandrov.accountOperations.Exceptions.MyWalletException;
import com.iteco.a.alexandrov.accountOperations.Model.TransactionModel;
import com.iteco.a.alexandrov.accountOperations.Service.TransactionsServiceImpl;
import com.iteco.a.alexandrov.accountOperations.Service.WalletsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    private final WalletsServiceImpl walletsService;
    private final TransactionsServiceImpl transactionsService;

    @Autowired
    Controller(WalletsServiceImpl walletsService, TransactionsServiceImpl transactionsService) {
        this.walletsService = walletsService;
        this.transactionsService = transactionsService;
    }


    @GetMapping("/wallets")
    ResponseEntity<List<WalletEntity>> readAllWallets() {
        return walletsService.findAllWallets();
    }

    @GetMapping("/wallets/{id}")
    ResponseEntity<WalletEntity> readWalletById(@PathVariable long id) throws MyWalletException {
        return walletsService.readWallet(id);
    }

    @PostMapping("/wallets")
    ResponseEntity<WalletEntity> createNewWallet(@Valid @RequestBody WalletEntity newWallet) throws MyWalletException {
        log.info("Create Wallet");
        return walletsService.createWallet(newWallet);
    }

    @PutMapping(value = "/wallets/{id}")
    public ResponseEntity<WalletEntity> updateWallet(@PathVariable long id, @Valid @RequestBody WalletEntity wallet) throws MyWalletException {
        log.info("Update Wallet with id = {}", id);
        return walletsService.updateWallet(id, wallet);
    }

    @DeleteMapping(value = "/wallets/{id}")
    public ResponseEntity<WalletEntity> deleteWallet(@PathVariable("id") long id) throws MyWalletException, MyTransactionException {
        log.info("Delete Wallet with id = {}", id);
        return walletsService.deleteWallet(id);
    }

    @DeleteMapping(value = "/wallets")
    public ResponseEntity<WalletEntity> deleteAllWallets() {
        log.info("Deleting All Wallets");
        walletsService.deleteAllWallets();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ResponseBody
    @GetMapping("/wallets/transactions")
    public ResponseEntity<List<TransactionEntity>> readAllTransactionsOfAllWallets() {
        return transactionsService.findAllTransactionsFromAllWallets();
    }

    @GetMapping("/wallets/{id}/transactions")
    public ResponseEntity<List<TransactionEntity>> readAllTransactionsFromWalletId(@PathVariable long id) throws MyWalletException {
        return transactionsService.findAllTransactionsFromWalletId(id);
    }

    @GetMapping("/wallets/transactions/{id}")
    public ResponseEntity<TransactionEntity> readTransactionIdFromAllWallets(@PathVariable long id) throws MyTransactionException {
        return transactionsService.findTransactionIdFromAllWallets(id);
    }


    @PostMapping("/wallets/transactions")
    public ResponseEntity<CustomErrorResponse> readTransactionIdFromWalletId(@Valid @RequestBody TransactionModel transactionModel) throws MyTransactionException {
        log.info("Create new transaction for Wallet with id = {}", transactionModel.getWalletId());
        return transactionsService.createTransaction(transactionModel);
    }

}
