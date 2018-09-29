package de.hackerstolz.stockgameserver.web;

import de.hackerstolz.stockgameserver.model.Quote;
import de.hackerstolz.stockgameserver.service.QuoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/quote/")
public class QuoteResource {

    private final QuoteService quoteService;

    public QuoteResource(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping("{symbol}/")
    public ResponseEntity<Quote> getQuote(@PathVariable final String symbol) {
        return quoteService.getStockInfo(symbol).map(ResponseEntity::ok).orElseGet(this::notFound);
    }

    private ResponseEntity<Quote> notFound() {
        return ResponseEntity.notFound().build();
    }
}
