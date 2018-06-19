package de.hackerstolz.stockgameserver.model;

public class Order {
    private String symbol;
    private Boolean isBuy;
    private Integer amount;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(final String symbol) {
        this.symbol = symbol;
    }

    public Boolean isBuy() {
        return isBuy;
    }

    public void setBuy(final Boolean buy) {
        isBuy = buy;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(final Integer amount) {
        this.amount = amount;
    }
}
