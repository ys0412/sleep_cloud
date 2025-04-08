//package net.eson.component;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//
//@Component
//public class JwtFilter implements WebFilter {
//
//    private static final String SECRET_KEY = "eson666";
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        String token= exchange.getRequest().getHeaders().getFirst("Authorization");
//        if(token!=null&&token.startsWith("Bearer")){
//            try{
//                token = token.substring(7);
//                SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
//                Claims claims = Jwts.parser()
//                        .verifyWith(key)
//                        .build()
//                        .parseSignedClaims(token)
//                        .getPayload();
//                exchange.getRequest().mutate().header("user", claims.getSubject()).build();
//            }catch (Exception e){
//                return Mono.error(new RuntimeException("Invalid token"));
//            }
//        return chain.filter(exchange);
//        }
//
//        return chain.filter(exchange);
//    }
//}
