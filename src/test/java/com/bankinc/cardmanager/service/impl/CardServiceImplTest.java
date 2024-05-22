package com.bankinc.cardmanager.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.bankinc.cardmanager.BaseTest;
import com.bankinc.cardmanager.model.entity.Card;
import com.bankinc.cardmanager.repository.CardRepository;

public class CardServiceImplTest extends BaseTest{
	
	 	@MockBean
	    private CardRepository cardRepository;

	    @InjectMocks
	    private CardServiceImpl cardServiceImpl;

	    @BeforeEach
	    public void setup() {
	        MockitoAnnotations.openMocks(this);
	    }

	    @Test
	    public void testCreateCard_ValidProductId() throws Exception {
	        String validProductId = "123456";
	        String cardHolderName = "Arnold Wis";

	        Card card = new Card();
	        String generatedCardId = validProductId + "7890123456";
	        card.setCardId(generatedCardId);
	        card.setProductId(validProductId);
	        card.setCardHolderName(cardHolderName);
	        card.setBalance(0);
	        card.setActive(false);
	        card.setBlocked(false);
	        card.setExpirationDate(LocalDate.now().plusYears(3));

	        when(cardRepository.save(any(Card.class))).thenReturn(card);

	        Card createdCard = cardServiceImpl.createCard(validProductId, cardHolderName);

	        assertEquals(16, createdCard.getCardId().length());
	        assertTrue(createdCard.getCardId().startsWith(validProductId));
	    }

	    @Test
	    public void testCreateCard_InvalidProductIdLength() {
	        String invalidProductId = "12345"; // Length is not 6
	        String cardHolderName = "Arnold Wis";

	        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
	            cardServiceImpl.createCard(invalidProductId, cardHolderName);
	        });

	        assertEquals("El ID del producto debe tener 6 dígitos de longitud", exception.getMessage());
	    }
	    
	    @Test
	    public void testActivateCard_CardNotFound() {
	        String cardId = "1234567890123456";

	        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

	        Exception exception = assertThrows(Exception.class, () -> {
	            cardServiceImpl.activateCard(cardId);
	        });

	        assertEquals("Tarjeta no encontrada", exception.getMessage());
	    }

	    @Test
	    public void testActivateCard_AlreadyActive() {
	        String cardId = "1234567890123456";
	        Card card = new Card();
	        card.setCardId(cardId);
	        card.setActive(true);

	        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

	        Exception exception = assertThrows(Exception.class, () -> {
	            cardServiceImpl.activateCard(cardId);
	        });

	        assertEquals("La tarjeta ya está activa", exception.getMessage());
	    }

	    @Test
	    public void testActivateCard_Success() throws Exception {
	        String cardId = "1234567890123456";
	        Card card = new Card();
	        card.setCardId(cardId);
	        card.setActive(false);

	        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
	        when(cardRepository.save(any(Card.class))).thenReturn(card);

	        cardServiceImpl.activateCard(cardId);

	        verify(cardRepository).save(card);
	        assertTrue(card.isActive());
	    }
	    
	    @Test
	    public void testBlockCard_CardNotFound() {
	        String cardId = "1234567890123456";

	        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

	        Exception exception = assertThrows(Exception.class, () -> {
	            cardServiceImpl.blockCard(cardId);
	        });

	        assertEquals("Tarjeta no encontrada", exception.getMessage());
	    }

	    @Test
	    public void testBlockCard_AlreadyBlocked() {
	        String cardId = "1234567890123456";
	        Card card = new Card();
	        card.setCardId(cardId);
	        card.setBlocked(true);

	        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

	        Exception exception = assertThrows(Exception.class, () -> {
	            cardServiceImpl.blockCard(cardId);
	        });

	        assertEquals("La tarjeta ya está bloqueada", exception.getMessage());
	    }

	    @Test
	    public void testBlockCard_Success() throws Exception {
	        String cardId = "1234567890123456";
	        Card card = new Card();
	        card.setCardId(cardId);
	        card.setBlocked(false);

	        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
	        when(cardRepository.save(any(Card.class))).thenReturn(card);

	        cardServiceImpl.blockCard(cardId);

	        verify(cardRepository).save(card);
	        assertTrue(card.isBlocked());
	    }
	    
	    @Test
	    public void testRechargeBalance_CardNotFound() {
	        String cardId = "1234567890123456";
	        double balance = 100.0;

	        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

	        Exception exception = assertThrows(Exception.class, () -> {
	            cardServiceImpl.rechargeBalance(cardId, balance);
	        });

	        assertEquals("Tarjeta no encontrada", exception.getMessage());
	    }

	    @Test
	    public void testRechargeBalance_Success() throws Exception {
	        String cardId = "1234567890123456";
	        double balance = 100.0;
	        Card card = new Card();
	        card.setCardId(cardId);
	        card.setBalance(0);

	        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
	        when(cardRepository.save(any(Card.class))).thenReturn(card);

	        cardServiceImpl.rechargeBalance(cardId, balance);

	        verify(cardRepository).save(card);
	        assertEquals(100.0, card.getBalance());
	    }
	    
	    @Test
	    public void testGetBalance_CardNotFound() {
	        String cardId = "1234567890123456";

	        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

	        Exception exception = assertThrows(Exception.class, () -> {
	            cardServiceImpl.getBalance(cardId);
	        });

	        assertEquals("Tarjeta no encontrada", exception.getMessage());
	    }

	    @Test
	    public void testGetBalance_Success() throws Exception {
	        String cardId = "1234567890123456";
	        Card card = new Card();
	        card.setCardId(cardId);
	        card.setBalance(5000);

	        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

	        double balance = cardServiceImpl.getBalance(cardId);

	        assertEquals(5000, balance);
	    }
	    
	    @Test
	    public void testGetCardByProductId_CardNotFound() {
	        String cardId = "1234567890123456";

	        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

	        Exception exception = assertThrows(Exception.class, () -> {
	            cardServiceImpl.getCardByProductId(cardId);
	        });

	        assertEquals("Tarjeta no encontrada", exception.getMessage());
	    }

	    @Test
	    public void testGetCardByProductId_Success() throws Exception {
	        String cardId = "1234567890123456";
	        Card card = new Card();
	        card.setCardId(cardId);
	        card.setProductId("123456");
	        card.setCardHolderName("Arnold Wis");

	        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

	        Card retrievedCard = cardServiceImpl.getCardByProductId(cardId);

	        assertEquals(cardId, retrievedCard.getCardId());
	        assertEquals("123456", retrievedCard.getProductId());
	        assertEquals("Arnold Wis", retrievedCard.getCardHolderName());
	    }

}
