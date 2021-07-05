package com.leej.community.controller;

import com.leej.community.entity.Comment;
import com.leej.community.entity.DiscussPost;
import com.leej.community.entity.Event;
import com.leej.community.event.EventProducer;
import com.leej.community.service.CommentService;
import com.leej.community.service.DiscussPostService;
import com.leej.community.utils.CommunityConstant;
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
public class CommentController implements CommunityConstant {
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private DiscussPostService discussPostService;
    @PostMapping("/add/{postid}")
    public String addComment(@PathVariable("postid") int postid, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        if(comment.getTargetId()==null){
            comment.setTargetId(0);
        }
        commentService.addComment(comment);
        //触发评论事件
        Event event = new Event().setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",postid);
        if(comment.getEntityType()==ENTITY_TYPE_POST){
            DiscussPost discussPost = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(discussPost.getUserId());
        }else if(comment.getEntityType()==ENTITY_TYPE_COMMENT){
            Comment target =commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);
        if(comment.getEntityType()==ENTITY_TYPE_POST){
             event=new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityId(postid)
                    .setEntityType(ENTITY_TYPE_POST);
            eventProducer.fireEvent(event);
        }
        return "redirect:/discuss/detail/"+postid;
    }
}
