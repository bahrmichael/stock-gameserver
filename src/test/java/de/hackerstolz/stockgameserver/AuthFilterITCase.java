package de.hackerstolz.stockgameserver;

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

@Ignore // todo: add embedded mongodb for tests
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthFilterITCase {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void badUrl_returns404() {
        final ResponseEntity<Void> orderResponse = restTemplate.exchange("/api/transactions/", HttpMethod.GET, null, Void.class);
        assertEquals(HttpStatus.NOT_FOUND, orderResponse.getStatusCode());
    }

    @Test
    public void nonExistingUser_returns404() {
        final ResponseEntity<Void> orderResponse = restTemplate.exchange("/api/transactions/aUser", HttpMethod.GET, null, Void.class);
        assertEquals(HttpStatus.NOT_FOUND, orderResponse.getStatusCode());
    }

    @Test
    public void badAuthentication_returns404() {
        final Account account = createAccount("Test");
        final ResponseEntity<Void> orderResponse = restTemplate.exchange("/api/transactions/" + account.getUserId(), HttpMethod.GET, authorizedRequest(account.getSecret() + "wrong"), Void.class);
        assertEquals(HttpStatus.UNAUTHORIZED, orderResponse.getStatusCode());
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
