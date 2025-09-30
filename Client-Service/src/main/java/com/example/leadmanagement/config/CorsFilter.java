package com.example.leadmanagement.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Global CORS filter for Auth Service.
 * Allows cross-origin requests and handles preflight OPTIONS requests.
 *
 * Configurable allowed origins via application.properties or application.yml:
 * spring.cors.allowed-origins=https://frontend.example.com,http://localhost:3000
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    // You can load allowed origins from properties if needed
    private static final String[] ALLOWED_ORIGINS = {
            "http://localhost:3000",
            "https://leadslocator.orangebits.click"
    };

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String origin = request.getHeader("Origin");

        // Only allow configured origins or fallback to null
        if (origin != null && isAllowedOrigin(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "Authorization, Content-Type, X-Requested-With, Accept, Origin, " +
                        "Access-Control-Request-Method, Access-Control-Request-Headers");
        response.setHeader("Access-Control-Expose-Headers",
                "Authorization, Content-Type, X-Requested-With, Accept, Origin, " +
                        "Access-Control-Request-Method, Access-Control-Request-Headers");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }

    // Helper method to check if the request origin is allowed
    private boolean isAllowedOrigin(String origin) {
        for (String allowed : ALLOWED_ORIGINS) {
            if (allowed.equalsIgnoreCase(origin)) {
                return true;
            }
        }
        return false;
    }
}
