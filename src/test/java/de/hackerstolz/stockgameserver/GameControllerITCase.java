package de.hackerstolz.stockgameserver;

import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.assertEquals;

import de.hackerstolz.stockgameserver.model.Account;
import de.hackerstolz.stockgameserver.model.Balance;
import de.hackerstolz.stockgameserver.model.Order;
import de.hackerstolz.stockgameserver.model.Quote;
import de.hackerstolz.stockgameserver.model.Transaction;

@Ignore // todo: add embedded mongodb for tests
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GameControllerITCase {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void isAlive() {
        final ResponseEntity<Void> response = restTemplate.exchange("/api/isalive", HttpMethod.GET, emptyRequest(), Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void quote() {
        final String symbol = "aapl";
        final Quote quote = getQuote(symbol);
        assertEquals(symbol.toUpperCase(), quote.getSymbol());
    }

    @Test
    public void createAccount() {
        final String name = UUID.randomUUID().toString();
        final Account account = createAccount(name);
        assertEquals(name, account.getName());

        final Balance balance = getBalance(account);
        assertEquals(10_000, balance.getAmount(), 0.1);
    }

    @Test
    public void balance_changes() {
        final String name = UUID.randomUUID().toString();
        final Account account = createAccount(name);

        final Balance balance = getBalance(account);
        assertEquals(10_000.0, balance.getAmount(), 0.1);

        final String symbol = "AAPL";
        final Quote quote = getQuote(symbol);

        final Order order = new Order();
        order.setAmount(1);
        order.setIsBuy(true);
        order.setSymbol(symbol);

        final Transaction transaction = submitOrder(account, order);
        assertEquals(symbol, transaction.getSymbol());

        final Balance newBalance = getBalance(account);
        assertEquals(balance.getAmount() - quote.getLatestPrice(), newBalance.getAmount(), 0.1);
    }

    private Balance getBalance(final Account account) {
        final ResponseEntity<Balance> response = restTemplate.exchange("/api/balance/" + account.getUserId(),
                                                                       HttpMethod.GET, authorizedRequest(account.getSecret()),
                                                                       Balance.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    private Quote getQuote(final String symbol) {
        final ResponseEntity<Quote> response = restTemplate.exchange("/api/quote/" + symbol, HttpMethod.GET, emptyRequest(), Quote.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    private Transaction submitOrder(final Account account, final Order order) {
        final HttpEntity<Order> orderRequest = new HttpEntity<>(order, buildAuthHeader(account.getSecret()));
        final ResponseEntity<Transaction> orderResponse = restTemplate.exchange("/api/order/" + account.getUserId(),
                                                                                HttpMethod.POST, orderRequest, Transaction.class);
        assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
        return orderResponse.getBody();
    }

    private Account createAccount(final String name) {
        final ResponseEntity<Account> response = restTemplate.exchange("/api/account/" + name, HttpMethod.POST, emptyRequest(), Account.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    private static HttpEntity<Object> emptyRequest() {
        final HttpHeaders headers = new HttpHeaders();
        return new HttpEntity<Object>(null, headers);
    }

    private static HttpEntity<Object> authorizedRequest(final String secret) {
        final HttpHeaders headers = buildAuthHeader(secret);
        return new HttpEntity<Object>(null, headers);
    }

    private static HttpHeaders buildAuthHeader(final String secret) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Secret " + secret);
        return headers;
    }
}
