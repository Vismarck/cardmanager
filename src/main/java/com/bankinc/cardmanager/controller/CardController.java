package com.bankinc.cardmanager.controller;

import com.bankinc.cardmanager.model.entity.Card;
import com.bankinc.cardmanager.model.request.ActivateCardRequest;
import com.bankinc.cardmanager.model.request.RechargeBalanceRequest;
import com.bankinc.cardmanager.service.CardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/card")
public class CardController {
	
	private static final Logger log = LoggerFactory.getLogger(CardController.class);

    @Autowired
    private CardService cardService;
    
    
    @GetMapping("/{productId}/number")
    public ResponseEntity<String> generateCardNumber(@PathVariable String productId, @RequestParam String cardHolderName) {
        log.info("Generando número de tarjeta productId: {} y cardholdername: {}", productId, cardHolderName);
        try {
            Card newCard = cardService.createCard(productId, cardHolderName);
            log.info("Id de tarjeta generada: {}", newCard.getCardId());
            return ResponseEntity.ok(newCard.getCardId());
        } catch (Exception e) {
            log.error("Error al generar el número de tarjeta: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/enroll")
    public ResponseEntity<String> activateCard(@RequestBody ActivateCardRequest request) {
        log.info("Activar tarjeta ID: {}", request.getCardId());
        try {
            cardService.activateCard(request.getCardId());
            log.info("Tarjeta activada exitosamente");
            return ResponseEntity.ok("Tarjeta activada exitosamente");
        } catch (Exception e) {
            log.error("Error al activar la tarjeta: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<String> blockCard(@PathVariable String cardId) {
        log.info("Tarjeta de bloqueo ID: {}", cardId);
        try {
            cardService.blockCard(cardId);
            log.info("Tarjeta bloqueada exitosamente");
            return ResponseEntity.ok("Tarjeta bloqueada exitosamente");
        } catch (Exception e) {
            log.error("Error al bloquear la tarjeta: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/balance")
    public ResponseEntity<String> rechargeBalance(@RequestBody RechargeBalanceRequest request) {
        log.info("Recarga de saldo para tarjeta ID: {} con cantidad: {}", request.getCardId(), request.getBalance());
        try {
            cardService.rechargeBalance(request.getCardId(), request.getBalance());
            log.info("Saldo recargado exitosamente");
            return ResponseEntity.ok("Saldo recargado exitosamente");
        } catch (Exception e) {
            log.error("Error al recargar saldo: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/balance/{cardId}")
    public ResponseEntity<?> getBalance(@PathVariable String cardId) {
        log.info("Obteniendo saldo para la tarjeta ID: {}", cardId);
        try {
            double balance = cardService.getBalance(cardId);
            log.info("Saldo recuperado: {}", balance);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            log.error("Error al obtener saldo: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
