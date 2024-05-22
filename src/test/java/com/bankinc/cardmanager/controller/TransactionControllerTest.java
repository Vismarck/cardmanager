package com.bankinc.cardmanager.controller;

import com.bankinc.cardmanager.model.entity.Transaction;
import com.bankinc.cardmanager.model.request.AnulateTransactionRequest;
import com.bankinc.cardmanager.model.request.PurchaseRequest;
import com.bankinc.cardmanager.service.TransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void purchase_Success() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setCardId("1234567890123456");
        request.setPrice(100.0);

        Transaction transaction = new Transaction();
        transaction.setTransaction_id(1L);
        transaction.setAmount(100.0);

        when(transactionService.purchase(request.getCardId(), request.getPrice())).thenReturn(transaction);

        mockMvc.perform(post("/transaction/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cardId\":\"1234567890123456\",\"price\":100.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(1L))
                .andExpect(jsonPath("$.amount").value(100.0));
    }

    @Test
    public void purchase_Failure() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setCardId("1234567890123456");
        request.setPrice(100.0);

        when(transactionService.purchase(request.getCardId(), request.getPrice())).thenThrow(new RuntimeException("Transaction failed"));

        mockMvc.perform(post("/transaction/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cardId\":\"1234567890123456\",\"price\":100.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Transaction failed"));
    }

    @Test
    public void getTransaction_Success() throws Exception {
        Long transactionId = 1L;
        Transaction transaction = new Transaction();
        transaction.setTransaction_id(transactionId);
        transaction.setAmount(100.0);

        when(transactionService.getTransactionById(transactionId)).thenReturn(transaction);

        mockMvc.perform(get("/transaction/{transactionId}", transactionId))
            .andDo(print()) 
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(transactionId))
            .andExpect(jsonPath("$.amount").value(100.0));
    }

    @Test
    public void getTransaction_Failure() throws Exception {
        Long transactionId = 1L;

        when(transactionService.getTransactionById(transactionId)).thenThrow(new RuntimeException("Transaction not found"));

        mockMvc.perform(get("/transaction/{transactionId}", transactionId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Transaction not found"));
    }

    @Test
    public void anulateTransaction_Success() throws Exception {
        AnulateTransactionRequest request = new AnulateTransactionRequest();
        request.setTransactionId("1");
        request.setCardId("1234567890123456");

        doNothing().when(transactionService).anulateTransaction(request.getTransactionId(), request.getCardId());

        mockMvc.perform(post("/transaction/anulation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"transactionId\":\"1\",\"cardId\":\"1234567890123456\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transacción anulada exitosamente"));
    }

    @Test
    public void anulateTransaction_Failure() throws Exception {
        AnulateTransactionRequest request = new AnulateTransactionRequest();
        request.setTransactionId("1");
        request.setCardId("1234567890123456");

        doThrow(new RuntimeException("Anulation failed")).when(transactionService).anulateTransaction(request.getTransactionId(), request.getCardId());

        mockMvc.perform(post("/transaction/anulation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"transactionId\":\"1\",\"cardId\":\"1234567890123456\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Anulation failed"));
    }
}
