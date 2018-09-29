package de.hackerstolz.stockgameserver.web;

import de.hackerstolz.stockgameserver.config.SecurityUtils;
import de.hackerstolz.stockgameserver.model.Transaction;
import de.hackerstolz.stockgameserver.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions/")
public class TransactionResource {

    private final TransactionService transactionService;

    public TransactionResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions() {
        final String userId = SecurityUtils.getCurrentUserLoginAsString();
        return ResponseEntity.ok(transactionService.getTransactions(userId));
    }

    @GetMapping("{symbol}/")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable final String symbol) {
        final String userId = SecurityUtils.getCurrentUserLoginAsString();
        return ResponseEntity.ok(transactionService.getTransactions(userId, symbol));
    }
}
