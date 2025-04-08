package net.eson.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
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
        String secretKey = "eson666eson666eson666eson666eson"; // 至少32位
        long ttlMillis = 3600_000; // 1小时
        Map<String, Object> claims = new HashMap<>();
        claims.put("code","helloWorld");
        // 生成 token
        String token = JwtUtil.generateToken(secretKey, ttlMillis, claims);
        System.out.println("生成的JWT Token：");
        System.out.println(token);

        // 解析 token 验证内容
        Claims parsed = JwtUtil.parseToken(secretKey, token);
        System.out.println("解析得到的 claims：");
        System.out.println("code: " + parsed.get("code"));
        System.out.println("过期时间: " + parsed.getExpiration());
    }
}
