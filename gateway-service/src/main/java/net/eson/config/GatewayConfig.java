//package net.eson.config;
//
//import net.eson.component.JwtAuthenticationFilter;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author Eson
// * @date 2025年04月08日 15:23
// */
//@Configuration
//public class GatewayConfig {
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtAuthenticationFilter jwtAuthenticationFilter) {
//        return builder.routes()
//                .route("my-service-route", r -> r.path("/api/**")
//                        .filters(f -> f.filter(jwtAuthenticationFilter) // 使用 JwtAuthenticationFilter
//                                .stripPrefix(1)) // 去掉 /api 前缀
//                        .uri("http://localhost:8084"))
//                .build();
//    }
//}
