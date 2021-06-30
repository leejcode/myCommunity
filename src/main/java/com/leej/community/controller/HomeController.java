package com.leej.community.controller;

import com.leej.community.dao.DiscussPostMapper;
import com.leej.community.entity.DiscussPost;
import com.leej.community.entity.Page;
import com.leej.community.entity.User;
import com.leej.community.service.DiscussPostService;
import com.leej.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @GetMapping("/index")
    public String getIndexPage(Model m, Page page){//会自动根据?后的参数自动给page赋值 current赋值
        System.out.println(page.getCurrent());
        page.setRows(discussPostService.findDiscussPostsRows(0));
        page.setPath("/index");
        List<DiscussPost> discusssPosts = discussPostService.findDiscusssPosts(0, page.getOffest(), page.getLimit());
        List<Map<String,Object>> dis = new ArrayList<>();
        if(discusssPosts!=null){
            for(DiscussPost d:discusssPosts){
                Map<String,Object> map=new HashMap<>();
                map.put("post",d);
                User user=userService.findUserById(d.getUserId());
                map.put("user",user);
                dis.add(map);
            }
        }
        m.addAttribute("discussPosts",dis);

        return "/index";
    }
}
