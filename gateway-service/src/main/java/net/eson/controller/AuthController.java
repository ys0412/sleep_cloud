package net.eson.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.eson.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/login")
public class AuthController {

    private final WebClient webClient;

    // 通过构造函数注入 WebClient 实例
    public AuthController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.weixin.qq.com").build();
    }

    @PostMapping("/wechatLogin")
    public Mono<ResponseEntity<Map<String, Object>>> wechatLogin(@RequestBody Map<String, String> body) {
        String code = body.get("code");

        // 你自己的微信小程序 appId 和 secret
        String appId = "wx94a4ba0bea9f3088";
        String secret = "82882a56c55294d1fe8f3753a52138b5";

        // 正确的微信 API URL
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId +
                "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)  // 获取原始的 JSON 字符串
                .flatMap(responseBody -> {
                    // 使用 ObjectMapper 将 JSON 字符串解析成 Map
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        Map<String, Object> wechatResp = objectMapper.readValue(responseBody, Map.class);
                        String openid = (String) wechatResp.get("openid");

                        if (openid != null) {
                            // 使用 openid 生成 JWT
                            Map<String, Object> claims = Map.of("sub", openid, "platform", "miniapp");

                            String token = JwtUtil.generateToken("eson666eson666eson666eson666eson", 3600_000L, claims);
                            return Mono.just(ResponseEntity.ok(Map.of("token", token)));
                        } else {
                            return Mono.just(ResponseEntity.status(401).body(Map.of("error", "code 无效")));
                        }
                    } catch (JsonProcessingException e) {
                        // 如果 JSON 解析失败
                        return Mono.just(ResponseEntity.status(500).body(Map.of("error", "解析微信返回数据失败")));
                    }
                });
    }

}
