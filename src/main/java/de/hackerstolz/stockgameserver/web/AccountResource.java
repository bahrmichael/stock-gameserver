package de.hackerstolz.stockgameserver.web;

import de.hackerstolz.stockgameserver.model.Account;
import de.hackerstolz.stockgameserver.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account/")
public class AccountResource {

    private final AccountService accountService;

    public AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

    // name is not unique, feel free to create multiple accounts and trade on them
    @PostMapping("/{name}")
    public ResponseEntity<Account> createAccount(@PathVariable final String name) {
        return ResponseEntity.ok(accountService.createNewAccount(name));
    }
}
