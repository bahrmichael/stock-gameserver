package de.hackerstolz.stockgameserver.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories("de.hackerstolz.stockgameserver.repositories")
public class DatabaseConfiguration extends AbstractMongoConfiguration {

    private final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);

    private final DatabaseProperties properties;
    private final ProfileProperties profile;

    public DatabaseConfiguration(final DatabaseProperties properties,
            final ProfileProperties profile) {
        this.properties = properties;
        this.profile = profile;
    }

    @Override
    public MongoClient mongoClient() {
        if (!profile.isDev()) {
            final ServerAddress address = new ServerAddress(properties.getHost(), properties.getPort());
            final MongoCredential credential = MongoCredential.createCredential(properties.getUsername(),
                                                                                properties.getDatabase(),
                                                                                properties.getPassword().toCharArray());
            final MongoClientOptions options = MongoClientOptions.builder().build();
            return new MongoClient(address, credential, options);
        } else {
            return new MongoClient("127.0.0.1", 27017);
        }
    }

    @Override
    protected String getDatabaseName() {
        return properties.getDatabase();
    }
}
