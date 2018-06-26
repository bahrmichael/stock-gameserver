package de.hackerstolz.stockgameserver.service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.hackerstolz.stockgameserver.model.Balance;
import de.hackerstolz.stockgameserver.model.HasSymbol;
import de.hackerstolz.stockgameserver.model.Transaction;
import de.hackerstolz.stockgameserver.repositories.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(final TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Balance getBalance(final String userId) {
        final double balance = transactionRepository.findAllByUserId(userId).mapToDouble(Transaction::getValue).sum();
        return new Balance(userId, balance);
    }

    public List<Transaction> getTransactions(final String userId) {
        return transactionRepository.findAllByUserId(userId).collect(Collectors.toList());
    }

    public List<Transaction> getTransactions(final String userId, final String symbol) {
        return getTransactions(userId).stream().filter(filterBySymbol(symbol)).collect(Collectors.toList());
    }

    private Predicate<HasSymbol> filterBySymbol(final String symbol) {
        return entity -> entity.getSymbol().equalsIgnoreCase(symbol);
    }

    public Transaction createTransaction(final String userId, final String symbol, final Integer amount, final Boolean isBuy,
            final Double pricePerUnit) {
        return transactionRepository.save(new Transaction(userId, pricePerUnit, amount, symbol, isBuy));
    }

    public int getRemainingShares(final String userId, final String symbol) {
        return transactionRepository.findAllByUserIdAndSymbolOrderByInstantDesc(userId, symbol)
                .stream().mapToInt(t -> {
                    int sign = t.isBuy() ? 1 : -1;
                    return sign * t.getAmount();
                }).sum();
    }
}
