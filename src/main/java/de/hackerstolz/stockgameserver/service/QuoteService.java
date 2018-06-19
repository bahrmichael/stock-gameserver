package de.hackerstolz.stockgameserver.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.hackerstolz.stockgameserver.model.Quote;

@Service
public class QuoteService {

    private final RestTemplate restTemplate;

    public QuoteService(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<Quote> getStockInfo(final String symbol) {
        final String url = "https://api.iextrading.com/1.0/stock/" + symbol.toLowerCase() + "/quote";
        return Optional.ofNullable(restTemplate.getForObject(url, Quote.class));
    }
}
