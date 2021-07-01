package com.leej.community;

import com.leej.community.utils.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@RunWith(SpringRunner.class) //告知java这个类通过什么运行环境运行，例如启动和创建spring的应用上下文，直接使用spring容器
@SpringBootTest
public class MailTests {
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Test
    public void testText(){
        mailClient.sendMail("aqa909101@qq.com","TEST","Welcome");
    }
    @Test
    public void testTemplate(){
        Context context = new Context();
        context.setVariable("username","sunday");
        String content=templateEngine.process("/mail/demo",context);
        System.out.println(content);
        mailClient.sendMail("aqa909101@qq.com","html",content);
    }
}
