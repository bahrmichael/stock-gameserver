package de.hackerstolz.stockgameserver.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import de.hackerstolz.stockgameserver.model.Account;
import de.hackerstolz.stockgameserver.model.Transaction;
import de.hackerstolz.stockgameserver.repositories.AccountRepository;
import de.hackerstolz.stockgameserver.repositories.TransactionRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(final AccountRepository accountRepository,
            final TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Account createNewAccount(final String name) {
        final UUID uuid = UUID.randomUUID();
        final UUID secret = UUID.randomUUID();
        final Account account = new Account(uuid.toString(), secret.toString(), name);
        final Account created = accountRepository.save(account);
        transactionRepository.save(starterTransaction(account.getUserId()));
        return created;
    }

    private Transaction starterTransaction(final String userId) {
        return new Transaction(userId, 10_000.0, 1, "starter", false);
    }

    public Account getAccount(final String userId) {
        return accountRepository.findById(userId).orElse(null);
    }
}
