package com.fitstore.filter;

import com.fitstore.service.AdminAuthService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Intercepts all requests to /api/admin/* and validates the admin token.
 * Skips the /api/admin/login endpoint so admins can authenticate.
 */
public class AdminAuthFilter implements Filter {

    private final AdminAuthService authService;

    public AdminAuthFilter(AdminAuthService authService) {
        this.authService = authService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        // Allow login endpoint without authentication
        if (path.equals("/api/admin/login")) {
            chain.doFilter(request, response);
            return;
        }

        // Allow CORS preflight requests
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // Validate Authorization header
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(httpResponse, "No token provided");
            return;
        }

        String token = authHeader.substring(7);
        if (!authService.validateToken(token)) {
            sendUnauthorized(httpResponse, "Invalid or expired token");
            return;
        }

        chain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"status\":\"error\",\"message\":\"Unauthorized - " + message + "\"}");
    }
}
