package de.hackerstolz.stockgameserver.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import de.hackerstolz.stockgameserver.model.Recommendation;

@Repository
public interface RecommendationRepository extends MongoRepository<Recommendation, String> {
    int countBySource(final String source);
}
