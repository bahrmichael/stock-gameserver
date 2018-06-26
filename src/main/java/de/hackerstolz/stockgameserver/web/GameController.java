package de.hackerstolz.stockgameserver.web;

import java.util.List;

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
import de.hackerstolz.stockgameserver.model.Transaction;
import de.hackerstolz.stockgameserver.service.AccountService;
import de.hackerstolz.stockgameserver.service.OrderService;
import de.hackerstolz.stockgameserver.service.QuoteService;
import de.hackerstolz.stockgameserver.service.TransactionService;

@RestController
@RequestMapping("/api")
public class GameController {

    private final QuoteService quoteService;
    private final TransactionService transactionService;
    private final AccountService accountService;
    private final OrderService orderService;

    public GameController(final QuoteService quoteService,
            final TransactionService transactionService,
            final AccountService accountService, final OrderService orderService) {
        this.quoteService = quoteService;
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.orderService = orderService;
    }

    @GetMapping("/isalive")
    public ResponseEntity<Void> isAlive() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/quote/{symbol}")
    public ResponseEntity<Quote> getQuote(@PathVariable final String symbol) {
        return quoteService.getStockInfo(symbol).map(ResponseEntity::ok).orElseGet(this::notFound);
    }

    // name is not unique, feel free to create multiple accounts and trade on them
    @PostMapping("/account/{name}")
    public ResponseEntity<Account> createAccount(@PathVariable final String name) {
        return ResponseEntity.ok(accountService.createNewAccount(name));
    }

    @GetMapping("/balance/{userId}")
    public ResponseEntity<Balance> getBalance(@PathVariable final String userId) {
        return ResponseEntity.ok(transactionService.getBalance(userId));
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable final String userId) {
        return ResponseEntity.ok(transactionService.getTransactions(userId));
    }

    @GetMapping("/transactions/{userId}/{symbol}")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable final String userId, @PathVariable final String symbol) {
        return ResponseEntity.ok(transactionService.getTransactions(userId, symbol));
    }

    @PostMapping("/order/{userId}")
    public ResponseEntity<?> placeOrder(@PathVariable final String userId, @RequestBody final Order order) {
        if (order.getIsBuy() == null || order.getAmount() == null || order.getSymbol() == null) {
            System.out.println("Invalid order: " + order);
            return ResponseEntity.badRequest().build();
        }

        try {
            return ResponseEntity.ok(orderService.placeOrder(order, userId));
        } catch (StockNotFoundException e) {
            return ResponseEntity.status(404).body("The symbol " + e.getSymbol() + " could not be found. If it's not a "
                                                   + "typo, then please report this to the admin.");
        } catch (InsufficientFundsException e) {
            return ResponseEntity.status(400).body(
                    "Insufficient Funds. You don't have the required " + e.getRequiredAmount().intValue());
        } catch (InsufficientSharesException e) {
            return ResponseEntity.status(400).body(
                    "Insufficient Shares. For the given symbol you only have " + e.getAvailableShares() + " shares");
        }
    }

    private ResponseEntity<Quote> notFound() {
        return ResponseEntity.notFound().build();
    }
}
