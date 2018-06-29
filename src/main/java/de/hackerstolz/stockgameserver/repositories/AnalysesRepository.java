package de.hackerstolz.stockgameserver.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import de.hackerstolz.stockgameserver.model.Analysis;

@Repository
public interface AnalysesRepository extends MongoRepository<Analysis, String> {
    int countBySource(final String source);
}
