package de.hackerstolz.stockgameserver.exception;

public class InsufficientSharesException extends Throwable {
    private final int availableShares;

    public InsufficientSharesException(final int availableShares) {
        this.availableShares = availableShares;
    }

    public int getAvailableShares() {
        return availableShares;
    }
}
