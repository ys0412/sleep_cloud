package net.eson.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * @author Eson
 * @date 2025年04月08日 15:18
 */
@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    private static final String SECRET_KEY = "eson666";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("aaaaa");
        String token= exchange.getRequest().getHeaders().getFirst("Authorization");
        if(token!=null&&token.startsWith("Bearer")){
            try{
                token = token.substring(7);
                SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
                exchange.getRequest().mutate().header("user", claims.getSubject()).build();
            }catch (Exception e){
                return Mono.error(new RuntimeException("Invalid token"));
            }
            return chain.filter(exchange);
        }
        return Mono.error(new RuntimeException("JWT Token is missing or invalid"));
//        return chain.filter(exchange);

    }
}
    