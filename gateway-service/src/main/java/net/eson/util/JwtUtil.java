package net.eson.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    public static String generateToken(String secretKey, long ttlMillis, Map<String, Object> claims){
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        // 设置jwt的body
        return Jwts.builder()
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(key)
                .claims(claims)
                // 设置过期时间
                .expiration(exp)
                .compact();
    }

    public static Claims parseToken(String secretKey,String token){

        //生成 HMAC 密钥，根据提供的字节数组长度选择适当的 HMAC 算法，并返回相应的 SecretKey 对象。
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        // 得到DefaultJwtParser
        JwtParser jwtParser = Jwts.parser()
                // 设置签名的秘钥
                .verifyWith(key)
                .build();
        Jws<Claims> claimsJws = jwtParser.parseSignedClaims(token);
        return claimsJws.getPayload();
    }

    public static void main(String[] args) {
//        parseToken("eson666",)
    }
}
