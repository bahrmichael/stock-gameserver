package de.hackerstolz.stockgameserver.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import de.hackerstolz.stockgameserver.model.Account;

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {
    Optional<Account> findByName(String name);
}
