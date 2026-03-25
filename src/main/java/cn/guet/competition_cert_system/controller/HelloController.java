package cn.guet.competition_cert_system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "欢迎使用竞赛证书系统！";
    }

    @GetMapping("/api/info")
    public String info() {
        return "系统运行正常";
    }
}
