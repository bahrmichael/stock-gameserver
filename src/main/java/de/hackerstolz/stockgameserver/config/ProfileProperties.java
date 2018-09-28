package de.hackerstolz.stockgameserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.profiles")
public class ProfileProperties {

    private String active;

    public String getActive() {
        return active;
    }

    public void setActive(final String active) {
        this.active = active;
    }

    public boolean isDev() {
        return "dev".equals(active);
    }

    public boolean isProd() {
        return "prod".equals(active);
    }

}
