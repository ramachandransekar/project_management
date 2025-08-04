package com.projectmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:[*]", "http://127.0.0.1:[*]", 
                                      "https://project-management-ynzl.vercel.app",
                                      "https://project-management-frontend.onrender.com")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", 
                               "Access-Control-Request-Method", "Access-Control-Request-Headers", "x-auth-token")
                .exposedHeaders("Authorization", "x-auth-token", "Access-Control-Allow-Origin", 
                               "Access-Control-Allow-Credentials")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow localhost with any port and production origins
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:[*]",
            "http://127.0.0.1:[*]",
            "https://project-management-ynzl.vercel.app",
            "https://project-management-frontend.onrender.com"
        ));
        
        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // Allow specific headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "x-auth-token"
        ));
        
        // Expose headers to the client
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "x-auth-token",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Set max age for preflight requests (in seconds)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private List<String> getAllowedOrigins() {
        return Arrays.asList(
            "http://localhost:3000",           // Local development
            "http://localhost:5173",           // Vite dev server
            "http://localhost:5174",           // Vite dev server (alternative port)
            "http://localhost:5175",           // Vite dev server (alternative port)
            "http://127.0.0.1:3000",          // Alternative local
            "http://127.0.0.1:5173",          // Alternative Vite
            "http://127.0.0.1:5174",          // Alternative Vite (alternative port)
            "http://127.0.0.1:5175",          // Alternative Vite (alternative port)
            "http://localhost:4173",          // Vite preview server
            "http://127.0.0.1:4173",          // Alternative Vite preview
            "https://project-management-ynzl.vercel.app", // Production frontend on Vercel
            "https://project-management-frontend.onrender.com" // Alternative production frontend
        );
    }
} 