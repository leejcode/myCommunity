package com.leej.community;

import com.leej.community.dao.LoginTicketMapper;
import com.leej.community.entity.LoginTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class) //告知java这个类通过什么运行环境运行，例如启动和创建spring的应用上下文，直接使用spring容器
@SpringBootTest
public class LoginTicketTests {
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Test
    public void testInsert(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket("abc");
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
        loginTicket.setStatus(0);
        loginTicket.setUserId(101);
        loginTicketMapper.insertLoginTicket(loginTicket);
    }
    @Test
    public void testSelect(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
        loginTicketMapper.updateStatus("abc",1);
    }
}
