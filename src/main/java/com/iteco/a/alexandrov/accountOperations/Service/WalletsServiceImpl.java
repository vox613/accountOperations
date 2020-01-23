package com.iteco.a.alexandrov.accountOperations.Service;

import com.iteco.a.alexandrov.accountOperations.Entity.WalletEntity;
import com.iteco.a.alexandrov.accountOperations.Exceptions.Error.CustomErrorResponse;
import com.iteco.a.alexandrov.accountOperations.Exceptions.MyWalletException;
import com.iteco.a.alexandrov.accountOperations.Repository.WalletsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@EnableTransactionManagement
public class WalletsServiceImpl implements WalletsService {

    private WalletsRepository walletsRepository;

    @Autowired
    public WalletsServiceImpl(WalletsRepository walletsRepository) {
        this.walletsRepository = walletsRepository;
    }


    @Override
    public ResponseEntity<List<WalletEntity>> findAllWallets() {
        return new ResponseEntity<>(walletsRepository.findAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<WalletEntity> readWallet(long id) throws MyWalletException {
        Optional<WalletEntity> walletEntityOptional = walletsRepository.findById(id);
        if (walletEntityOptional.isPresent()) {
            return new ResponseEntity<>(walletEntityOptional.get(), HttpStatus.OK);
        } else {
            log.error("Wallet with id {} not found.", id);
            throw new MyWalletException(String.format("Wallet with id: %d not found!", id), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = MyWalletException.class)
    public ResponseEntity<WalletEntity> createWallet(WalletEntity newWallet) throws MyWalletException {
        log.warn("Wallet = " + newWallet);
        if (checkWalletWithSameNameExist(newWallet.getWalletName())) {
            log.warn("Created wallet: = " + newWallet);
            throw new MyWalletException("Wallet with same name exist!", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        WalletEntity save = walletsRepository.save(newWallet);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    private boolean checkWalletWithSameNameExist(String walletName) {
        return walletsRepository.existsByWalletName(walletName);
    }


    public ResponseEntity<CustomErrorResponse> responseCreater(String responseBody, HttpStatus status) {
        return new ResponseEntity<>(new CustomErrorResponse(responseBody), status);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = MyWalletException.class)
    public ResponseEntity<WalletEntity> updateWallet(long id, WalletEntity newWallet) throws MyWalletException {
        log.info("Updating Wallet with id {}", id);

        Optional<WalletEntity> walletEntityOptional = walletsRepository.findById(id);

        if (walletEntityOptional.isPresent()) {
            WalletEntity oldWalletEntity = walletEntityOptional.get();
            oldWalletEntity.setAccount(newWallet.getAccount());
            oldWalletEntity.setWalletName(newWallet.getWalletName());


            walletsRepository.updateWallet(oldWalletEntity.getId(), oldWalletEntity.getAccount(),
                    oldWalletEntity.getWalletName());

            return new ResponseEntity<>(oldWalletEntity, HttpStatus.OK);
        } else {
            log.error("Unable to update. Wallet with id {} not found.", id);
            throw new MyWalletException(String.format("Unable to update. Wallet with id: %d not found!", id),
                    HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<WalletEntity> deleteWallet(long id) throws MyWalletException {
        log.info("Deleting User with id {}", id);

        Optional<WalletEntity> optionalWalletEntity = walletsRepository.findById(id);

        if (optionalWalletEntity.isPresent()) {
            walletsRepository.deleteById(id);
            return new ResponseEntity<>(optionalWalletEntity.get(), HttpStatus.OK);
        } else {
            log.error("Unable to delete. Wallet with id {} not found.", id);
            throw new MyWalletException(String.format("Wallet with id: %d not found!", id), HttpStatus.NOT_FOUND);
        }
    }


    @Override
    public ResponseEntity<CustomErrorResponse> deleteAllWallets() {
        log.info("Deleting All Wallet");
        walletsRepository.deleteAll();
        return responseCreater("", HttpStatus.NO_CONTENT);
    }


}
