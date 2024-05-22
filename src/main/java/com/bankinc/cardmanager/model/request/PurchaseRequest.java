package com.bankinc.cardmanager.model.request;

public class PurchaseRequest {
	
	private String cardId;
    private double price;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
