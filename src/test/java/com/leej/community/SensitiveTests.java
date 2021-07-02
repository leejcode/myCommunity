package com.leej.community;

import com.leej.community.dao.CommentMapper;
import com.leej.community.entity.Comment;
import com.leej.community.utils.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class) //告知java这个类通过什么运行环境运行，例如启动和创建spring的应用上下文，直接使用spring容器
@SpringBootTest
public class SensitiveTests {
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private CommentMapper commentMapper;
    @Test
    public void testSensitive(){
        String text="这里可以☆赌博☆,可以☆嫖娼☆,可以☆吸毒☆,可以☆开票☆,哈哈哈!";
        String filter = sensitiveFilter.filter(text);
        System.out.println(filter);
    }
    @Test
    public void testCommentMapper(){
        List<Comment> comments = commentMapper.selectCommentByEntity(2, 12, 0, Integer.MAX_VALUE);
        for(Comment c:comments){
            System.out.println(c);
        }
    }
}
