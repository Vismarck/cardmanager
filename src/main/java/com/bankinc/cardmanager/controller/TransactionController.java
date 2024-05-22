package com.bankinc.cardmanager.controller;

import com.bankinc.cardmanager.model.entity.Transaction;
import com.bankinc.cardmanager.model.request.AnulateTransactionRequest;
import com.bankinc.cardmanager.model.request.PurchaseRequest;
import com.bankinc.cardmanager.service.TransactionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
	
	private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/purchase")
    public ResponseEntity<?> purchase(@RequestBody PurchaseRequest request) {
        log.info("Iniciando compra cardId: {}, cantidad: {}", request.getCardId(), request.getPrice());
        try {
            Transaction transaction = transactionService.purchase(request.getCardId(), request.getPrice());
            log.info("Compra exitosa transactionId: {}", transaction.getTransaction_id());
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            log.error("La compra falló cardId: {}: {}", request.getCardId(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransaction(@PathVariable Long transactionId) {
        log.info("Recuperando transacción ID: {}", transactionId);
        try {
            Transaction transaction = transactionService.getTransactionById(transactionId);
            log.info("Transacción recuperada exitosamente para el ID de transacción: {}", transactionId);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            log.error("No se pudo recuperar la transacción ID: {}: {}", transactionId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/anulation")
    public ResponseEntity<String> anulateTransaction(@RequestBody AnulateTransactionRequest request) {
        log.info("Iniciando anulación de transacción transactionId: {}, cardId: {}", request.getTransactionId(), request.getCardId());
        try {
            transactionService.anulateTransaction(request.getTransactionId(), request.getCardId());
            log.info("Transacción anulada exitosamente transactionId: {}", request.getTransactionId());
            return ResponseEntity.ok("Transacción anulada exitosamente");
        } catch (Exception e) {
            log.error("Anulación fallida transactionId: {}: {}", request.getTransactionId(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
