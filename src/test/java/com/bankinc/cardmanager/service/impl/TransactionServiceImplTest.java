package com.bankinc.cardmanager.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bankinc.cardmanager.model.entity.Card;
import com.bankinc.cardmanager.model.entity.Transaction;
import com.bankinc.cardmanager.repository.CardRepository;
import com.bankinc.cardmanager.repository.TransactionRepository;

public class TransactionServiceImplTest {
	
	 	@Mock
	    private TransactionRepository transactionRepository;
	 	
	    @Mock
	    private CardRepository cardRepository;

	    @InjectMocks
	    private TransactionServiceImpl transactionServiceImpl;

	    @BeforeEach
	    public void setup() {
	        MockitoAnnotations.openMocks(this);
	    }

	    @Test
	    public void testGetTransactionById_TransactionNotFound() {
	        Long transactionId = 1L;
	        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

	        Exception exception = assertThrows(Exception.class, () -> {
	            transactionServiceImpl.getTransactionById(transactionId);
	        });

	        assertEquals("Transacción no encontrada", exception.getMessage());
	    }

	    @Test
	    public void testGetTransactionById_Success() throws Exception {
	        Long transactionId = 1L;
	        Transaction transaction = new Transaction();
	        transaction.setTransaction_id(transactionId);

	        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

	        Transaction foundTransaction = transactionServiceImpl.getTransactionById(transactionId);

	        assertEquals(transactionId, foundTransaction.getTransaction_id());
	    }
	    
	    @Test
	    public void testPurchase_CardNotFound() {
	        String cardId = "1234567890123456";
	        double price = 100.0;

	        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

	        Exception exception = assertThrows(Exception.class, () -> {
	            transactionServiceImpl.purchase(cardId, price);
	        });

	        assertEquals("Tarjeta no encontrada", exception.getMessage());
	    }

	    @Test
	    public void testPurchase_CardNotActive() {
	        String cardId = "1234567890123456";
	        double price = 100.0;
	        Card card = new Card();
	        card.setActive(false);

	        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

	        Exception exception = assertThrows(Exception.class, () -> {
	            transactionServiceImpl.purchase(cardId, price);
	        });

	        assertEquals("La tarjeta no está activada", exception.getMessage());
	    }

	    @Test
	    public void testPurchase_CardExpired() {
	        String cardId = "1234567890123456";
	        double price = 100.0;
	        Card card = new Card();
	        card.setActive(true);
	        card.setExpirationDate(LocalDate.now().minusDays(1));

	        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

	        Exception exception = assertThrows(Exception.class, () -> {
	            transactionServiceImpl.purchase(cardId, price);
	        });

	        assertEquals("La tarjeta ha caducado", exception.getMessage());
	    }

	    @Test
	    public void testPurchase_CardBlocked() {
	        String cardId = "1234567890123456";
	        double price = 100.0;
	        Card card = new Card();
	        card.setActive(true);
	        card.setExpirationDate(LocalDate.now().plusDays(1));
	        card.setBlocked(true);

	        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

	        Exception exception = assertThrows(Exception.class, () -> {
	            transactionServiceImpl.purchase(cardId, price);
	        });

	        assertEquals("La tarjeta está bloqueada", exception.getMessage());
	    }

	    @Test
	    public void testPurchase_InsufficientBalance() {
	        String cardId = "1234567890123456";
	        double price = 100.0;
	        Card card = new Card();
	        card.setActive(true);
	        card.setExpirationDate(LocalDate.now().plusDays(1));
	        card.setBlocked(false);
	        card.setBalance(50.0);

	        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

	        Exception exception = assertThrows(Exception.class, () -> {
	            transactionServiceImpl.purchase(cardId, price);
	        });

	        assertEquals("Saldo insuficiente", exception.getMessage());
	    }

