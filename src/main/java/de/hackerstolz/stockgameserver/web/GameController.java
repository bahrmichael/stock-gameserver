package de.hackerstolz.stockgameserver.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hackerstolz.stockgameserver.exception.InsufficientFundsException;
import de.hackerstolz.stockgameserver.exception.InsufficientSharesException;
import de.hackerstolz.stockgameserver.exception.StockNotFoundException;
import de.hackerstolz.stockgameserver.model.Account;
import de.hackerstolz.stockgameserver.model.Balance;
import de.hackerstolz.stockgameserver.model.Order;
import de.hackerstolz.stockgameserver.model.Quote;
import de.hackerstolz.stockgameserver.model.Recommendation;
import de.hackerstolz.stockgameserver.model.Transaction;
import de.hackerstolz.stockgameserver.service.AccountService;
import de.hackerstolz.stockgameserver.service.OrderService;
import de.hackerstolz.stockgameserver.service.QuoteService;
import de.hackerstolz.stockgameserver.service.RecommendationService;
import de.hackerstolz.stockgameserver.service.TransactionService;

@RestController
@RequestMapping("/api")
public class GameController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final QuoteService quoteService;
    private final TransactionService transactionService;
    private final AccountService accountService;
    private final OrderService orderService;
    private final RecommendationService recommendationService;

    public GameController(final QuoteService quoteService,
            final TransactionService transactionService,
            final AccountService accountService, final OrderService orderService,
            final RecommendationService recommendationService) {
        this.quoteService = quoteService;
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.orderService = orderService;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/v1/isalive")
    public ResponseEntity<Void> isAlive() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/v1/quote/{symbol}")
    public ResponseEntity<Quote> getQuote(@PathVariable final String symbol) {
        return quoteService.getStockInfo(symbol).map(ResponseEntity::ok).orElseGet(this::notFound);
    }

    // name is not unique, feel free to create multiple accounts and trade on them
    @PostMapping("/v1/account/{name}")
    public ResponseEntity<Account> createAccount(@PathVariable final String name) {
        return ResponseEntity.ok(accountService.createNewAccount(name));
    }

    @GetMapping("/v1/balance/{userId}")
    public ResponseEntity<Balance> getBalance(@PathVariable final String userId) {
        return ResponseEntity.ok(transactionService.getBalance(userId));
    }

    @GetMapping("/v1/transactions/{userId}")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable final String userId) {
        return ResponseEntity.ok(transactionService.getTransactions(userId));
    }

    @GetMapping("/v1/transactions/{userId}/{symbol}")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable final String userId, @PathVariable final String symbol) {
        return ResponseEntity.ok(transactionService.getTransactions(userId, symbol));
    }

    @PostMapping("/v1/order/{userId}")
    public ResponseEntity<?> placeOrder(@PathVariable final String userId, @RequestBody final Order order) {
        if (order.getIsBuy() == null || order.getAmount() == null || order.getSymbol() == null) {
            log.info("Invalid order: {}", order);
            return ResponseEntity.badRequest().build();
        }

        try {
            return ResponseEntity.ok(orderService.placeOrder(order, userId));
        } catch (final StockNotFoundException e) {
            return ResponseEntity.status(404).body("The symbol " + e.getSymbol() + " could not be found. If it's not a "
                                                   + "typo, then please report this to the admin.");
        } catch (final InsufficientFundsException e) {
            return ResponseEntity.status(400).body(
                    "Insufficient Funds. You don't have the required " + e.getRequiredAmount().intValue());
        } catch (final InsufficientSharesException e) {
            return ResponseEntity.status(400).body(
                    "Insufficient Shares. For the given symbol you only have " + e.getAvailableShares() + " shares");
        }
    }

    @GetMapping("/v1/analyses")
    public List<Recommendation> getRecommendations() {
        return recommendationService.findAll();
    }

    private ResponseEntity<Quote> notFound() {
        return ResponseEntity.notFound().build();
    }
}
