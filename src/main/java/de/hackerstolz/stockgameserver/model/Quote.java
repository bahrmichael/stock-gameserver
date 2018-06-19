package de.hackerstolz.stockgameserver.model;


public class Quote implements HasSymbol {
    private String symbol;
    private Double latestPrice;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(final String symbol) {
        this.symbol = symbol;
    }

    public Double getLatestPrice() {
        return latestPrice;
    }

    public void setLatestPrice(final Double latestPrice) {
        this.latestPrice = latestPrice;
    }
}
