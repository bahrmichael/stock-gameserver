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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import de.hackerstolz.stockgameserver.model.Account;
import de.hackerstolz.stockgameserver.service.AccountService;

@Component
@Order(1)
public class RequestFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(getClass());

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
        log.info("{} {} was invoked.", req.getMethod(), servletPath);

        boolean deny = false;
        if (servletPath.startsWith("/api")
            && !servletPath.startsWith("/api/v1/isalive")
            && !servletPath.startsWith("/api/v1/quote")
            && !servletPath.startsWith("/api/v1/account")) {

            deny = shouldDeny(req, res, servletPath);
        }

        if (!deny) {
            chain.doFilter(request, response);
        }
    }

    private boolean shouldDeny(final HttpServletRequest req, final HttpServletResponse res, final String servletPath) {
        // do filter
        final String[] split = servletPath.split("/");
        if (split.length < 5) {
            write(res,"The requested endpoint could not be found.");
            res.setStatus(404);
            return true;
        }
        final String userId = split[4];
        final Account account = accountService.getAccount(userId);
        if (null == account) {
            write(res,"The user could not be found.");
            res.setStatus(404);
            return true;
        }

        final String expectedSecret = account.getSecret();
        final String authorizationHeader = req.getHeader("Authorization");
        if (null == authorizationHeader) {
            res.setStatus(401);
            write(res, "No Authorization header was set.");
            return true;
        }
        final String[] authSplit = authorizationHeader.split(" ");
        if (authSplit.length != 2) {
            write(res, "The Authorization header has an error. Please make sure that it follow the format 'Secret "
                       + "<secret:string>' and that there is an actual whitespace in between.");
            res.setStatus(401);
            return true;
        }
        final String actualSecret = authSplit[1];
        if (!expectedSecret.equals(actualSecret)) {
            write(res, "The secret is not valid for this user.");
            res.setStatus(401);
            return true;
        }
        return false;
    }

    private void write(final HttpServletResponse res, final String text) {
        try {
            res.getWriter().append(text);
        } catch (final IOException e) {
            log.error("Failed to write response", e);
        }
    }

    @Override
    public void init(final FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }
}
