package net.eson.audio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

/**
 * @author Eson
 * @date 2025年03月24日 11:46
 */
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList("http://localhost:8008")); // ✅ 允许前端访问
        config.setAllowedMethods(Collections.singletonList("*")); // 允许所有请求方法
        config.setAllowedHeaders(Collections.singletonList("*")); // 允许所有请求头
        config.setAllowCredentials(true); // 允许携带 Cookie

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}