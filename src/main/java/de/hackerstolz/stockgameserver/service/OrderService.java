package de.hackerstolz.stockgameserver.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.hackerstolz.stockgameserver.exception.InsufficientFundsException;
import de.hackerstolz.stockgameserver.exception.InsufficientSharesException;
import de.hackerstolz.stockgameserver.exception.StockNotFoundException;
import de.hackerstolz.stockgameserver.model.Order;
import de.hackerstolz.stockgameserver.model.Quote;
import de.hackerstolz.stockgameserver.model.Transaction;

@Service
public class OrderService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final QuoteService quoteService;
    private final TransactionService transactionService;

    public OrderService(final QuoteService quoteService,
            final TransactionService transactionService) {
        this.quoteService = quoteService;
        this.transactionService = transactionService;
    }

    public Transaction placeOrder(final Order order, final String userId)
            throws StockNotFoundException, InsufficientFundsException, InsufficientSharesException {
        final Optional<Quote> quoteOptional = quoteService.getStockInfo(order.getSymbol());
        if (!quoteOptional.isPresent()) {
            log.info("Symbol {} could not be retrieved for user {}.", order.getSymbol(), userId);
            throw new StockNotFoundException(order.getSymbol());
        }

        final Quote quote = quoteOptional.get();
        final Double price = quote.getLatestPrice();

        if (order.getIsBuy()) {
            final double availableAmount = transactionService.getBalance(userId).getAmount();
            final double requiredAmount = price * order.getAmount();
            if (availableAmount < requiredAmount) {
                log.info("User {} does not have sufficient funds to buy {} {}.", userId, order.getAmount(), order.getSymbol());
                throw new InsufficientFundsException(requiredAmount);
            }
        } else {
            final int availableShares = transactionService.getRemainingShares(userId, order.getSymbol());
            if (availableShares < order.getAmount()) {
                log.info("User {} does not have sufficient shares to sell {} {}.", userId, order.getAmount(), order.getSymbol());
                throw new InsufficientSharesException(availableShares);
            }
        }

        return transactionService.createTransaction(userId, order.getSymbol(), order.getAmount(), order.getIsBuy(), price);
    }
}
