package com.iteco.a.alexandrov.accountOperations.Controller;


import com.iteco.a.alexandrov.accountOperations.Entity.TransactionEntity;
import com.iteco.a.alexandrov.accountOperations.Entity.WalletEntity;
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
public class WalletController {

    private static final Logger log = LoggerFactory.getLogger(WalletController.class);
    private final WalletsServiceImpl walletsService;
    private final TransactionsServiceImpl transactionsService;

    @Autowired
    WalletController(WalletsServiceImpl walletsService, TransactionsServiceImpl transactionsService) {
        this.walletsService = walletsService;
        this.transactionsService = transactionsService;
    }


    @GetMapping("/wallets")
    ResponseEntity<List<WalletEntity>> readAllWallets() {
        return walletsService.findAllWallets();
    }

    @GetMapping("/wallets/{id}")
    ResponseEntity<?> readWalletById(@PathVariable long id) {
        return walletsService.readWallet(id);
    }

    @PostMapping("/wallets")
    ResponseEntity<?> createNewWallet(@Valid @RequestBody WalletEntity newWallet) {
        return walletsService.createWallet(newWallet);
    }

    @PutMapping(value = "/wallets/{id}")
    public ResponseEntity<?> updateWallet(@PathVariable long id, @Valid @RequestBody WalletEntity wallet) {
        return walletsService.updateWallet(id, wallet);
    }

    @DeleteMapping(value = "/wallets/{id}")
    public ResponseEntity<?> deleteWallet(@PathVariable("id") long id) {
        return walletsService.deleteWallet(id);
    }

    @DeleteMapping(value = "/wallets")
    public ResponseEntity<WalletEntity> deleteAllWallets() {
        log.info("Deleting All Wallets");
        walletsService.deleteAllWallets();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/wallets/transactions")
    ResponseEntity<List<TransactionEntity>> readAllTransactionsOfAllWallets() {
        return transactionsService.findAllTransactionsFromAllWallets();
    }

    @GetMapping("/wallets/{id}/transactions")
    ResponseEntity<List<TransactionEntity>> readAllTransactionsFromWalletId(@PathVariable long id) {
        return transactionsService.findAllTransactionsFromWalletId(id);
    }

    @GetMapping("/wallets/transactions/{id}")
    ResponseEntity<?> readTransactionIdFromAllWallets(@PathVariable long id) {
        return transactionsService.findTransactionIdFromAllWallets(id);
    }

    @GetMapping("/wallets/{idWallet}/transactions/{idTransaction}")
    ResponseEntity<?> readTransactionIdFromWalletId(@PathVariable long idWallet, @PathVariable long idTransaction) {
        return transactionsService.findTransactionIdFromWalletId(idWallet, idTransaction);
    }

    @PostMapping("/wallets/transactions")
    ResponseEntity<?> readTransactionIdFromWalletId(@Valid @RequestBody TransactionModel transactionModel) {
        System.out.println(">>> " + transactionModel);
        return transactionsService.createTransaction(transactionModel);
    }


// TODO: 16.01.2020 Delete not used URLS


}
