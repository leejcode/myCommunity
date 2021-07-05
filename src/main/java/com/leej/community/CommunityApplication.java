package com.leej.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.annotation.PostConstruct;

@EnableAspectJAutoProxy
@SpringBootApplication
public class CommunityApplication {
    @PostConstruct
    public void init(){
        //解决netty启动冲突的问题 redis和es之间
        System.setProperty("es.set.netty.runtime.available.processors","false");
    }
    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
