package com.bankinc.cardmanager.service;


import com.bankinc.cardmanager.model.entity.Transaction;

public interface TransactionService {
	
	Transaction purchase(String cardId, double price) throws Exception;
	
	Transaction getTransactionById(Long transactionId) throws Exception;
	
	void anulateTransaction(String transactionId, String cardId) throws Exception;

}
