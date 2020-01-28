package com.iteco.a.alexandrov.accountOperations.Controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteco.a.alexandrov.accountOperations.Entity.TransactionEntity;
import com.iteco.a.alexandrov.accountOperations.Entity.WalletEntity;
import com.iteco.a.alexandrov.accountOperations.Enum.AvailableTransactions;
import com.iteco.a.alexandrov.accountOperations.Exceptions.MyTransactionException;
import com.iteco.a.alexandrov.accountOperations.Exceptions.MyWalletException;
import com.iteco.a.alexandrov.accountOperations.Model.TransactionModel;
import com.iteco.a.alexandrov.accountOperations.Repository.TransactionsRepository;
import com.iteco.a.alexandrov.accountOperations.Repository.WalletsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class ControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletsRepository walletsRepository;


    @Autowired
    private TransactionsRepository transactionsRepository;

    private final long idExist = 1;
    private final long idNotExist = 5;


// =========================================== GET all wallets ==========================================

    @Test
    public void testGetAllWallet_whenGetAllWallet_thenHttp200_andJsonArrayList() throws Exception {
        String contentAsString = mockMvc.perform(get("/rest/wallets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // if pretty format and write to DB wil be trouble with deserialization
        List<WalletEntity> returnedListWalletEntity = objectMapper.readValue(
                contentAsString, new TypeReference<List<WalletEntity>>() {
                });

        assertFalse(returnedListWalletEntity.isEmpty());
        assertEquals(3, returnedListWalletEntity.size());

        List<WalletEntity> walletsRepositoryAll = walletsRepository.findAll();

        assertEquals(walletsRepositoryAll.size(), returnedListWalletEntity.size());
        assertEquals(walletsRepositoryAll, returnedListWalletEntity);
    }


    // =========================================== GET wallet by id ==========================================
    @Test
    public void testGetWalletById_whenGetWalletById_thenHttp200_andJsonResponseEntity() throws Exception {
        String contentAsString = mockMvc.perform(get("/rest/wallets/{id}", idExist)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // if pretty format and write to DB wil be trouble with deserialization
        WalletEntity walletEntityActual = objectMapper.readValue(contentAsString, WalletEntity.class);
        WalletEntity walletEntityExpected = walletsRepository.findById(idExist).orElseThrow(Exception::new);
        assertEquals(walletEntityExpected, walletEntityActual);
    }


    @Test
    public void testGetWalletById_whenGetWalletById_isNotExist_thenThrowMyWalletException() throws Exception {
        Exception resolvedException = mockMvc.perform(get("/rest/wallets/{id}", idNotExist)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value(String.format("Wallet with id: %d not found!", idNotExist))
                ).andReturn().getResolvedException();
        assertTrue(resolvedException instanceof MyWalletException);
    }


    // =========================================== Wallets PUT ==========================================
    @Test
    public void testPutWalletById_whenPutWalletById_thenHttp200_andJsonResponseEntity() throws Exception {

        WalletEntity updatedWallet = new WalletEntity();
        updatedWallet.setWalletName("newNameForTest");
        updatedWallet.setAccount(BigDecimal.valueOf(2000.05));

        String contentAsString = mockMvc.perform(put("/rest/wallets/{id}", idExist)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedWallet)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        WalletEntity walletEntityActual = objectMapper.readValue(contentAsString, WalletEntity.class);
        WalletEntity walletEntityExpected = walletsRepository.findById(idExist).orElseThrow(RuntimeException::new);

        assertEquals(walletEntityExpected, walletEntityActual);
    }


    @Test
    public void testPutWalletById_whenPutWalletById_isNotExist_thenHttp404_andJsonResponseMessage() throws Exception {
        WalletEntity updatedWallet = new WalletEntity();
        updatedWallet.setWalletName("newNameForTest");
        updatedWallet.setAccount(BigDecimal.valueOf(2000));

        Exception resolvedException = mockMvc.perform(put("/rest/wallets/{id}", idNotExist)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedWallet)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(String.format("Unable to update. Wallet with id: %d not found!", idNotExist))
                )
                .andReturn().getResolvedException();

        assertTrue(resolvedException instanceof MyWalletException);
    }


    // =========================================== Wallets POST =========================================
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testPostWallet_whenPostWallet_thenHttp201_andJsonResponseEntity() throws Exception {
        WalletEntity newWallet = new WalletEntity();
        newWallet.setWalletName("NameForTestPost");
        newWallet.setAccount(BigDecimal.valueOf(3200.89));

        String contentAsString = mockMvc.perform(post("/rest/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(newWallet)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        WalletEntity walletEntityActual = objectMapper.readValue(contentAsString, WalletEntity.class);
        WalletEntity walletEntityExpected = walletsRepository.findByWalletName(newWallet.getWalletName())
                .orElseThrow(MyWalletException::new);

        assertEquals(walletEntityExpected, walletEntityActual);
    }


    @Test
    public void testPostWallet_whenPostWalletWithAlreadyExistName_thenHttp422_andJsonResponseMessage() throws Exception {
        WalletEntity updatedWallet = new WalletEntity();
        updatedWallet.setWalletName("acc2");
        updatedWallet.setAccount(new BigDecimal(2000));

        Exception resolvedException = mockMvc.perform(post("/rest/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(updatedWallet)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Wallet with same name exist!"))
                .andReturn().getResolvedException();

        assertTrue(resolvedException instanceof MyWalletException);
    }


    // =========================================== Wallets DELETE =========================================
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeleteAllWallet_whenDeleteAllWallet_thenHttp204() throws Exception {

        assertFalse(walletsRepository.findAll().isEmpty());

        mockMvc.perform(delete("/rest/wallets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        assertTrue(walletsRepository.findAll().isEmpty());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testDeleteWalletById_whenDeleteWalletById_thenHttp200() throws Exception {

        WalletEntity walletEntityExpected = walletsRepository.findById(idExist).orElseThrow(Exception::new);

        String response = mockMvc.perform(delete("/rest/wallets/{id}", idExist)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        WalletEntity walletEntityActual = objectMapper.readValue(response, WalletEntity.class);
        assertEquals(walletEntityExpected, walletEntityActual);
    }


    // =========================================== GET all Transactions ==========================================

    @Test
    public void testGetAll_whenGetAllTransactions_thenHttp200_andJsonArrayList() throws Exception {
        String response = mockMvc.perform(get("/rest/wallets/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // if pretty format and write to DB wil be trouble with deserialization

        List<TransactionEntity> returnedListTransactions = objectMapper.readValue(response,
                new TypeReference<List<TransactionEntity>>() {
                });

        assertFalse(returnedListTransactions.isEmpty());
        assertEquals(1, returnedListTransactions.size());

        List<TransactionEntity> transactionEntityList = transactionsRepository.findAll();

        assertEquals(transactionEntityList.size(), returnedListTransactions.size());
        assertEquals(transactionEntityList, returnedListTransactions);
    }


    // =========================================== GET Transactions by id ==========================================
    @Test
    public void testGetTransactionById_whenGetTransactionsById_thenHttp200_andJsonResponseEntity() throws Exception {
        String contentAsString = mockMvc.perform(get("/rest/wallets/transactions/{id}", idExist)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        TransactionEntity transactionEntityActual = objectMapper.readValue(contentAsString, TransactionEntity.class);
        TransactionEntity transactionEntityExpected = transactionsRepository.findById(idExist).orElseThrow(Exception::new);
        assertEquals(transactionEntityExpected, transactionEntityActual);
    }


    @Test
    public void testGetTransactionById_whenTransactionsById_isNotExist_thenHttp404_andJsonResponseMessage() throws Exception {
        Exception resolvedException = mockMvc.perform(get("/rest/wallets/transactions/{id}", idNotExist)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(String.format("Operation with id: %d not found!", idNotExist))
                ).andReturn().getResolvedException();

        assertTrue(resolvedException instanceof MyTransactionException);
    }


    @Test
    public void testGetAllTransactionsByWalletId_whenGetAllTransactionsByWalletId_thenHttp200_andJsonArrayList() throws Exception {
        List<TransactionEntity> allTransactions = transactionsRepository.findAllByWalletId(idExist);

        String contentAsString = mockMvc.perform(get("/rest/wallets/{id}/transactions", idExist)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(allTransactions.size())))
                .andReturn().getResponse().getContentAsString();

        List<TransactionEntity> transactionListActual = objectMapper.readValue(contentAsString,
                new TypeReference<List<TransactionEntity>>() {
                });
        assertEquals(allTransactions, transactionListActual);
    }


    @Test
    public void testGetAllTransactionsByWalletId_whenGetAllTransactionsByNotExistWalletId_thenHttp404_andJsonResponseMessage() throws Exception {
        Exception resolvedException = mockMvc.perform(get("/rest/wallets/{id}/transactions", idNotExist)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResolvedException();

        assertTrue(resolvedException instanceof MyWalletException);
    }


    // =========================================== Transactions POST ==========================================
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testPostTransactions_whenPostTransactions_thenHttp201_andJsonResponseMessage() throws Exception {
        final String requestStringToPOST = "{\"walletId\":\"1\",\"transactionType\":\"sum\",\"transactionAmount\":\"100\"}";
        TransactionModel transactionModel = objectMapper.readValue(requestStringToPOST, TransactionModel.class);

        mockMvc.perform(post("/rest/wallets/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(transactionModel)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Success transaction!"));
    }


    @Test
    public void testPostTransactions_whenPostTransactionsWithNotExistWalletId_thenHttp404_andJsonResponseMessage() throws Exception {
        final String requestStringToPOST = "{\"walletId\":\"10\",\"transactionType\":\"sum\",\"transactionAmount\":\"100\"}";

        TransactionModel transactionModel = objectMapper.readValue(requestStringToPOST, TransactionModel.class);
        Exception resolvedException = mockMvc.perform(post("/rest/wallets/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionModel)))
                .andExpect(status().isNotFound())
                .andReturn().getResolvedException();

        assertTrue(resolvedException instanceof MyTransactionException);
    }

    @Test
    public void testPostTransactions_whenPostTransactionsWithIncorrectData_thenHttp404_andJsonResponseMessage() throws Exception {
        final String requestStringToPOST = "{\"walletId\":\"1\",\"transactionType\":\"s1m\",\"transactionAmount\":\"100\"}";
        TransactionModel transactionModel = objectMapper.readValue(requestStringToPOST, TransactionModel.class);

        Exception resolvedException = mockMvc.perform(post("/rest/wallets/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionModel)))
                .andExpect(status().isUnprocessableEntity())
                .andReturn().getResolvedException();

        assertTrue(resolvedException instanceof MyTransactionException);
    }

}
