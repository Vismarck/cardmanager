package com.bankinc.cardmanager.controller;

import com.bankinc.cardmanager.model.entity.Card;
import com.bankinc.cardmanager.model.request.ActivateCardRequest;
import com.bankinc.cardmanager.model.request.RechargeBalanceRequest;
import com.bankinc.cardmanager.service.CardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
public class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @Test
    public void generateCardNumber_Success() throws Exception {
        String productId = "123456";
        String cardHolderName = "John Doe";
        Card card = new Card();
        card.setCardId("1234567890123456");

        when(cardService.createCard(productId, cardHolderName)).thenReturn(card);

        mockMvc.perform(get("/card/{productId}/number", productId)
                        .param("cardHolderName", cardHolderName))
                .andExpect(status().isOk())
                .andExpect(content().string("1234567890123456"));
    }

    @Test
    public void generateCardNumber_Failure() throws Exception {
        String productId = "123456";
        String cardHolderName = "John Doe";
        when(cardService.createCard(productId, cardHolderName)).thenThrow(new RuntimeException("Error creating card"));

        mockMvc.perform(get("/card/{productId}/number", productId)
                        .param("cardHolderName", cardHolderName))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error creating card"));
    }

    @Test
    public void activateCard_Success() throws Exception {
        ActivateCardRequest request = new ActivateCardRequest();
        request.setCardId("1234567890123456");

        doNothing().when(cardService).activateCard(anyString());

        mockMvc.perform(post("/card/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cardId\":\"1234567890123456\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Tarjeta activada exitosamente"));
    }

    @Test
    public void activateCard_Failure() throws Exception {
        ActivateCardRequest request = new ActivateCardRequest();
        request.setCardId("1234567890123456");

        doThrow(new RuntimeException("Error al activar la tarjeta")).when(cardService).activateCard(anyString());

        mockMvc.perform(post("/card/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cardId\":\"1234567890123456\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al activar la tarjeta"));
    }

    @Test
    public void blockCard_Success() throws Exception {
        String cardId = "1234567890123456";

        doNothing().when(cardService).blockCard(cardId);

        mockMvc.perform(delete("/card/{cardId}", cardId))
                .andExpect(status().isOk())
                .andExpect(content().string("Tarjeta bloqueada exitosamente"));
    }

    @Test
    public void blockCard_Failure() throws Exception {
        String cardId = "1234567890123456";

        doThrow(new RuntimeException("Error blocking card")).when(cardService).blockCard(cardId);

        mockMvc.perform(delete("/card/{cardId}", cardId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error blocking card"));
    }

    @Test
    public void rechargeBalance_Success() throws Exception {
        RechargeBalanceRequest request = new RechargeBalanceRequest();
        request.setCardId("1234567890123456");
        request.setBalance(100.0);

        doNothing().when(cardService).rechargeBalance(anyString(), anyDouble());

        mockMvc.perform(post("/card/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cardId\":\"1234567890123456\",\"balance\":100.0}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Saldo recargado exitosamente"));
    }

    @Test
    public void rechargeBalance_Failure() throws Exception {
        RechargeBalanceRequest request = new RechargeBalanceRequest();
        request.setCardId("1234567890123456");
        request.setBalance(100.0);

        doThrow(new RuntimeException("Error al recargar saldo")).when(cardService).rechargeBalance(anyString(), anyDouble());

        mockMvc.perform(post("/card/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cardId\":\"1234567890123456\",\"balance\":100.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al recargar saldo"));
    }

    @Test
    public void getBalance_Success() throws Exception {
        String cardId = "1234567890123456";
        double balance = 100.0;

        when(cardService.getBalance(cardId)).thenReturn(balance);

        mockMvc.perform(get("/card/balance/{cardId}", cardId))
                .andExpect(status().isOk())
                .andExpect(content().string("100.0"));
    }

    @Test
    public void getBalance_Failure() throws Exception {
        String cardId = "1234567890123456";

        when(cardService.getBalance(cardId)).thenThrow(new RuntimeException("Error retrieving balance"));

        mockMvc.perform(get("/card/balance/{cardId}", cardId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error retrieving balance"));
    }
}
