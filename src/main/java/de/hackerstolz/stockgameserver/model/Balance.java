package de.hackerstolz.stockgameserver.model;

public class Balance {

    private String userId;
    private double amount;

    public Balance() {
    }

    public Balance(final String userId, final double amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(final double amount) {
        this.amount = amount;
    }
}
