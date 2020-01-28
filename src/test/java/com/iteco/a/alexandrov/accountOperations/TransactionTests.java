package com.iteco.a.alexandrov.accountOperations;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteco.a.alexandrov.accountOperations.Entity.WalletEntity;
import com.iteco.a.alexandrov.accountOperations.Enum.AvailableTransactions;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testParallel_SumAndSub_TransactionsOnOneWallet() throws Exception {
        final long walletId = 1L;
        final int numSumOperations = 11;
        final int numSubOperations = 9;
        final BigDecimal amountSumOperation = new BigDecimal("100.05");
        final BigDecimal amountSubOperation = new BigDecimal("100.05");

        WalletEntity walletForOperations = walletsRepository.findById(walletId).orElseThrow(MyWalletException::new);
        BigDecimal accountExpected = executeTransaction(walletForOperations, numSumOperations, amountSumOperation, numSubOperations, amountSubOperation);
        BigDecimal accountActual = walletsRepository.findById(walletId).orElseThrow(MyWalletException::new).getAccount();
        System.out.println("accountExpected = " + accountExpected + "\naccountActual = " + accountActual);
        assertEquals(accountExpected, accountActual);
    }




    private BigDecimal executeTransaction(WalletEntity walletForOperations,
                                          int numSumOperations, BigDecimal amountSumOperation,
                                          int numSubOperations, BigDecimal amountSubOperation) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(threadNum);

        List<Callable<String>> taskSet = createTaskSet(walletForOperations.getId(), numSumOperations, amountSumOperation, numSubOperations, amountSubOperation);
        List<Future<String>> futures = executor.invokeAll(taskSet);
        Thread.sleep(1000);

        return calculateExpectedWalletAccountValue(futures, walletForOperations, numSumOperations, numSubOperations, amountSubOperation);
    }


    private BigDecimal calculateExpectedWalletAccountValue(List<Future<String>> futures,
                                                           WalletEntity walletForOperations,
                                                           int numSumOperations, int numSubOperations,
                                                           BigDecimal amount) throws Exception {

        final BigDecimal numSum = BigDecimal.valueOf(numSumOperations);
        final BigDecimal numSub = BigDecimal.valueOf(numSubOperations);
        final BigDecimal startWalletAccount = walletForOperations.getAccount();

        long notEnoughMoneyOperations = 0;
        for (Future<String> future : futures) {
            notEnoughMoneyOperations += future.get().equals("{\"message\":\"Not enough money for operation!\"}") ? 1 : 0;
        }
        BigDecimal unsuccessfulTransaction = BigDecimal.valueOf(notEnoughMoneyOperations);

        return ((numSum.multiply(amount))
                .subtract((numSub.subtract(unsuccessfulTransaction).multiply(amount))))
                .add(startWalletAccount);
    }


    private List<Callable<String>> createTaskSet(long walletId,
                                                 int numSumOperations, BigDecimal amountSumOperation,
                                                 int numSubOperations, BigDecimal amountSubOperation) {

        Callable<String> callableTaskSum = () -> initTransactionOperation(walletId, AvailableTransactions.SUM, amountSumOperation);
        Callable<String> callableTaskSub = () -> initTransactionOperation(walletId, AvailableTransactions.SUB, amountSubOperation);

        List<Callable<String>> taskSet = new ArrayList<>();
        for (int i = 0; i < numSumOperations; i++) {
            taskSet.add(callableTaskSum);
        }
        for (int i = 0; i < numSubOperations; i++) {
            taskSet.add(callableTaskSub);
        }
        return taskSet;
    }


    private String initTransactionOperation(Long walletId, AvailableTransactions operation, BigDecimal amount) {
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



