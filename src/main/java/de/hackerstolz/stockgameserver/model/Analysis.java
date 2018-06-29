package de.hackerstolz.stockgameserver.model;

import java.time.Instant;

public class Analysis {

    private String symbol;
    private int buy;
    private int hold;
    private int sell;
    private String source;
    private Instant instant;

    public Analysis() {
    }

    public Analysis(final String symbol, final int buy, final int hold, final int sell, final String source) {
        this.symbol = symbol;
        this.buy = buy;
        this.hold = hold;
        this.sell = sell;
        this.source = source;
        instant = Instant.now();
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(final Instant instant) {
        this.instant = instant;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(final String symbol) {
        this.symbol = symbol;
    }

    public int getBuy() {
        return buy;
    }

    public void setBuy(final int buy) {
        this.buy = buy;
    }

    public int getHold() {
        return hold;
    }

    public void setHold(final int hold) {
        this.hold = hold;
    }

    public int getSell() {
        return sell;
    }

    public void setSell(final int sell) {
        this.sell = sell;
    }

    @Override
    public String toString() {
        return "Analysis{" +
               "symbol='" + symbol + '\'' +
               ", buy=" + buy +
               ", hold=" + hold +
               ", sell=" + sell +
               ", source='" + source + '\'' +
               '}';
    }
}
