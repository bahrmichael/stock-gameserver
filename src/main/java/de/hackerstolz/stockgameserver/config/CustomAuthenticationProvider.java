package de.hackerstolz.stockgameserver.config;

import de.hackerstolz.stockgameserver.model.Account;
import de.hackerstolz.stockgameserver.service.AccountService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final AccountService accountService;

    public CustomAuthenticationProvider(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        final Account account = accountService.getAccount(name);
        if (account == null || !account.getSecret().equals(password)) {
            return null;
        }

        return new UsernamePasswordAuthenticationToken(name, password, new ArrayList<>());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
