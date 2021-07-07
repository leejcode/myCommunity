package com.leej.community.controller;

import com.leej.community.entity.Comment;
import com.leej.community.entity.DiscussPost;
import com.leej.community.entity.Event;
import com.leej.community.entity.User;
import com.leej.community.event.EventProducer;
import com.leej.community.service.LikeService;
import com.leej.community.utils.CommunityConstant;
import com.leej.community.utils.CommunityUtil;
import com.leej.community.utils.HostHolder;
import com.leej.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId,int postId){

        User user =hostHolder.getUser();

        likeService.like(user.getId(),entityType,entityId,entityUserId);
        long entityLikeCount = likeService.findEntityLikeCount(entityType, entityId);
        int Status = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",entityLikeCount);
        map.put("likeStatus",Status);
        //触发事件
        if(Status==1){
            Event event = new Event().setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);
                    eventProducer.fireEvent(event);
        }
        if(entityType==ENTITY_TYPE_POST){
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey,postId);
        }
        return CommunityUtil.getJsonString(0,null,map);
    }
}
