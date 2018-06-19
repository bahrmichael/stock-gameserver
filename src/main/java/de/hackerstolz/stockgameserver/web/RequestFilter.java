package de.hackerstolz.stockgameserver.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import de.hackerstolz.stockgameserver.model.Account;
import de.hackerstolz.stockgameserver.service.AccountService;

@Component
@Order(1)
public class RequestFilter implements Filter {

    private final AccountService accountService;

    public RequestFilter(final AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse res = (HttpServletResponse) response;

        final String servletPath = req.getServletPath();
        if (servletPath.startsWith("/api")
            && !servletPath.startsWith("/api/isalive")
            && !servletPath.startsWith("/api/quote")
            && !servletPath.startsWith("/api/account")) {
            doFilter(req, res, servletPath);
        }

        chain.doFilter(request, response);
    }

    private void doFilter(final HttpServletRequest req, final HttpServletResponse res, final String servletPath) {
        // do filter
        final String[] split = servletPath.split("/");
        if (split.length < 3) {
            res.setStatus(400);
            return;
        }
        final String userId = split[2];
        final Account account = accountService.getAccount(userId);
        if (null == account) {
            res.setStatus(404);
            return;
        }

        final String expectedSecret = account.getSecret();
        final String authorizationHeader = req.getHeader("Authorization");
        final String actualSecret = authorizationHeader.split(" ")[1];

        if (!expectedSecret.equals(actualSecret)) {
            res.setStatus(401);
        }
    }

    @Override
    public void init(final FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }
}
