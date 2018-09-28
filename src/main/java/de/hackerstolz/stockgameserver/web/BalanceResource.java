package de.hackerstolz.stockgameserver.web;

import de.hackerstolz.stockgameserver.config.SecurityUtils;
import de.hackerstolz.stockgameserver.model.Balance;
import de.hackerstolz.stockgameserver.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/balance")
public class BalanceResource {

    private final TransactionService transactionService;

    public BalanceResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<Balance> getBalance() {
        final String userId = SecurityUtils.getCurrentUserLoginAsString();
        return ResponseEntity.ok(transactionService.getBalance(userId));
    }
}
