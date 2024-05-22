package com.bankinc.cardmanager.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankinc.cardmanager.model.entity.Card;
import com.bankinc.cardmanager.repository.CardRepository;
import com.bankinc.cardmanager.service.CardService;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class CardServiceImpl implements CardService {

    private static final Logger log = LoggerFactory.getLogger(CardServiceImpl.class);
    private static final int CARD_NUMBER_LENGTH = 16;

    @Autowired
    private CardRepository cardRepository;

    @Override
    @Transactional
    public Card createCard(String productId, String cardHolderName) throws Exception {
        log.info("Intentando crear tarjeta con productId: {} y cardHolderName: {}", productId, cardHolderName);
        if (productId.length() != 6) {
            log.error("Error en la longitud del productId: {}", productId);
            throw new IllegalArgumentException("El ID del producto debe tener 6 dígitos de longitud");
        }
        String cardId = generateCardNumber(productId);
        Card card = new Card();
        card.setCardId(cardId);
        card.setProductId(productId);
        card.setCardHolderName(cardHolderName);
        card.setExpirationDate(LocalDate.now().plusYears(3));
        card.setActive(false);
        card.setBlocked(false);
        card.setBalance(0);

        Card savedCard = cardRepository.save(card);
        log.info("Tarjeta creada con éxito ID: {}", savedCard.getCardId());
        return savedCard;
    }

    private String generateCardNumber(String productId) {
        SecureRandom random = new SecureRandom();
        StringBuilder cardNumber = new StringBuilder(productId);
        for (int i = 0; i < CARD_NUMBER_LENGTH - productId.length(); i++) {
            int digit = random.nextInt(10);
            cardNumber.append(digit);
        }
        log.info("Número de tarjeta generado: {}", cardNumber);
        return cardNumber.toString();
    }

    @Override
    @Transactional
    public void activateCard(String cardId) throws Exception {
        log.info("Activando tarjeta ID: {}", cardId);
        Optional<Card> optionalCard = cardRepository.findById(cardId);
        if (!optionalCard.isPresent()) {
            log.error("Tarjeta no encontrada ID: {}", cardId);
            throw new Exception("Tarjeta no encontrada");
        }
        Card card = optionalCard.get();
        if (card.isActive()) {
            log.warn("Intento de activar una tarjeta ya activa ID: {}", cardId);
            throw new Exception("La tarjeta ya está activa");
        }
        card.setActive(true);
        cardRepository.save(card);
        log.info("Tarjeta activada con éxito ID: {}", cardId);
    }

    @Override
    @Transactional
    public void blockCard(String cardId) throws Exception {
        log.info("Bloqueando tarjeta con ID: {}", cardId);
        Optional<Card> optionalCard = cardRepository.findById(cardId);
        if (!optionalCard.isPresent()) {
            log.error("Tarjeta no encontrada ID: {}", cardId);
            throw new Exception("Tarjeta no encontrada");
        }
        Card card = optionalCard.get();
        if (card.isBlocked()) {
            log.warn("Intento de bloquear una tarjeta ya bloqueada ID: {}", cardId);
            throw new Exception("La tarjeta ya está bloqueada");
        }
        card.setBlocked(true);
        cardRepository.save(card);
        log.info("Tarjeta bloqueada con éxito ID: {}", cardId);
    }

    @Override
    @Transactional
    public void rechargeBalance(String cardId, double balance) throws Exception {
        log.info("Recargando saldo de la tarjeta con ID: {} en cantidad: {}", cardId, balance);
        Optional<Card> optionalCard = cardRepository.findById(cardId);
        if (!optionalCard.isPresent()) {
            log.error("Tarjeta no encontrada ID: {}", cardId);
            throw new Exception("Tarjeta no encontrada");
        }
        Card card = optionalCard.get();
        card.setBalance(card.getBalance() + balance);
        cardRepository.save(card);
        log.info("Saldo recargado con éxito para la tarjeta ID: {}", cardId);
    }

    @Override
    @Transactional
    public double getBalance(String cardId) throws Exception {
        log.info("Consultando el saldo de la tarjeta con ID: {}", cardId);
        Optional<Card> optionalCard = cardRepository.findById(cardId);
        if (!optionalCard.isPresent()) {
            log.error("Tarjeta no encontrada ID: {}", cardId);
            throw new Exception("Tarjeta no encontrada");
        }
        Card card = optionalCard.get();
        log.info("Saldo recuperado para la tarjeta con ID: {}", cardId);
        return card.getBalance();
    }
    
    @Override
    public Card getCardByProductId(String cardId) throws Exception {
        log.info("Buscando tarjeta con producto ID: {}", cardId);
        Optional<Card> optionalCard = cardRepository.findById(cardId);
        if (!optionalCard.isPresent()) {
            log.error("Tarjeta no encontrada con producto ID: {}", cardId);
            throw new Exception("Tarjeta no encontrada");
        }
        Card card = optionalCard.get();
        log.info("Tarjeta encontrada con producto ID: {}", cardId);
        return card;
    }
}
