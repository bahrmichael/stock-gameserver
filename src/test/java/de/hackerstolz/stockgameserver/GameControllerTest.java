package de.hackerstolz.stockgameserver;

import java.util.Optional;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hackerstolz.stockgameserver.model.Quote;
import de.hackerstolz.stockgameserver.web.GameController;
import de.hackerstolz.stockgameserver.service.QuoteService;

public class GameControllerTest {

    private final QuoteService service = mock(QuoteService.class);
    private final GameController sut = new GameController(service, null, null, null, null);

    @Test
    public void isAlive() {
        final ResponseEntity<Void> response = sut.isAlive();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void returnsStockDataFromService() {
        final String stockCode = "StockCode";
        when(service.getStockInfo(stockCode)).thenReturn(Optional.of(createStockInfo(stockCode)));

        final ResponseEntity<Quote> response = sut.getQuote(stockCode);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(stockCode, response.getBody().getSymbol());
    }

    @Test
    public void returns404_whenStockCantBeFound() {
        final String stockCode = "StockCode";
        when(service.getStockInfo(stockCode)).thenReturn(Optional.empty());

        final ResponseEntity<Quote> response = sut.getQuote(stockCode);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private Quote createStockInfo(final String stockCode) {
        final Quote stockInfo = new Quote();
        stockInfo.setSymbol(stockCode);
        return stockInfo;
    }
}
