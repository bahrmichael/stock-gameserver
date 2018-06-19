package de.hackerstolz.stockgameserver.model;

import org.springframework.data.annotation.Id;

public class Account {
    @Id
    private String userId;
    private String secret;
    private String name;

    public Account() {
    }

    public Account(final String userId, final String secret, final String name) {
        this.userId = userId;
        this.secret = secret;
        this.name = name;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public void setSecret(final String secret) {
        this.secret = secret;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getSecret() {
        return secret;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
