package de.hackerstolz.stockgameserver.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;

public class Transaction implements HasSymbol {

    @Id
    private String id;
    private String userId;
    private Double pricePerUnit;
    private Integer amount;
    private String symbol;
    private Boolean isBuy;
    private Instant instant;

    public Transaction() {
    }

    public Transaction(final String userId, final Double pricePerUnit, final Integer amount, final String symbol,
            final Boolean isBuy) {
        this.userId = userId;
        this.pricePerUnit = pricePerUnit;
        this.amount = amount;
        this.symbol = symbol;
        this.isBuy = isBuy;
        instant = Instant.now();
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(final Instant instant) {
        this.instant = instant;
    }

    public Double getValue() {
        return (isBuy ? -1 : 1) * pricePerUnit * amount;
    }

    public Boolean isBuy() {
        return isBuy;
    }

    public void setBuy(final Boolean buy) {
        isBuy = buy;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(final Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(final Integer amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(final String symbol) {
        this.symbol = symbol;
    }
}
