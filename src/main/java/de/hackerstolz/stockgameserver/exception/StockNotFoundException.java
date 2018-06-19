package de.hackerstolz.stockgameserver.exception;

public class StockNotFoundException extends Throwable {
    private final String symbol;

    public StockNotFoundException(final String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
