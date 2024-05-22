package com.bankinc.cardmanager.service.impl;

import com.bankinc.cardmanager.model.entity.Card;
import com.bankinc.cardmanager.model.entity.Transaction;
import com.bankinc.cardmanager.repository.CardRepository;
import com.bankinc.cardmanager.repository.TransactionRepository;
import com.bankinc.cardmanager.service.TransactionService;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {
	
	private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);
	
    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    public Transaction getTransactionById(Long transactionId) throws Exception {
    	log.info("Buscando transacción ID: {}", transactionId);
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        if (!transaction.isPresent()) {
        	log.error("Transacción no encontrada ID: {}", transactionId);
            throw new Exception("Transacción no encontrada");
        }
        return transaction.get();
    }
    
    @Transactional
    public Transaction purchase(String cardId, double price) throws Exception {
    	log.info("Iniciando compra con tarjeta ID: {}, por importe: {}", cardId, price);
        Optional<Card> optionalCard = cardRepository.findById(cardId);
        if (!optionalCard.isPresent()) {
        	log.error("No se encontró tarjeta ID: {}", cardId);
            throw new Exception("Tarjeta no encontrada");
        }

        Card card = optionalCard.get();
        if (!card.isActive()) {
        	log.error("La tarjeta no está activada ID: {}", cardId);
            throw new Exception("La tarjeta no está activada");
        }

        if (card.getExpirationDate().isBefore(LocalDate.now())) {
        	log.error("La tarjeta ha caducado ID: {}", cardId);
            throw new Exception("La tarjeta ha caducado");
        }

        if (card.isBlocked()) {
        	log.error("La tarjeta está bloqueada ID: {}", cardId);
            throw new Exception("La tarjeta está bloqueada");
        }

        if (card.getBalance() < price) {
        	log.error("Saldo insuficiente ID: {}", cardId);
            throw new Exception("Saldo insuficiente");
        }

        card.setBalance(card.getBalance() - price);
        cardRepository.save(card);

        Transaction transaction = new Transaction();
        transaction.setCard(card);
        transaction.setAmount(price);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus("COMPLETED");

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transacción completada con éxito ID de transacción: {}", savedTransaction.getTransaction_id());
        return savedTransaction;
    }

    @Transactional
    public void anulateTransaction(String transactionId, String cardId) throws Exception {
    	Long transactionId_ = Long.parseLong(transactionId);
        Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId_);
        if (!optionalTransaction.isPresent()) {
        	log.error("Transacción no encontrada ID de transacción: {}", transactionId);
            throw new Exception("Transacción no encontrada");
        }

        Transaction transaction = optionalTransaction.get();

        if (transaction.isAnulled()) {
        	log.error("Intento de anular una transacción ya anulada ID de transacción: {}", transactionId);
            throw new Exception("Transacción ya anulada");
        }

        LocalDateTime now = LocalDateTime.now();
        if (transaction.getTransactionDate().isBefore(now.minusHours(24))) {
        	log.error("La transacción tiene más de 24 hora ID de transacción: {}", transactionId);
            throw new Exception("La transacción tiene más de 24 horas");
        }

        if (!transaction.getCard().getCardId().equals(cardId)) {
        	log.error("La transacción no pertenece a la tarjeta especificada ID de transacción: {}", transactionId);
            throw new Exception("La transacción no pertenece a la tarjeta especificada");
        }

        transaction.setAnulled(true);
        transaction.setStatus("ANULLED");

        Card card = transaction.getCard();
        double newBalance = card.getBalance() + transaction.getAmount();
        card.setBalance(newBalance);

        transactionRepository.save(transaction);
        cardRepository.save(card);
        log.info("Transacción anulada con éxito, ID: {}", transaction.getTransaction_id());
    }
}