	    @Test
	    public void testPurchase_Success() throws Exception {
	        String cardId = "1234567890123456";
	        double price = 100.0;
	        Card card = new Card();
	        card.setActive(true);
	        card.setExpirationDate(LocalDate.now().plusDays(1));
	        card.setBlocked(false);
	        card.setBalance(200.0);

	        Transaction transaction = new Transaction();
	        transaction.setCard(card);
	        transaction.setAmount(price);
	        transaction.setTransactionDate(LocalDateTime.now());
	        transaction.setStatus("COMPLETED");

	        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
	        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

	        Transaction result = transactionServiceImpl.purchase(cardId, price);

	        verify(cardRepository).save(card);
	        assertEquals("COMPLETED", result.getStatus());
	        assertEquals(100.0, card.getBalance());
	    }
	    
	    @Test
	    public void testAnulateTransaction_NotFound() {
	        String transactionId = "1";
	        String cardId = "1234567890123456";

	        when(transactionRepository.findById(Long.parseLong(transactionId))).thenReturn(Optional.empty());

	        Exception exception = assertThrows(Exception.class, () -> {
	            transactionServiceImpl.anulateTransaction(transactionId, cardId);
	        });

	        assertEquals("Transacción no encontrada", exception.getMessage());
	    }

	    @Test
	    public void testAnulateTransaction_AlreadyAnulled() {
	        String transactionId = "1";
	        String cardId = "1234567890123456";
	        Transaction transaction = new Transaction();
	        transaction.setAnulled(true);

	        when(transactionRepository.findById(Long.parseLong(transactionId))).thenReturn(Optional.of(transaction));

	        Exception exception = assertThrows(Exception.class, () -> {
	            transactionServiceImpl.anulateTransaction(transactionId, cardId);
	        });

	        assertEquals("Transacción ya anulada", exception.getMessage());
	    }

	    @Test
	    public void testAnulateTransaction_OlderThan24Hours() {
	        String transactionId = "1";
	        String cardId = "1234567890123456";
	        Transaction transaction = new Transaction();
	        transaction.setTransactionDate(LocalDateTime.now().minusHours(25));

	        when(transactionRepository.findById(Long.parseLong(transactionId))).thenReturn(Optional.of(transaction));

	        Exception exception = assertThrows(Exception.class, () -> {
	            transactionServiceImpl.anulateTransaction(transactionId, cardId);
	        });

	        assertEquals("La transacción tiene más de 24 horas", exception.getMessage());
	    }

	    @Test
	    public void testAnulateTransaction_CardMismatch() {
	        String transactionId = "1";
	        String cardId = "1234567890123456";
	        Transaction transaction = new Transaction();
	        transaction.setTransactionDate(LocalDateTime.now().minusHours(1));
	        Card card = new Card();
	        card.setCardId("different_card_id");
	        transaction.setCard(card);

	        when(transactionRepository.findById(Long.parseLong(transactionId))).thenReturn(Optional.of(transaction));

	        Exception exception = assertThrows(Exception.class, () -> {
	            transactionServiceImpl.anulateTransaction(transactionId, cardId);
	        });

	        assertEquals("La transacción no pertenece a la tarjeta especificada", exception.getMessage());
	    }

	    @Test
	    public void testAnulateTransaction_Success() throws Exception {
	        String transactionId = "1";
	        String cardId = "1234567890123456";
	        Transaction transaction = new Transaction();
	        transaction.setTransactionDate(LocalDateTime.now().minusHours(1));
	        transaction.setAmount(100.0);
	        transaction.setAnulled(false);
	        Card card = new Card();
	        card.setCardId(cardId);
	        card.setBalance(200.0);
	        transaction.setCard(card);

	        when(transactionRepository.findById(Long.parseLong(transactionId))).thenReturn(Optional.of(transaction));
	        when(cardRepository.save(card)).thenReturn(card);
	        when(transactionRepository.save(transaction)).thenReturn(transaction);

	        transactionServiceImpl.anulateTransaction(transactionId, cardId);

	        verify(transactionRepository).save(transaction);
	        verify(cardRepository).save(card);
	        assertTrue(transaction.isAnulled());
	        assertEquals("ANULLED", transaction.getStatus());
	        assertEquals(300.0, card.getBalance());
	    }


}
