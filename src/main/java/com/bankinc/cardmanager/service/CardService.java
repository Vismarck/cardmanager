package com.bankinc.cardmanager.service;

import org.springframework.stereotype.Service;

import com.bankinc.cardmanager.model.entity.Card;

@Service
public interface CardService {
	
	Card createCard(String productId, String cardHolderName) throws Exception ;
	
	void activateCard(String cardId) throws Exception;
	
	void blockCard(String cardId) throws Exception;
	
	void rechargeBalance(String cardId, double balance) throws Exception;
	
	double getBalance(String cardId) throws Exception;
	
	Card getCardByProductId(String cardId) throws Exception;

}
