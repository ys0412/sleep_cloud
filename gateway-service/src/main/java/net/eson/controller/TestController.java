package net.eson.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Eson
 * @date 2025年04月09日 11:41
 */
@RefreshScope
@RestController
@RequestMapping("/auth")
public class TestController {

    @Value("${spring.application.name}")
    private String name;


    @GetMapping("/goLogin")
    public String wechatLogin() {
        return name;
    }


}