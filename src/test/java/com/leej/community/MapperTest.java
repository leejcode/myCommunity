package com.leej.community;

import com.leej.community.dao.DiscussPostMapper;
import com.leej.community.dao.UserMapper;
import com.leej.community.entity.DiscussPost;
import com.leej.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class) //告知java这个类通过什么运行环境运行，例如启动和创建spring的应用上下文，直接使用spring容器
@SpringBootTest
public class MapperTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);
        User liubei = userMapper.selectByName("liubei");
        System.out.println(liubei);
        User user1 = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user1);
    }
    @Test
    public void testInsertUser(){
        User user =new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setActivationCode("");
        user.setCreateTime(new Date());
        user.setHeaderUrl("http://images.nowcoder.com/head/100t.png");
        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }
    @Test
    public void testUpdateUser(){
        userMapper.updateHeader(150,"http://images.nowcoder.com/head/101t.png");
        userMapper.updatePassword(150,"333333");
    }
    @Test
    public void testSelectPosts(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 10,0);
        for(DiscussPost d:discussPosts){
            System.out.println(d);
        }
    }
}
