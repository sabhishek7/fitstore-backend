package com.fitstore.config;

import com.fitstore.filter.AdminAuthFilter;
import com.fitstore.service.AdminAuthService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SecurityConfig {

    /**
     * BCrypt encoder bean for hashing and verifying admin passwords.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Register the admin auth filter to intercept all /api/admin/* requests.
     */
    @Bean
    public FilterRegistrationBean<AdminAuthFilter> adminAuthFilter(AdminAuthService authService) {
        FilterRegistrationBean<AdminAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AdminAuthFilter(authService));
        registration.addUrlPatterns("/api/admin/*");
        registration.setOrder(1);
        return registration;
    }
}
