package com.iteco.a.alexandrov.accountOperations.Service;

import com.iteco.a.alexandrov.accountOperations.Entity.WalletEntity;
import com.iteco.a.alexandrov.accountOperations.Error.CustomErrorResponse;
import com.iteco.a.alexandrov.accountOperations.Repository.WalletsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WalletsServiceImpl implements WalletsService {

    WalletsRepository walletsRepository;

    @Autowired
    public WalletsServiceImpl(WalletsRepository walletsRepository) {
        this.walletsRepository = walletsRepository;
    }


    @Override
    public ResponseEntity<List<WalletEntity>> findAllWallets() {
        return new ResponseEntity<>(walletsRepository.findAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> readWallet(long id) {
        Optional<WalletEntity> walletEntityOptional = walletsRepository.findById(id);
        if (walletEntityOptional.isPresent()) {
            return new ResponseEntity<>(walletEntityOptional.get(), HttpStatus.OK);
        } else {
            log.error("Wallet with id {} not found.", id);
            return new ResponseEntity<>(
                    new CustomErrorResponse(String.format("Wallet with id: %d not found!", id)),
                    HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> createWallet(WalletEntity newWallet) {
        log.warn("Wallet = " + newWallet);
        return new ResponseEntity<>(walletsRepository.save(newWallet), HttpStatus.CREATED);
    }


    public ResponseEntity<?> responseCreater(String responseBody, HttpStatus status) {
        return new ResponseEntity<>(new CustomErrorResponse(responseBody), status);
    }


    @Override
    public ResponseEntity<?> updateWallet(long id, WalletEntity newWallet) {
        log.info("Updating Wallet with id {}", id);

        Optional<WalletEntity> walletEntityOptional = walletsRepository.findById(id);

        if (walletEntityOptional.isPresent()) {
            WalletEntity oldWalletEntity = walletEntityOptional.get();

            oldWalletEntity.setAccount(newWallet.getAccount());
            oldWalletEntity.setWalletName(newWallet.getWalletName());

            walletsRepository.updateWallet(oldWalletEntity.getId(), oldWalletEntity.getAccount(), oldWalletEntity.getWalletName());

            return new ResponseEntity<>(oldWalletEntity, HttpStatus.OK);
        } else {
            log.error("Unable to update. Wallet with id {} not found.", id);
            return responseCreater(
                    String.format("Unable to update. Wallet with id: %d not found!", id),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @Override
    public ResponseEntity<?> deleteWallet(long id) {
        log.info("Deleting User with id {}", id);

        Optional<WalletEntity> optionalWalletEntity = walletsRepository.findById(id);

        if (!optionalWalletEntity.isPresent()) {
            log.error("Unable to delete. Wallet with id {} not found.", id);
            return responseCreater(String.format("Wallet with id: %d not found!", id),
                    HttpStatus.NOT_FOUND);
        }
        walletsRepository.deleteById(id);
        return new ResponseEntity<>(optionalWalletEntity.get(), HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> deleteAllWallets() {
        log.info("Deleting All Wallet");
        walletsRepository.deleteAll();
        return responseCreater("", HttpStatus.NO_CONTENT);
    }


}
