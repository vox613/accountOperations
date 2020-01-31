package com.iteco.a.alexandrov.accountOperations;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteco.a.alexandrov.accountOperations.Entity.WalletEntity;
import com.iteco.a.alexandrov.accountOperations.Enum.AvailableOperations;
import com.iteco.a.alexandrov.accountOperations.Exceptions.MyWalletException;
import com.iteco.a.alexandrov.accountOperations.Model.TransactionModel;
import com.iteco.a.alexandrov.accountOperations.Repository.WalletsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * The correctness of parallel execution of transactional transactions on one account is tested.
 * For testing, an in-memory H2 database is used, the configuration of which is specified in the
 * application-test.properties file
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class TransactionTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletsRepository walletsRepository;

    private final int threadNum = 20;
    private final BigDecimal operationAmountValue = BigDecimal.valueOf(100.05);

    /**
     * Parallel operations of charging and withdrawing funds from one wallet are tested.
     * The write-off operation may not be performed, because there may not be enough funds.
     * The final result is read from the database and also calculated on the basis of the number of write-offs not performed.
     *
     * @throws MyWalletException    - Throws if the wallet with the given id does not exist
     * @throws ExecutionException   - if the computation threw an exception
     * @throws InterruptedException - if the current thread was interrupted
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)

    public void testParallel_SumAndSub_TransactionsOnOneWallet() throws MyWalletException, ExecutionException, InterruptedException {
        final long walletId = 1L;
        final int numSumOperations = 11;
        final int numSubOperations = 9;

        WalletEntity walletForOperations = walletsRepository.findById(walletId).orElseThrow(MyWalletException::new);
        BigDecimal accountExpected = executeTransactionBunch(walletForOperations, numSumOperations, operationAmountValue,
                numSubOperations, operationAmountValue);
        BigDecimal accountActual = walletsRepository.findById(walletId).orElseThrow(MyWalletException::new).getAccount();
        System.out.println("accountExpected = " + accountExpected + "\naccountActual = " + accountActual);
        assertEquals(accountExpected, accountActual);
    }


    /**
     * Parallel Top-Up Transactions Tested
     * The final result is read from the database and also calculated on the basis of the number of write-offs not performed.
     *
     * @throws MyWalletException    - Throws if the wallet with the given id does not exist
     * @throws ExecutionException   - if the computation threw an exception
     * @throws InterruptedException - if the current thread was interrupted
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testParallel_Sum_TransactionsOnOneWallet() throws ExecutionException, InterruptedException, MyWalletException {
        final long walletId = 1L;
        final int numSumOperations = 100;
        final int numSubOperations = 0;

        WalletEntity walletForOperations = walletsRepository.findById(walletId).orElseThrow(MyWalletException::new);
        BigDecimal accountExpected = executeTransactionBunch(walletForOperations, numSumOperations, operationAmountValue,
                numSubOperations, BigDecimal.ZERO);
        BigDecimal accountActual = walletsRepository.findById(walletId).orElseThrow(MyWalletException::new).getAccount();
        System.out.println("accountExpected = " + accountExpected + "\naccountActual = " + accountActual);
        assertEquals(accountExpected, accountActual);
    }


    /**
     * Parallel debit transactions are being tested. There may be situations when there is insufficient funds in the account.
     * The final result is read from the database and also calculated on the basis of the number of write-offs not performed.
     *
     * @throws MyWalletException    - Throws if the wallet with the given id does not exist
     * @throws ExecutionException   - if the computation threw an exception
     * @throws InterruptedException - if the current thread was interrupted
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testParallel_Sub_TransactionsOnOneWallet() throws ExecutionException, InterruptedException, MyWalletException {
        final long walletId = 2L;
        final int numSumOperations = 0;
        final int numSubOperations = 110;

        WalletEntity walletForOperations = walletsRepository.findById(walletId).orElseThrow(MyWalletException::new);
        BigDecimal accountExpected = executeTransactionBunch(walletForOperations, numSumOperations, BigDecimal.ZERO,
                numSubOperations, operationAmountValue);
        BigDecimal accountActual = walletsRepository.findById(walletId).orElseThrow(MyWalletException::new).getAccount();
        System.out.println("accountExpected = " + accountExpected + "\naccountActual = " + accountActual);
        assertEquals(accountExpected, accountActual);
    }


    private BigDecimal executeTransactionBunch(WalletEntity walletForOperations,
                                               int numSumOperations, BigDecimal amountSumOperation,
                                               int numSubOperations, BigDecimal amountSubOperation) throws ExecutionException, InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(threadNum);

        Callable<String> callableTaskSum = () ->
                initTransactionOperation(walletForOperations.getId(), AvailableOperations.SUM, amountSumOperation);

        Callable<String> callableTaskSub = () ->
                initTransactionOperation(walletForOperations.getId(), AvailableOperations.SUB, amountSubOperation);

        final List<Future<String>> futureList = new ArrayList<>();
        IntStream.range(0, numSumOperations).forEach(i -> futureList.add(executor.submit(callableTaskSum)));
        IntStream.range(0, numSubOperations).forEach(i -> futureList.add(executor.submit(callableTaskSub)));
        executor.shutdown();

        return calculateExpectedWalletAccountValue(futureList, walletForOperations,
                numSumOperations, amountSumOperation,
                numSubOperations, amountSubOperation);
    }


    private BigDecimal calculateExpectedWalletAccountValue(List<Future<String>> futures,
                                                           WalletEntity walletForOperations,
                                                           int numSumOperations, BigDecimal amountSumOperation,
                                                           int numSubOperations, BigDecimal amountSubOperation) throws ExecutionException, InterruptedException {

        final BigDecimal numSum = BigDecimal.valueOf(numSumOperations);
        final BigDecimal numSub = BigDecimal.valueOf(numSubOperations);
        final BigDecimal startWalletAccount = walletForOperations.getAccount();

        long notEnoughMoneyOperations = 0;
        for (Future<String> future : futures) {
            notEnoughMoneyOperations += future.get().equals("{\"message\":\"Not enough money for operation!\"}") ? 1 : 0;
        }
        BigDecimal unsuccessfulTransaction = BigDecimal.valueOf(notEnoughMoneyOperations);

        return ((numSum.multiply(amountSumOperation))
                .subtract(((numSub.subtract(unsuccessfulTransaction)).multiply(amountSubOperation))))
                .add(startWalletAccount);
    }


    private String initTransactionOperation(Long walletId, AvailableOperations operation, BigDecimal amount) {
        String contentAsString = null;
        try {
            contentAsString = mockMvc.perform(post("/rest/wallets/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(
                            new TransactionModel(walletId, operation.getValue(), amount))))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentAsString;
    }
}



