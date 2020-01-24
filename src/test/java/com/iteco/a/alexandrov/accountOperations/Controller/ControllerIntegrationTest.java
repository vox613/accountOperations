package com.iteco.a.alexandrov.accountOperations.Controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteco.a.alexandrov.accountOperations.Entity.TransactionEntity;
import com.iteco.a.alexandrov.accountOperations.Entity.WalletEntity;
import com.iteco.a.alexandrov.accountOperations.Exceptions.MyWalletException;
import com.iteco.a.alexandrov.accountOperations.Model.TransactionModel;
import com.iteco.a.alexandrov.accountOperations.Repository.TransactionsRepository;
import com.iteco.a.alexandrov.accountOperations.Repository.WalletsRepository;
import com.iteco.a.alexandrov.accountOperations.Service.TransactionsServiceImpl;
import com.iteco.a.alexandrov.accountOperations.Service.WalletsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.hamcrest.Matchers.*;
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
    private WalletsService walletsService;


    @Autowired
    private TransactionsRepository transactionsRepository;

    @Autowired
    private TransactionsServiceImpl transactionsService;

    private final long idExist = 1;
    private final long idNotExist = 5;


// =========================================== GET all wallets ==========================================

    @Test
    public void testGetAllWallet_whenGetAllWallet_thenHttp200_andJsonArrayList() throws Exception {
        String contentAsString = mockMvc.perform(get("/rest/wallets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List returnedListWalletEntity = objectMapper.readValue(contentAsString, List.class);        // if pretty format and write to DB wil be trouble with deserialization
        assertFalse(returnedListWalletEntity.isEmpty());
        assertEquals(3, returnedListWalletEntity.size());

        List<WalletEntity> walletsRepositoryAll = walletsRepository.findAll();
        assertEquals(walletsRepositoryAll.size(), returnedListWalletEntity.size());
    }


    // =========================================== GET wallet by id ==========================================
    @Test
    public void testGetWalletById_whenGetWalletById_thenHttp200_andJsonResponseEntity() throws Throwable {
        String contentAsString = mockMvc.perform(get("/rest/wallets/{id}", idExist)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // WalletEntity{id=1, account=1.00, walletName='acc1', createDateTime=2020-01-21T16:27:52.522109}
        WalletEntity walletEntityActual = objectMapper.readValue(contentAsString, WalletEntity.class);        // if pretty format and write to DB wil be trouble with deserialization
        WalletEntity walletEntityExpected = walletsRepository.findById(idExist).orElseThrow(Exception::new);
        assertEquals(walletEntityExpected, walletEntityActual);
    }

    @Test
    public void testGetWalletById_whenGetWalletById_isNotExist_thenHttp404_andJsonResponseMessage() throws Exception {
        mockMvc.perform(get("/rest/wallets/{id}", idNotExist)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo(String.format("Wallet with id: %d not found!", idNotExist))));
    }

    @Test
    public void testGetWalletById_whenGetWalletById_isNotExist_thenThrowMyWalletException() {
        try {
            mockMvc.perform(get("/rest/wallets/{id}", idNotExist)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(String.format("Wallet with id: %d not found!", idNotExist)))
                    .andReturn();
        } catch (Exception e) {
            assertEquals(String.format("Wallet with id: %d not found!", idNotExist), e.getMessage());
        }
    }


    // =========================================== Wallets PUT ==========================================
    @Test
    public void testPutWalletById_whenPutWalletById_thenHttp200_andJsonResponseEntity() throws Throwable {

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
    public void testPutWalletById_whenPutWalletById_isNotExist_thenHttp404_andJsonResponseMessage() {
        WalletEntity updatedWallet = new WalletEntity();
        updatedWallet.setWalletName("newNameForTest");
        updatedWallet.setAccount(BigDecimal.valueOf(2000));

        try {
            mockMvc.perform(put("/rest/wallets/{id}", idNotExist)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedWallet)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(String.format("Unable to update. Wallet with id: %d not found!", idNotExist)))
                    .andReturn();
        } catch (Exception e) {
            assertEquals(String.format("Unable to update. Wallet with id: %d not found!", idNotExist), e.getMessage());
        }
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
    public void testPostWallet_whenPostWalletWithAlreadyExistName_thenHttp422_andJsonResponseMessage() {
        WalletEntity updatedWallet = new WalletEntity();
        updatedWallet.setWalletName("acc2");
        updatedWallet.setAccount(new BigDecimal(2000));

        try {
            mockMvc.perform(post("/rest/wallets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(updatedWallet)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.message").value("Wallet with same name exist!"))
                    .andReturn();
        } catch (Exception e) {
            assertEquals("Wallet with same name exist!", e.getMessage());
        }
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

        List returnedListTransactions = objectMapper.readValue(response, List.class);        // if pretty format and write to DB wil be trouble with deserialization
        assertFalse(returnedListTransactions.isEmpty());
        assertEquals(1, returnedListTransactions.size());

        List<TransactionEntity> transactionEntityList = transactionsRepository.findAll();
        assertEquals(transactionEntityList.size(), returnedListTransactions.size());
    }


    // =========================================== GET Transactions by id ==========================================
    @Test
    public void testGetTransactionById_whenGetTransactionsById_thenHttp200_andJsonResponseEntity() throws Throwable {
        String contentAsString = mockMvc.perform(get("/rest/wallets/transactions/{id}", idExist)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();


        TransactionEntity transactionEntityActual = objectMapper.readValue(contentAsString, TransactionEntity.class);        // if pretty format and write to DB wil be trouble with deserialization
        TransactionEntity transactionEntityExpected = transactionsRepository.findById(idExist).orElseThrow(Exception::new);
        assertEquals(transactionEntityExpected, transactionEntityActual);
    }


    @Test
    public void testGetTransactionById_whenTransactionsById_isNotExist_thenHttp404_andJsonResponseMessage() {
        try {
            mockMvc.perform(get("/rest/wallets/transactions/{id}", idNotExist)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(String.format("Operation with id: %d not found!", idNotExist)))
                    .andReturn();
        } catch (Exception e) {
            assertEquals(String.format("Operation with id: %d not found!", idNotExist), e.getMessage());
        }
    }


    @Test
    public void testGetAllTransactionsByWalletId_whenGetAllTransactionsByWalletId_thenHttp200_andJsonArrayList() throws Exception {
        List<TransactionEntity> allTransactions = transactionsRepository.findAllByWalletId(idExist);

        mockMvc.perform(get("/rest/wallets/{id}/transactions", idExist)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(allTransactions.size())));
    }


    @Test
    public void testGetAllTransactionsByWalletId_whenGetAllTransactionsByNotExistWalletId_thenHttp404_andJsonResponseMessage() {
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/rest/wallets/{id}/transactions", idNotExist)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            assertThat(e.getMessage(), is(String.format("Wallet with id: %d not found!", idNotExist)));
        }
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
    public void testPostTransactions_whenPostTransactionsWithNotExistWalletId_thenHttp404_andJsonResponseMessage() {
        final String requestStringToPOST = "{\"walletId\":\"10\",\"transactionType\":\"sum\",\"transactionAmount\":\"100\"}";

        try {
            TransactionModel transactionModel = objectMapper.readValue(requestStringToPOST, TransactionModel.class);
            mockMvc.perform(MockMvcRequestBuilders.post("/rest/wallets/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(transactionModel)))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            System.out.println();
            assertEquals(e.getMessage(), String.format("Operation with id: %d not found!", idNotExist));
        }
    }

    @Test
    public void testPostTransactions_whenPostTransactionsWithIncorrectData_thenHttp404_andJsonResponseMessage() throws JsonProcessingException {
        final String requestStringToPOST = "{\"walletId\":\"1\",\"transactionType\":\"s1m\",\"transactionAmount\":\"100\"}";
        TransactionModel transactionModel = objectMapper.readValue(requestStringToPOST, TransactionModel.class);

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/rest/wallets/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(transactionModel)))
                    .andExpect(status().isUnprocessableEntity());
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Uncorrected operation!"));
        }
    }


    // TODO: 24.01.2020 оформить тест транзакционности, rollback, одновременные операции

    // TODO: 24.01.2020 Написать тесты сервисов и репозиториев написать JavaDoc

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void execute() throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(25);

        Callable<String> callableTaskSub = () -> {
//            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
            System.out.println("1> " + walletsRepository.findById(idExist).orElseThrow(MyWalletException::new).getAccount().toString());
            String s = testPostSub();
            System.out.println("2> " + walletsRepository.findById(idExist).orElseThrow(MyWalletException::new).getAccount().toString());
            return s;
        };
        Callable<String> callableTaskSum = () -> {
//            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(500));
            System.out.println("1> " + walletsRepository.findById(idExist).orElseThrow(MyWalletException::new).getAccount().toString());
            String s = testPostSum();
            System.out.println("2> " + walletsRepository.findById(idExist).orElseThrow(MyWalletException::new).getAccount().toString());
            return s;
        };


        List<Callable<String>> callableTasks = new ArrayList<>();
        callableTasks.add(callableTaskSum);
        callableTasks.add(callableTaskSum);
        callableTasks.add(callableTaskSum);
        callableTasks.add(callableTaskSum);
        callableTasks.add(callableTaskSum);

        callableTasks.add(callableTaskSub);
        callableTasks.add(callableTaskSub);
        callableTasks.add(callableTaskSub);
        callableTasks.add(callableTaskSub);
        callableTasks.add(callableTaskSub);

        callableTasks.add(callableTaskSum);
        callableTasks.add(callableTaskSum);
        callableTasks.add(callableTaskSum);
        callableTasks.add(callableTaskSum);
        callableTasks.add(callableTaskSum);

        callableTasks.add(callableTaskSub);
        callableTasks.add(callableTaskSub);
        callableTasks.add(callableTaskSub);
        callableTasks.add(callableTaskSub);
        callableTasks.add(callableTaskSub);

        callableTasks.add(callableTaskSum);
        callableTasks.add(callableTaskSum);
        callableTasks.add(callableTaskSum);
        callableTasks.add(callableTaskSum);
        callableTasks.add(callableTaskSum);


        List<Future<String>> futures = null;
        try {
            futures = executor.invokeAll(callableTasks);
        } catch (InterruptedException e) {
            System.out.println("1 >>>>>>>>>>>");
            e.printStackTrace();
        }
        Thread.sleep(2000);
        for (int i = 0; i < futures.size(); i++) {
            try {
                System.out.println(i + " = " + futures.get(i).get());
            } catch (ExecutionException e) {
                System.out.println("2 >>>>>>>>>>>");
                e.printStackTrace();
            }
        }

        System.out.println("<><><><> " + walletsRepository.findById(idExist).orElseThrow(MyWalletException::new).getAccount().toString());

    }


//
//    @Before
//    public void init(){
//
//    }


    private String testPostSum() throws Exception {
        final String requestStringToPOST = "{\"walletId\":\"1\",\"transactionType\":\"sum\",\"transactionAmount\":\"100\"}";
        TransactionModel transactionModel = objectMapper.readValue(requestStringToPOST, TransactionModel.class);


        String contentAsString = mockMvc.perform(post("/rest/wallets/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(transactionModel)))
                .andReturn().getResponse().getContentAsString();


        return walletsRepository.findById(idExist).orElseThrow(MyWalletException::new).getAccount().toString();
    }

    private String testPostSub() throws Exception {
        final String requestStringToPOST = "{\"walletId\":\"1\",\"transactionType\":\"sub\",\"transactionAmount\":\"100\"}";
        TransactionModel transactionModel = objectMapper.readValue(requestStringToPOST, TransactionModel.class);

        String contentAsString = mockMvc.perform(post("/rest/wallets/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(transactionModel)))
                .andReturn().getResponse().getContentAsString();

        return walletsRepository.findById(idExist).orElseThrow(MyWalletException::new).getAccount().toString();
    }

}
