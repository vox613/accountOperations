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

/**
 * Rest controller for managing wallet transactions and wallet management
 */
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

    /**
     * The method expects a GET request at localhost:8080/rest/wallets.
     *
     * @return A list of all created wallets or an empty list, HTTP status 200 OK
     */
    @GetMapping("/wallets")
    public ResponseEntity<List<WalletEntity>> readAllWallets() {
        return walletsService.findAllWallets();
    }

    /**
     * The method expects a GET request at localhost:8080/rest/wallets/{id} and returns a wallet object, if one exists.
     *
     * @param id - unique id of an existing wallet.
     * @return - wallet JSON object with given id, HTTP status 200 OK
     * Example of response JSON body: [{"id":2,"account":10005.05,"walletName":"acc2","createDateTime":"2020-01-30T16:48:00.809857"},{"id":3,"account":0.00,"walletName":"acc3","createDateTime":"2020-01-30T16:48:00.809857"}]
     * @throws MyWalletException - throws if the wallet with the given id does not exist, the message:
     *                           "Wallet with id: {id} not found!" is returned with HTTP status 404 NOT_FOUND
     */
    @GetMapping("/wallets/{id}")
    public ResponseEntity<WalletEntity> readWalletById(@PathVariable long id) throws MyWalletException {
        return walletsService.readWallet(id);
    }


    /**
     * The method expects a POST request at localhost:8080/rest/wallets and create a new wallet object, if the data
     * in the request body is correct and there is no wallet with the same name
     * <p>
     * Example of request JSON body: {"account":100,"walletName":"exampleWallet"}
     *
     * @param newWallet - JSON object representing wallet data
     * @return Created wallet object with HTTP status 201 CREATED or exception when problems occur
     * <p>
     * Example of response JSON body: {"id":4,"account":100,"walletName":"exampleWallet","createDateTime":"2020-01-30T17:38:40.757"}
     * @throws MyWalletException - It is thrown if the entered data is incorrect (a negative amount, a wallet with
     *                           the same name exists, incorrect JSON, etc.) with HTTP status:
     *                           * 422 Unprocessable Entity - if wallet with same name already exist with
     *                           JSON message {"message": "Wallet with same name exist!"}
     *                           * 400 BAD REQUEST - if negative count or incorrect JSON body specified
     */
    @PostMapping("/wallets")
    public ResponseEntity<WalletEntity> createNewWallet(@Valid @RequestBody WalletEntity newWallet) throws MyWalletException {
        log.info("Create Wallet");
        return walletsService.createWallet(newWallet);
    }

    /**
     * The method expects a PUT request at localhost:8080/rest/wallets/{id} and updates the fields of the current
     * object if it exists and the data in the request body is correct
     *
     * @param id     - unique id of the wallet for which the operation is performed
     * @param wallet - JSON object representing new data for exist wallet
     * @return Updated wallet object with HTTP status 200 OK or exception when problems occur
     * <p>
     * Example of request JSON body to 8080/rest/wallets/1: {"account":100,"walletName":"existWalletName"}
     * Example of response JSON body: {"message": "Wallet with same name exist!"} with HTTP status 422 Unprocessable Entity
     * <p>
     * Example of request JSON body to 8080/rest/wallets/1: {"account":100,"walletName":"newWalletName"}
     * Example of request : {"id": 1,"account": 100,"walletName": "newWalletName","createDateTime": "2020-01-31T14:11:53.341284"}
     * with HTTP status 200 OK
     * <p>
     * Example of request JSON body to 8080/rest/wallets/{not exist wallet id}: {"account":100,"walletName":"newWalletName"}
     * Example of response JSON body: {"message": "Unable to update. Wallet with id: 100 not found!"} with HTTP status 404 Not Found
     * <p>
     * @throws MyWalletException - It is thrown if the entered data is incorrect (a negative amount, a wallet with
     *                           the same name exists, incorrect JSON, etc.) with HTTP status:
     *                           * 422 Unprocessable Entity - if wallet with same name already exist with
     *                           JSON message {"message": "Wallet with same name exist!"}
     *                           * 400 BAD REQUEST - if negative count or incorrect JSON body specified
     *                           * 404 Not Found - if the wallet with the given id does not exist
     */
    @PutMapping(value = "/wallets/{id}")
    public ResponseEntity<WalletEntity> updateWallet(@PathVariable long id, @Valid @RequestBody WalletEntity wallet) throws MyWalletException {
        log.info("Update Wallet with id = {}", id);
        return walletsService.updateWallet(id, wallet);
    }

    /**
     * The method expects a DELETE request at localhost:8080/rest/wallets/{id} and delete the current object if it exists
     *
     * @param id - unique id of the wallet for which the operation is performed
     * @return - If the wallet exists, then during the first delete operation it will return a JSON object with the
     * data of this wallet and the HTTP status is 200 OK, with repeated and subsequent delete operations it will return
     * a message: {"message": "Wallet with id: 1 not found!"} with HTTP status 404 Not Found
     * <p>
     * Example of response DELETE first request to 8080/rest/wallets/2:
     * {"id": 2,"account": 10005.05,"walletName": "acc2","createDateTime": "2020-01-31T14:11:53.341284"}
     * with HTTP status 200 OK
     * <p>
     * Example of response DELETE second and other request to 8080/rest/wallets/2:
     * {"message": "Wallet with id: 2 not found!"} with HTTP status 404 Not Found
     * <p>
     * @throws MyWalletException - It is thrown if the entered data is incorrect (a wallet with same id not exist)
     *                           with HTTP status:
     *                           * 404 Not Found - if the wallet with the given id does not exist
     */
    @DeleteMapping(value = "/wallets/{id}")
    public ResponseEntity<WalletEntity> deleteWallet(@PathVariable("id") long id) throws MyWalletException {
        log.info("Delete Wallet with id = {}", id);
        return walletsService.deleteWallet(id);
    }

    /**
     * Deletes all existing wallets
     *
     * @return empty response with HTTP status 204 No Content
     */
    @DeleteMapping(value = "/wallets")
    public ResponseEntity<WalletEntity> deleteAllWallets() {
        log.info("Deleting All Wallets");
        walletsService.deleteAllWallets();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    /**
     * The method expects a GET request at localhost:8080/rest/wallets/transactions and return list all made transaction
     * operations for all wallets object if it exists
     *
     * @return Returns a list of all transactions made.
     * <p>
     * Example of response JSON body:
     * [{"id": 1,"walletId": 1,"walletName": "acc1","transactionType": "sum","transactionAmount": 1.00,
     * "walletAccountAfterTransaction": 1.00,"transactionalDate": "2020-01-31T14:11:53.342801"}]
     * with HTTP status 200 OK
     */
    @ResponseBody
    @GetMapping("/wallets/transactions")
    public ResponseEntity<List<TransactionEntity>> readAllTransactionsOfAllWallets() {
        return transactionsService.findAllTransactionsFromAllWallets();
    }

    /**
     * The method expects a GET request at localhost:8080/rest/wallets/{id}/transactions and returns list all made
     * transactions operations for wallet with id, if it exists and message if isn't
     *
     * @param id - unique id of an existing wallet.
     * @return - list all made transactions operations for wallet with id, HTTP status 200 OK
     * <p>
     * Example of response JSON body:
     * [{"id": 1,"walletId": 1,"walletName": "acc1","transactionType": "sum","transactionAmount": 1.00,
     * "walletAccountAfterTransaction": 1.00,"transactionalDate": "2020-01-31T14:11:53.342801"}]
     * <p>
     * @throws MyWalletException - throws if the wallet with the given id does not exist, the message:
     *                           "Wallet with id: {id} not found!" is returned with HTTP status 404 NOT_FOUND
     */
    @GetMapping("/wallets/{id}/transactions")
    public ResponseEntity<List<TransactionEntity>> readAllTransactionsFromWalletId(@PathVariable long id) throws MyWalletException {
        return transactionsService.findAllTransactionsFromWalletId(id);
    }


    /**
     * The method expects a GET request at localhost:8080/rest/wallets/transactions/{id} and returns JSON object made
     * transaction operation with id, if it exists and message if isn't
     *
     * @param id - unique id of an existing transaction.
     * @return - JSON object made transaction operation with id, HTTP status 200 OK
     * <p>
     * Example of response JSON body:
     * {"id": 1,"walletId": 1,"walletName": "acc1","transactionType": "sum","transactionAmount": 1.00,
     * "walletAccountAfterTransaction": 1.00,"transactionalDate": "2020-01-31T14:11:53.342801"}
     * <p>
     * @throws MyTransactionException - throws if the transaction with the given id does not exist, the message:
     *                                {"message": "Operation with id: 10 not found!"} is returned with HTTP
     *                                status 404 NOT_FOUND
     */
    @GetMapping("/wallets/transactions/{id}")
    public ResponseEntity<TransactionEntity> readTransactionIdFromAllWallets(@PathVariable long id) throws MyTransactionException {
        return transactionsService.findTransactionIdFromAllWallets(id);
    }

    /**
     * The method expects a POST request at localhost:8080/rest/wallets/transactions and create a new wallet transaction
     * operation if the data in the request body is correct
     * <p>
     * Example of  credit transaction JSON body
     * {"walletId": 1,"transactionType": "sum","transactionAmount": 62}
     * <p>
     * Example of debit transaction JSON body
     * {"walletId": 1,"transactionType": "sub","transactionAmount": 100}
     * </p>
     *
     * @param transactionModel contains input parameters
     * @return created transaction in JSON format
     * <p>
     * Example of request JSON body to localhost:8080/rest/wallets/transactions: {"walletId": 1,"transactionType": "sum","transactionAmount": 62}
     * Example of response JSON body: {"message": "Success transaction!"} with HTTP status 201 Created
     * <p>
     * Example of request JSON body to localhost:8080/rest/wallets/transactions: {"walletId": 1,"transactionType": "sub","transactionAmount": 1000}
     * {"message": "Not enough money for operation!"} with HTTP status 422 Unprocessable Entity
     * <p>
     * Example of request JSON body to localhost:8080/rest/wallets/transactions: {"walletId": 101,"transactionType": "sub","transactionAmount": 1000}
     * Example of response JSON body: {"message": "Wallet by ID not found!"} with HTTP status 404 Not Found
     * <p>
     * Example of request JSON body to localhost:8080/rest/wallets/transactions: {"walletId": 1,"transactionType": "123","transactionAmount": 1}
     * Empty body with HTTP status 400 Bad Request if request have incorrect data
     * <p>
     * @throws MyTransactionException when couldn't create transaction (e.g. not enough funds on wallet balance, incorrect data etc.)
     */
    @PostMapping("/wallets/transactions")
    public ResponseEntity<CustomErrorResponse> readTransactionIdFromWalletId(@Valid @RequestBody TransactionModel transactionModel) throws MyTransactionException {
        log.info("Create new transaction for Wallet with id = {}", transactionModel.getWalletId());
        return transactionsService.createTransaction(transactionModel);
    }

}
