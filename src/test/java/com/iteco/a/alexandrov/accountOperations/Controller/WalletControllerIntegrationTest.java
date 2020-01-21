package com.iteco.a.alexandrov.accountOperations.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteco.a.alexandrov.accountOperations.Entity.WalletEntity;
import com.iteco.a.alexandrov.accountOperations.Repository.WalletsRepository;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletControllerIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletsRepository walletsRepository;

    private final long idExistWallet = 1;
    private final long idNotExistWallet = 5;




////////////////////////////////////////////
//              GET all
///////////////////////////////////////////


    @Test
    @Order(1)
    public void testGetAllWallet_whenGetAllWallet_thenHttp200_andJsonArrayList() throws Exception {
        MvcResult response = mockMvc.perform(get("/rest/wallets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List returnedListWalletEntity = objectMapper.readValue(response.getResponse().getContentAsString(), List.class);        // if pretty format and write to DB wil be trouble with deserialization
        assertFalse(returnedListWalletEntity.isEmpty());
        assertEquals(3, returnedListWalletEntity.size());

        List<WalletEntity> walletsRepositoryAll = walletsRepository.findAll();
        assertEquals(walletsRepositoryAll.size(), returnedListWalletEntity.size());
    }



////////////////////////////////////////////
//              GET by id
///////////////////////////////////////////

    @Test
    @Order(2)
    public void testGetWalletById_whenGetWalletById_thenHttp200_andJsonResponseEntity() throws Exception {
        MvcResult response = mockMvc.perform(get("/rest/wallets/{id}", idExistWallet)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // WalletEntity{id=1, account=1.00, walletName='acc1', createDateTime=2020-01-21T16:27:52.522109}
        WalletEntity returnedEntity = objectMapper.readValue(response.getResponse().getContentAsString(), WalletEntity.class);        // if pretty format and write to DB wil be trouble with deserialization
        WalletEntity entityFromDB = walletsRepository.findById(idExistWallet).orElseThrow(Exception::new);
        assertEquals(returnedEntity, entityFromDB);
    }

    @Test
    @Order(3)
    public void testGetWalletById_whenGetWalletById_isNotExist_thenHttp404_andJsonResponseMessage() throws Exception {
        mockMvc.perform(get("/rest/wallets/{id}", idNotExistWallet)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo(String.format("Wallet with id: %d not found!", idNotExistWallet))));
    }

    @Test
    @Order(4)
    public void testGetWalletById_whenGetWalletById_isNotExist_thenThrowMyWalletException() {
        try {
            mockMvc.perform(get("/rest/wallets/{id}", idNotExistWallet)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(String.format("Wallet with id: %d not found!", idNotExistWallet)))
                    .andReturn();
        } catch (Exception e) {
            assertEquals(e.getMessage(), String.format("Wallet with id: %d not found!", idNotExistWallet));
        }
    }


////////////////////////////////////////////
//              PUT
///////////////////////////////////////////

    @Test
    @Order(5)
    public void testPutWalletById_whenPutWalletById_thenHttp200_andJsonResponseEntity() throws Exception {

        WalletEntity updatedWallet = new WalletEntity();
        updatedWallet.setWalletName("newNameForTest");
        updatedWallet.setAccount(new BigDecimal(2000));

        mockMvc.perform(put("/rest/wallets/{id}", idExistWallet)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedWallet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idExistWallet))
                .andExpect(jsonPath("$.account").value(updatedWallet.getAccount()))
                .andExpect(jsonPath("$.walletName").value(updatedWallet.getWalletName()));
    }


    @Test
    @Order(6)
    public void testPutWalletById_whenPutWalletById_isNotExist_thenHttp404_andJsonResponseMessage() {
        WalletEntity updatedWallet = new WalletEntity();
        updatedWallet.setWalletName("newNameForTest");
        updatedWallet.setAccount(new BigDecimal(2000));

        try {
            mockMvc.perform(put("/rest/wallets/{id}", idNotExistWallet)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedWallet)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(String.format("Unable to update. Wallet with id: %d not found!", idNotExistWallet)))
                    .andReturn();
        } catch (Exception e) {
            assertEquals(e.getMessage(), String.format("Unable to update. Wallet with id: %d not found!", idNotExistWallet));
        }
    }



////////////////////////////////////////////
//              POST
///////////////////////////////////////////


    @Test
    @Order(7)
    public void testPostWalletById_whenPutWalletById_thenHttp201_andJsonResponseEntity() throws Exception {
        WalletEntity updatedWallet = new WalletEntity();
        updatedWallet.setWalletName("NameForTestPost");
        updatedWallet.setAccount(new BigDecimal(2000));

        mockMvc.perform(post("/rest/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(updatedWallet)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.account").value(updatedWallet.getAccount()))
                .andExpect(jsonPath("$.walletName").value(updatedWallet.getWalletName()))
                .andExpect(jsonPath("$.ver").value(0))
                .andReturn();
    }

    @Test
    @Order(8)
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
            assertEquals(e.getMessage(), "Wallet with same name exist!");
        }
    }



////////////////////////////////////////////
//              DELETE
///////////////////////////////////////////


    @Test
    @Order(10)
    public void testDeleteAllWallet_whenDeleteAllWallet_thenHttp204() throws Exception {

        assertFalse(walletsRepository.findAll().isEmpty());

        MvcResult response = mockMvc.perform(delete("/rest/wallets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        assertTrue(walletsRepository.findAll().isEmpty());
    }

    @Test
    @Order(9)
    public void testDeleteWalletById_whenDeleteWalletById_thenHttp200() throws Exception {

        WalletEntity walletEntity = walletsRepository.findById(idExistWallet).orElseThrow(Exception::new);

        String response = mockMvc.perform(delete("/rest/wallets/{id}", idExistWallet)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(walletEntity, objectMapper.readValue(response, WalletEntity.class));
    }

}
