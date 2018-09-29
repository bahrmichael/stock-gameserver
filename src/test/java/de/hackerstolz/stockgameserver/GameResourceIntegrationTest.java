package de.hackerstolz.stockgameserver;

import de.hackerstolz.stockgameserver.model.Account;
import de.hackerstolz.stockgameserver.model.Balance;
import de.hackerstolz.stockgameserver.model.Order;
import de.hackerstolz.stockgameserver.model.Quote;
import de.hackerstolz.stockgameserver.model.Transaction;
import org.apache.commons.codec.binary.Base64;
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

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GameResourceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void isAlive() {
        final ResponseEntity<Void> response = restTemplate.exchange("/isalive/", HttpMethod.GET, emptyRequest(), Void.class);
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

    @Test
    public void getTransactions() {
        final String name = UUID.randomUUID().toString();
        final Account account = createAccount(name);

        final String symbol = "AAPL";
        final Order order = new Order();
        order.setAmount(1);
        order.setIsBuy(true);
        order.setSymbol(symbol);

        final Transaction transaction = submitOrder(account, order);
        assertEquals(symbol, transaction.getSymbol());

        final Transaction transaction2 = submitOrder(account, order);
        assertEquals(symbol, transaction2.getSymbol());

        final String symbol2 = "AMD";
        final Order order2 = new Order();
        order2.setAmount(1);
        order2.setIsBuy(true);
        order2.setSymbol(symbol2);

        final Transaction transaction3 = submitOrder(account, order2);
        assertEquals(symbol2, transaction3.getSymbol());

        final List<Transaction> transactions = loadTransactions(account);
        // 3 + 1 because there is one starter transaction
        assertEquals(3 + 1, transactions.size());

        final List<Transaction> transactionsForAAPL = loadTransactions(account, symbol);
        assertEquals(2, transactionsForAAPL.size());
    }

    private List<Transaction> loadTransactions(Account account, String symbol) {
        final ResponseEntity<Transaction[]> response = restTemplate.exchange("/api/v1/transactions/" + symbol + "/",
                HttpMethod.GET, authorizedRequest(account.getUserId(), account.getSecret()),
                Transaction[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        return Arrays.asList(response.getBody());
    }

    private List<Transaction> loadTransactions(Account account) {
        final ResponseEntity<Transaction[]> response = restTemplate.exchange("/api/v1/transactions/",
                HttpMethod.GET, authorizedRequest(account.getUserId(), account.getSecret()),
                Transaction[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        return Arrays.asList(response.getBody());
    }

    private Balance getBalance(final Account account) {
        final ResponseEntity<Balance> response = restTemplate.exchange("/api/v1/balance/",
                                                                       HttpMethod.GET, authorizedRequest(account.getUserId(), account.getSecret()),
                                                                       Balance.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    private Quote getQuote(final String symbol) {
        final ResponseEntity<Quote> response = restTemplate.exchange("/api/v1/quote/" + symbol + "/", HttpMethod.GET, emptyRequest(), Quote.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    private Transaction submitOrder(final Account account, final Order order) {
        final HttpHeaders headers = buildAuthHeader(account.getUserId(), account.getSecret());
        final HttpEntity<Order> orderRequest = new HttpEntity<>(order, headers);
        final ResponseEntity<Transaction> orderResponse = restTemplate.exchange("/api/v1/order/",
                                                                                HttpMethod.POST, orderRequest, Transaction.class);
        assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
        return orderResponse.getBody();
    }

    private Account createAccount(final String name) {
        final ResponseEntity<Account> response = restTemplate.exchange("/api/v1/account/" + name + "/", HttpMethod.POST, emptyRequest(), Account.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    private static HttpEntity<Object> emptyRequest() {
        final HttpHeaders headers = new HttpHeaders();
        return new HttpEntity<>(null, headers);
    }

    private static HttpEntity<Object> authorizedRequest(String userId, final String secret) {
        final HttpHeaders headers = buildAuthHeader(userId, secret);
        return new HttpEntity<>(null, headers);
    }

    private static HttpHeaders buildAuthHeader(String userId, final String secret) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", getBasicAuth(userId, secret));
        return headers;
    }

    public static String getBasicAuth(final String clientId, final String clientSecret) {
        final String auth = clientId + ":" + clientSecret;
        final byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("UTF-8")));
        return "Basic " + new String(encodedAuth);
    }
}
