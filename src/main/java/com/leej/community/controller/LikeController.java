package com.leej.community.controller;

import com.leej.community.entity.User;
import com.leej.community.service.LikeService;
import com.leej.community.utils.CommunityUtil;
import com.leej.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId){

        User user =hostHolder.getUser();

        likeService.like(user.getId(),entityType,entityId,entityUserId);
        long entityLikeCount = likeService.findEntityLikeCount(entityType, entityId);
        int Status = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",entityLikeCount);
        map.put("likeStatus",Status);
        System.out.println(map);
        return CommunityUtil.getJsonString(0,null,map);
    }
}
