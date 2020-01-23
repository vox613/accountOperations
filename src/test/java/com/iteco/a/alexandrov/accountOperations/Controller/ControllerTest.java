
package com.iteco.a.alexandrov.accountOperations.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteco.a.alexandrov.accountOperations.Entity.TransactionEntity;
import com.iteco.a.alexandrov.accountOperations.Entity.WalletEntity;
import com.iteco.a.alexandrov.accountOperations.Enum.AvailableTransactions;
import com.iteco.a.alexandrov.accountOperations.Exceptions.Error.CustomErrorResponse;
import com.iteco.a.alexandrov.accountOperations.Exceptions.MyTransactionException;
import com.iteco.a.alexandrov.accountOperations.Exceptions.MyWalletException;
import com.iteco.a.alexandrov.accountOperations.Model.TransactionModel;
import com.iteco.a.alexandrov.accountOperations.Service.TransactionsServiceImpl;
import com.iteco.a.alexandrov.accountOperations.Service.WalletsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(Controller.class)
public class ControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletsServiceImpl walletsService;

    @MockBean
    private TransactionsServiceImpl transactionsService;

    private WalletEntity wallet1;
    private TransactionEntity transaction1;

    @Autowired
    private ObjectMapper objectMapper;

    private final long idExist = 1;
    private final long idNotExist = 5;


    @Before
    public void before() {
        wallet1 = new WalletEntity();
        wallet1.setId(1);
        wallet1.setWalletName("test");
        wallet1.setAccount(new BigDecimal(100));
        wallet1.setVer(0L);
        wallet1.setCreateDateTime(LocalDateTime.now());

        transaction1 = new TransactionEntity();
        transaction1.setTransactionAmount(new BigDecimal(100));
        transaction1.setWalletId(1L);
        transaction1.setTransactionType(AvailableTransactions.SUM.getValue());
        transaction1.setVer(0L);
        transaction1.setWalletName("acc1");
        transaction1.setWalletAccountAfterTransaction(new BigDecimal(1));
        transaction1.setTransactionalDate(LocalDateTime.now());

    }


    // =========================================== Wallets GET all ==========================================
    @Test
    public void testGetAll_whenGetWallets_thenHttp200_andJsonArrayList() throws Exception {
        List<WalletEntity> allWallets = Collections.singletonList(wallet1);
        ResponseEntity<List<WalletEntity>> responseEntity = new ResponseEntity<>(allWallets, HttpStatus.OK);

        when(walletsService.findAllWallets()).thenReturn(responseEntity);

        mockMvc.perform(get("/rest/wallets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(allWallets.size())))
                .andExpect(jsonPath("$[0].id").value(wallet1.getId()))
                .andExpect(jsonPath("$[0].account").value(wallet1.getAccount()))
                .andExpect(jsonPath("$[0].walletName").value(wallet1.getWalletName()))
                .andExpect(jsonPath("$[0].ver").value(wallet1.getVer()));
    }


    @Test
    public void testGetAll_whenWalletsEmpty_GetWallets_thenHttp200_andReturnEmptyList() throws Exception {
        List<WalletEntity> allWallets = new ArrayList<>();
        ResponseEntity<List<WalletEntity>> responseEntity = new ResponseEntity<>(allWallets, HttpStatus.OK);

        when(walletsService.findAllWallets()).thenReturn(responseEntity);

        MvcResult mvcResult = mockMvc.perform(get("/rest/wallets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(allWallets.size())))
                .andReturn();
        assertEquals(mvcResult.getResponse().getContentAsString(), "[]");
    }

    // =========================================== Wallets GET by id ==========================================
    @Test
    public void testGetWalletById_whenGetWalletById_thenHttp200_andJsonResponseEntity() throws Exception {
        ResponseEntity<WalletEntity> responseEntity = new ResponseEntity<>(wallet1, HttpStatus.OK);

        when(walletsService.readWallet(wallet1.getId())).thenReturn(responseEntity);

        String contentAsString = mockMvc.perform(get("/rest/wallets/{id}", wallet1.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        WalletEntity walletEntity = objectMapper.readValue(contentAsString, WalletEntity.class);
        assertEquals(walletEntity, wallet1);
    }

    @Test
    public void testGetWalletById_whenGetWalletById_isNotExist_thenHttp404_andJsonResponseMessage() {
        long walletNotExistId = 5;
        ResponseEntity responseEntity = new ResponseEntity<>(String.format("Wallet with id: %d not found!", walletNotExistId), HttpStatus.NOT_FOUND);

        try {
            when(walletsService.readWallet(walletNotExistId)).thenReturn(responseEntity);
            mockMvc.perform(MockMvcRequestBuilders.get("/rest/wallets/{id}", walletNotExistId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
            throw new MyWalletException(String.format("Wallet with id: %d not found!", walletNotExistId), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            assertThat(e.getMessage(), is(String.format("Wallet with id: %d not found!", walletNotExistId)));
        }

    }

    // =========================================== Wallets PUT ==========================================
    @Test
    public void testPutWalletById_whenPutWalletById_thenHttp200_andJsonResponseEntity() throws Exception {
        final String requestStringToPUT = "{\"account\":\"2000\",\"walletName\":\"newNameForTest\"}";
        WalletEntity walletEntity = objectMapper.readValue(requestStringToPUT, WalletEntity.class);

        WalletEntity updatedWallet = wallet1;
        updatedWallet.setWalletName(walletEntity.getWalletName());
        updatedWallet.setAccount(walletEntity.getAccount());

        ResponseEntity<WalletEntity> responseEntity = new ResponseEntity<>(updatedWallet, HttpStatus.OK);
        when(walletsService.updateWallet(any(long.class), any(WalletEntity.class))).thenReturn(responseEntity);

        String contentAsString = mockMvc.perform(put("/rest/wallets/{id}", wallet1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(walletEntity)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        WalletEntity walletEntity1 = objectMapper.readValue(contentAsString, WalletEntity.class);
        assertEquals(walletEntity1, updatedWallet);

    }

    @Test
    public void testPutWalletById_whenPutWalletById_isNotExist_thenHttp404_andJsonResponseMessage() {
        final String requestStringToPUT = "{\"account\":\"2000\",\"walletName\":\"newNameForTest\"}";

        try {
            WalletEntity walletEntity = objectMapper.readValue(requestStringToPUT, WalletEntity.class);
            ResponseEntity responseEntity = new ResponseEntity<>(String.format("Wallet with id: %d not found!", idNotExist), HttpStatus.NOT_FOUND);

            when(walletsService.updateWallet(any(long.class), any(WalletEntity.class))).thenReturn(responseEntity);

            mockMvc.perform(put("/rest/wallets/{id}", idNotExist)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(walletEntity)))
                    .andExpect(status().isNotFound());
            throw new MyWalletException(String.format("Wallet with id: %d not found!", idNotExist), HttpStatus.NOT_FOUND);
        } catch (MyWalletException ex) {
            assertThat(ex.getMessage(), is(String.format("Wallet with id: %d not found!", idNotExist)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // =========================================== Wallets POST ==========================================
    @Test
    public void testPostWalletById_whenPostWalletById_thenHttp200_andJsonResponseEntity() throws Exception {
        final String requestStringToPOST = "{\"account\":\"2000\",\"walletName\":\"newWallet\"}";
        WalletEntity walletEntity = objectMapper.readValue(requestStringToPOST, WalletEntity.class);

        WalletEntity updatedWallet = wallet1;
        updatedWallet.setWalletName(walletEntity.getWalletName());
        updatedWallet.setAccount(walletEntity.getAccount());

        ResponseEntity<WalletEntity> responseEntity = new ResponseEntity<>(updatedWallet, HttpStatus.CREATED);

        when(walletsService.createWallet(any(WalletEntity.class))).thenReturn(responseEntity);

        MvcResult mvcResult = mockMvc.perform(post("/rest/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(walletEntity)))
                .andExpect(status().isCreated())
                .andReturn();

        WalletEntity walletEntity1 = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), WalletEntity.class);
        assertEquals(walletEntity1, wallet1);
    }


    @Test
    public void testPostWallet_whenPostIncorrectWallet_thenHttp400() throws Exception {
        final String requestStringToPOST = "{\"account\":\"2000\",\"walletName\":\"newWallet\"}";
        WalletEntity incorrectEntity = objectMapper.readValue(requestStringToPOST, WalletEntity.class);
        ResponseEntity<WalletEntity> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        when(walletsService.createWallet(any(WalletEntity.class))).thenReturn(responseEntity);

        mockMvc.perform(post("/rest/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(incorrectEntity)))
                .andExpect(status().isBadRequest());
    }


    // =========================================== Wallets DELETE =========================================
    @Test
    public void testDeleteAllWallet_whenDeleteAllWallet_thenHttp204() throws Exception {
        ResponseEntity<CustomErrorResponse> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);

        when(walletsService.deleteAllWallets()).thenReturn(responseEntity);

        mockMvc.perform(delete("/rest/wallets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    public void testDeleteWalletById_whenDeleteWalletById_thenHttp200() throws Exception {

        ResponseEntity responseEntityFirst = new ResponseEntity<>(wallet1, HttpStatus.OK);
        ResponseEntity responseEntityOther = new ResponseEntity<>(
                String.format("Wallet with id: %d not found!", wallet1.getId()),
                HttpStatus.NOT_FOUND);

        when(walletsService.deleteWallet(wallet1.getId())).thenReturn(responseEntityFirst).thenReturn(responseEntityOther);


        //First request
        String response = mockMvc.perform(delete("/rest/wallets/{id}", idExist)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(wallet1, objectMapper.readValue(response, WalletEntity.class));

        //Other request
        try {
            mockMvc.perform(delete("/rest/wallets/{id}", idExist)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();
            throw new MyWalletException(String.format("Wallet with id: %d not found!", idExist), HttpStatus.NOT_FOUND);
        } catch (MyWalletException ex) {
            assertThat(ex.getMessage(), is(String.format("Wallet with id: %d not found!", idExist)));
        }
    }


    // =========================================== Transactions GET all ==========================================
    @Test
    public void testGetAll_whenGetAllTransactions_thenHttp200_andJsonArrayList() throws Exception {
        List<TransactionEntity> allTransactions = Collections.singletonList(transaction1);
        ResponseEntity<List<TransactionEntity>> listResponseEntity = new ResponseEntity<>(allTransactions, HttpStatus.OK);

        when(transactionsService.findAllTransactionsFromAllWallets()).thenReturn(listResponseEntity);

        mockMvc.perform(get("/rest/wallets/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(allTransactions.size())))
                .andExpect(jsonPath("$[0].id").value(transaction1.getId()))
                .andExpect(jsonPath("$[0].walletName").value(transaction1.getWalletName()))
                .andExpect(jsonPath("$[0].transactionType").value(transaction1.getTransactionType()))
                .andExpect(jsonPath("$[0].transactionAmount").value(transaction1.getTransactionAmount()))
                .andExpect(jsonPath("$[0].walletAccountAfterTransaction").value(transaction1.getWalletAccountAfterTransaction()));

    }


    // =========================================== Transactions GET by id ==========================================
    @Test
    public void testGetTransactionById_whenGetTransactionsById_thenHttp200_andJsonResponseEntity() throws Exception {
        ResponseEntity<TransactionEntity> responseEntity = new ResponseEntity<>(transaction1, HttpStatus.OK);

        when(transactionsService.findTransactionIdFromAllWallets(transaction1.getId())).thenReturn(responseEntity);

        String contentAsString = mockMvc.perform(get("/rest/wallets/transactions/{id}", transaction1.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        TransactionEntity transactionEntity = objectMapper.readValue(contentAsString, TransactionEntity.class);
        assertEquals(transaction1, transactionEntity);
    }

    @Test
    public void testGetTransactionById_whenTransactionsById_isNotExist_thenHttp404_andJsonResponseMessage() {

        ResponseEntity responseEntity = new ResponseEntity<>("Wallet by ID not found!", HttpStatus.NOT_FOUND);

        try {
            when(transactionsService.findTransactionIdFromAllWallets(idNotExist)).thenReturn(responseEntity);
            mockMvc.perform(MockMvcRequestBuilders.get("/rest/wallets/transactions/{id}", idNotExist)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
            throw new MyTransactionException(String.format("Operation with id: %d not found!", idNotExist), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            assertThat(e.getMessage(), is(String.format("Operation with id: %d not found!", idNotExist)));
        }
    }


    @Test
    public void testGetAllTransactionsByWalletId_whenGetAllTransactionsByWalletId_thenHttp200_andJsonArrayList() throws Exception {

        List<TransactionEntity> allTransactions = Collections.singletonList(transaction1);
        ResponseEntity<List<TransactionEntity>> listResponseEntity = new ResponseEntity<>(allTransactions, HttpStatus.OK);

        when(transactionsService.findAllTransactionsFromWalletId(idExist)).thenReturn(listResponseEntity);

        String contentAsString = mockMvc.perform(get("/rest/wallets/{id}/transactions", idExist)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(allTransactions.size())))
                .andReturn().getResponse().getContentAsString();

        List transactionEntity = objectMapper.readValue(contentAsString, List.class);
        assertEquals(allTransactions, transactionEntity);
    }



    @Test
    public void testGetAllTransactionsByWalletId_whenGetAllTransactionsByNotExistWalletId_thenHttp404_andJsonResponseMessage() {

        ResponseEntity responseEntity = new ResponseEntity<>(String.format("Wallet with id: %d not found!", idNotExist), HttpStatus.NOT_FOUND);

        try {
            when(transactionsService.findAllTransactionsFromWalletId(idNotExist)).thenReturn(responseEntity);
            mockMvc.perform(MockMvcRequestBuilders.get("/rest/wallets/{id}/transactions", idNotExist)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
            throw new MyTransactionException(String.format("Wallet with id: %d not found!", idNotExist), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            assertThat(e.getMessage(), is(String.format("Wallet with id: %d not found!", idNotExist)));
        }
    }


    // =========================================== Transactions POST ==========================================
    @Test
    public void testPostTransactions_whenPostTransactions_thenHttp201_andJsonResponseMessage() throws Exception {
        final String requestStringToPOST = "{\"walletId\":\"1\",\"transactionType\":\"sum\",\"transactionAmount\":\"100\"}";
        TransactionModel transactionModel = objectMapper.readValue(requestStringToPOST, TransactionModel.class);

        ResponseEntity<CustomErrorResponse> responseEntity = new ResponseEntity<>(new CustomErrorResponse("Success transaction!"), HttpStatus.CREATED);

        when(transactionsService.createTransaction(any(TransactionModel.class))).thenReturn(responseEntity);

        mockMvc.perform(post("/rest/wallets/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(transactionModel)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Success transaction!"));
    }


    @Test
    public void testPostTransactions_whenPostTransactionsWithNotExistWalletId_thenHttp404_andJsonResponseMessage() {
        final String requestStringToPOST = "{\"walletId\":\"10\",\"transactionType\":\"sum\",\"transactionAmount\":\"100\"}";
        ResponseEntity<CustomErrorResponse> responseEntity = new ResponseEntity<>(
                new CustomErrorResponse("Wallet by ID not found!"), HttpStatus.NOT_FOUND);

        try {
            TransactionModel transactionModel = objectMapper.readValue(requestStringToPOST, TransactionModel.class);
            when(transactionsService.createTransaction(any(TransactionModel.class))).thenReturn(responseEntity);
            mockMvc.perform(MockMvcRequestBuilders.post("/rest/wallets/transactions", idNotExist)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(transactionModel)))
                    .andExpect(status().isNotFound());
            throw new MyTransactionException(String.format("Operation with id: %d not found!", idNotExist), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            assertThat(e.getMessage(), is(String.format("Operation with id: %d not found!", idNotExist)));
        }
    }

    @Test
    public void testPostTransactions_whenPostTransactionsWithIncorrectData_thenHttp404_andJsonResponseMessage() throws JsonProcessingException {
        final String requestStringToPOST = "{\"walletId\":\"1\",\"transactionType\":\"s1m\",\"transactionAmount\":\"100\"}";
        TransactionModel transactionModel = objectMapper.readValue(requestStringToPOST, TransactionModel.class);

        ResponseEntity<CustomErrorResponse> responseEntity = new ResponseEntity<>(
                new CustomErrorResponse("Uncorrected operation!"), HttpStatus.UNPROCESSABLE_ENTITY);

        try {
            when(transactionsService.createTransaction(any(TransactionModel.class))).thenReturn(responseEntity);
            mockMvc.perform(MockMvcRequestBuilders.post("/rest/wallets/transactions", idExist)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(transactionModel)))
                    .andExpect(status().isUnprocessableEntity());
            throw new MyTransactionException("Uncorrected operation!", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Uncorrected operation!"));
        }
    }

}