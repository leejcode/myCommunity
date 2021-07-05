package com.leej.community.controller;

import com.leej.community.entity.DiscussPost;
import com.leej.community.entity.Page;
import com.leej.community.service.ElasticsearchService;
import com.leej.community.service.LikeService;
import com.leej.community.service.UserService;
import com.leej.community.utils.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @GetMapping("/search")
    public String search(String keyword, Page page, Model model){
        //搜索帖子
        org.springframework.data.domain.Page<DiscussPost> discussPosts = elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        //聚合数据
        List<Map<String,Object>> posts = new ArrayList<>();
        if(discussPosts!=null){
            for(DiscussPost post:discussPosts){
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                map.put("user",userService.findUserById(post.getUserId()));
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId()));
                posts.add(map);
            }
        }
        model.addAttribute("posts",posts);
        model.addAttribute("keyword",keyword);
        page.setPath("/search?keyword="+keyword);
        page.setRows(discussPosts==null?0:(int)discussPosts.getTotalElements());
        return "/site/search";
    }
}
