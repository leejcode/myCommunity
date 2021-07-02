package com.leej.community.controller;

import com.leej.community.entity.Comment;
import com.leej.community.service.CommentService;
import com.leej.community.utils.HostHolder;
import org.apache.catalina.manager.host.HostManagerServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    @PostMapping("/add/{postid}")
    public String addComment(@PathVariable("postid") int postid, Comment comment){
        System.out.println(postid);
        System.out.println(comment);
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        if(comment.getTargetId()==null){
            comment.setTargetId(0);
        }
        commentService.addComment(comment);
        return "redirect:/discuss/detail/"+postid;
    }
}
