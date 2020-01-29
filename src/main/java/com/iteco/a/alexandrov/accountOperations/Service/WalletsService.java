package com.iteco.a.alexandrov.accountOperations.Service;

import com.iteco.a.alexandrov.accountOperations.Entity.WalletEntity;
import com.iteco.a.alexandrov.accountOperations.Exceptions.CustomResponse.CustomErrorResponse;
import com.iteco.a.alexandrov.accountOperations.Exceptions.MyTransactionException;
import com.iteco.a.alexandrov.accountOperations.Exceptions.MyWalletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface WalletsService {
    Logger log = LoggerFactory.getLogger(WalletsService.class);

    ResponseEntity<List<WalletEntity>> findAllWallets();

    ResponseEntity<WalletEntity> readWallet(long id) throws MyWalletException;

    ResponseEntity<WalletEntity> createWallet(WalletEntity newWallet) throws MyWalletException;

    ResponseEntity<WalletEntity> updateWallet(long id, WalletEntity newAccount) throws MyWalletException;

    ResponseEntity<WalletEntity> deleteWallet(long id) throws MyWalletException, MyTransactionException;

    ResponseEntity<CustomErrorResponse> deleteAllWallets();

}
