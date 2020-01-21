package com.iteco.a.alexandrov.accountOperations.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteco.a.alexandrov.accountOperations.Entity.WalletEntity;
import com.iteco.a.alexandrov.accountOperations.Exceptions.MyWalletException;
import com.iteco.a.alexandrov.accountOperations.Service.TransactionsServiceImpl;
import com.iteco.a.alexandrov.accountOperations.Service.WalletsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(WalletController.class)
public class WalletControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletsServiceImpl service;

    @MockBean
    private TransactionsServiceImpl transactionsService;

    private WalletEntity wallet1;
    private WalletEntity wallet2;

    @Autowired
    private ObjectMapper objectMapper;


    @Before
    public void before() {
        wallet1 = new WalletEntity();
        wallet1.setId(1);
        wallet1.setWalletName("test");
        wallet1.setAccount(new BigDecimal(100));
        wallet1.setVer(0L);
        wallet1.setCreateDateTime(LocalDateTime.now());

        wallet2 = new WalletEntity();
        wallet2.setId(2L);
        wallet2.setWalletName("test2");
        wallet2.setAccount(new BigDecimal(200));
        wallet2.setVer(0L);
        wallet2.setCreateDateTime(LocalDateTime.now());

        //        List listResponseEntity = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), List.class);
    }


    @Test
    public void testGetAll_whenGetWallets_thenHttp200_andJsonArrayList() throws Exception {
        List<WalletEntity> allWallets = Collections.singletonList(wallet1);
        ResponseEntity<List<WalletEntity>> responseEntity = new ResponseEntity<>(allWallets, HttpStatus.OK);

        when(service.findAllWallets()).thenReturn(responseEntity);

        mockMvc.perform(get("/rest/wallets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(allWallets.size())))
                .andExpect(jsonPath("$[0].id", equalTo(wallet1.getId()), Long.class))
                .andExpect(jsonPath("$[0].account", equalTo(wallet1.getAccount()), BigDecimal.class))
                .andExpect(jsonPath("$[0].walletName", equalTo(wallet1.getWalletName())))
                .andExpect(jsonPath("$[0].ver", equalTo(wallet1.getVer()), Long.class));
    }


    @Test
    public void testGetAll_whenWalletsEmpty_GetWallets_thenHttp200_andReturnEmptyList() throws Exception {
        List<WalletEntity> allWallets = new ArrayList<>();
        ResponseEntity<List<WalletEntity>> responseEntity = new ResponseEntity<>(allWallets, HttpStatus.OK);

        when(service.findAllWallets()).thenReturn(responseEntity);

        MvcResult mvcResult = mockMvc.perform(get("/rest/wallets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(allWallets.size())))
                .andReturn();
        assertEquals(mvcResult.getResponse().getContentAsString(), "[]");
    }


    @Test
    public void testGetWalletById_whenGetWalletById_thenHttp200_andJsonResponseEntity() throws Exception {
        ResponseEntity responseEntity = new ResponseEntity<>(wallet1, HttpStatus.OK);

        when(service.readWallet(wallet1.getId())).thenReturn(responseEntity);

        mockMvc.perform(get("/rest/wallets/{id}", wallet1.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(wallet1.getId()), Long.class))
                .andExpect(jsonPath("$.account", equalTo(wallet1.getAccount()), BigDecimal.class))
                .andExpect(jsonPath("$.walletName", equalTo(wallet1.getWalletName())))
                .andExpect(jsonPath("$.ver", equalTo(wallet1.getVer()), Long.class));

    }

    @Test
    public void testGetWalletById_whenGetWalletById_isNotExist_thenHttp404_andJsonResponseMessage() {
        long walletNotExistId = 5;
        ResponseEntity responseEntity = new ResponseEntity<>(String.format("Wallet with id: %d not found!", walletNotExistId), HttpStatus.NOT_FOUND);

        try {
            when(service.readWallet(walletNotExistId)).thenReturn(responseEntity);
            mockMvc.perform(MockMvcRequestBuilders.get("/rest/wallets/{id}", walletNotExistId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            assertThat(e.getMessage(), is(String.format("Wallet with id: %d not found!", walletNotExistId)));
        }

    }

    @Test(expected = Exception.class)
    public void testGetWalletById_whenGetWalletById_isNotExist_thenThrowMyWalletException() throws Exception {
        long walletNotExistId = 5;
        when(service.readWallet(walletNotExistId)).thenThrow(MyWalletException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/rest/wallets/{id}", walletNotExistId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    public void testPutWalletById_whenPutWalletById_thenHttp200_andJsonResponseEntity() throws Exception {
        WalletEntity updatedWallet = wallet1;
        updatedWallet.setWalletName("newNameForTest");
        updatedWallet.setAccount(new BigDecimal(2000));

//        WalletEntity updatedWallet = new WalletEntity();
//        updatedWallet.setWalletName("newNameForTest");
//        updatedWallet.setAccount(new BigDecimal(2000));

        ResponseEntity responseEntity = new ResponseEntity<>("{\"account\":\"2000\",\"walletName\":\"newNameForTest\"}", HttpStatus.OK);

        when(service.updateWallet(wallet1.getId(), updatedWallet)).thenReturn(responseEntity);

        MvcResult response = mockMvc.perform(put("/rest/wallets/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("{\"account\":\"2000\",\"walletName\":\"newNameForTest\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(response);

        // "{\"account\":\"2000\",\"walletName\":\"newNameForTest\"}"

//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .characterEncoding("UTF-8")
//                .content("{\"account\":\"2000\",\"walletName\":\"newNameForTest\"}"))
//                .andExpect(status().isOk())
//                .andReturn().getResponse();
//                .andExpect(jsonPath("$.id", equalTo(updatedWallet.getId()), Long.class))
//                .andExpect(jsonPath("$.account").value(updatedWallet.getAccount()))
//                .andExpect(jsonPath("$.walletName").value(updatedWallet.getWalletName()));
//                .andExpect(jsonPath("$.ver", equalTo(updatedWallet.getVer()), Long.class));

    }


    @Test
    public void testPostWalletById_whenPutWalletById_thenHttp200_andJsonResponseEntity() throws Exception {
        WalletEntity updatedWallet = new WalletEntity();
        updatedWallet.setWalletName("newNameForTest");
        updatedWallet.setAccount(new BigDecimal(2000));

        ResponseEntity responseEntity = new ResponseEntity<>(updatedWallet, HttpStatus.OK);

        when(service.createWallet(updatedWallet)).thenReturn(responseEntity);

        MvcResult response = mockMvc.perform(post("/rest/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("{\"account\":\"2000\",\"walletName\":\"newNameForTest\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }


    // "{\"account\":\"2000\",\"walletName\":\"newNameForTest\"}"

    @Test
    public void createNewWallet() {
    }

    @Test
    public void updateWallet() {
    }

    @Test
    public void deleteWallet() {
    }

    @Test
    public void deleteAllWallets() {
    }

    @Test
    public void readAllTransactionsOfAllWallets() {
    }

    @Test
    public void readAllTransactionsFromWalletId() {
    }

    @Test
    public void readTransactionIdFromAllWallets() {
    }

    @Test
    public void readTransactionIdFromWalletId() {
    }

    @Test
    public void testReadTransactionIdFromWalletId() {
    }
}