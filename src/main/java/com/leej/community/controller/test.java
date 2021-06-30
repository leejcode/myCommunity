package com.leej.community.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController //相当于controller+responsebody注解的组合，前者将该类对象注入到容器中，
                // 后者说明该类所有的API接口返回的数据都以json形式返回给客户端 String除外
@RequestMapping(value = "/test")
public class test {
    @GetMapping("/hello")
    public String sayhello(){
        System.out.println("qee");
        return "Hello Spring boot";
    }
    @GetMapping
    public Map testGet() {
        return new HashMap<String, String>(){{
            put("name", "springboot");
        }};
    }
}
