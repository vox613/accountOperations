package com.iteco.a.alexandrov.accountOperations.Service;

import com.iteco.a.alexandrov.accountOperations.Entity.WalletEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WalletsService {
    Logger log = LoggerFactory.getLogger(WalletsService.class);

    ResponseEntity<List<WalletEntity>> findAllWallets();

    ResponseEntity<?> readWallet(long id);

    ResponseEntity<?> createWallet(WalletEntity newWallet);

    @Transactional
    ResponseEntity<?> updateWallet(long id, WalletEntity newAccount);

    ResponseEntity<?> deleteWallet(long id);

    ResponseEntity<?> deleteAllWallets();

    ResponseEntity<?> responseCreater(String responseBody, HttpStatus status);

}
