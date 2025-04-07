package net.eson.controller;

import net.eson.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @PostMapping("/wechatLogin")
    public ResponseEntity<?> wechatLogin(@RequestBody Map<String, String> body) {
        String code = body.get("code");

        // 你自己的微信小程序 appId 和 secret
        String appId = "wx94a4ba0bea9f3088";
        String secret = "c50d9064242a837147a820859704b7ac";

        // 调用微信接口获取 openid
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId +
                "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> wechatResp = restTemplate.getForObject(url, Map.class);

        String openid = wechatResp.get("openid");

        if (openid != null) {
            // 使用 openid 生成 JWT
            Map<String, Object> claims = Map.of("sub", openid, "platform", "miniapp");

            String token = JwtUtil.generateToken("eson666", 3600_000L, claims);
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(401).body("code 无效");
        }
    }
}
