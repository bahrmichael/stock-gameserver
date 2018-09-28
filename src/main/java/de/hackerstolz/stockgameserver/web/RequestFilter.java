package de.hackerstolz.stockgameserver.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

//@Component
//@Order(1)
public class RequestFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;

        final String servletPath = req.getServletPath();
        log.info("{} {} was invoked.", req.getMethod(), servletPath);

        chain.doFilter(request, response);
    }

    @Override
    public void init(final FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }
}
