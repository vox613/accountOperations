package com.iteco.a.alexandrov.accountOperations.Service;

import com.iteco.a.alexandrov.accountOperations.Entity.WalletEntity;
import com.iteco.a.alexandrov.accountOperations.Exceptions.Error.CustomErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface WalletsService {
    Logger log = LoggerFactory.getLogger(WalletsService.class);

    ResponseEntity<List<WalletEntity>> findAllWallets();

    ResponseEntity<WalletEntity> readWallet(long id);

    ResponseEntity<WalletEntity> createWallet(WalletEntity newWallet);

    ResponseEntity<WalletEntity> updateWallet(long id, WalletEntity newAccount);

    ResponseEntity<WalletEntity> deleteWallet(long id);

    ResponseEntity<CustomErrorResponse> deleteAllWallets();

}
