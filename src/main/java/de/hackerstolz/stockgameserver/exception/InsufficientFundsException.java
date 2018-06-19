package de.hackerstolz.stockgameserver.exception;

public class InsufficientFundsException extends Throwable {
    private final Double requiredAmount;

    public InsufficientFundsException(final Double requiredAmount) {
        this.requiredAmount = requiredAmount;
    }

    public Double getRequiredAmount() {
        return requiredAmount;
    }
}
