package de.hackerstolz.stockgameserver.repositories;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import de.hackerstolz.stockgameserver.model.Transaction;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    Stream<Transaction> findAllByUserId(String userId);
    List<Transaction> findAllByUserIdOrderByInstantDesc(String userId);
    List<Transaction> findAllByUserIdAndSymbolOrderByInstantDesc(String userId, String symbol);
}
