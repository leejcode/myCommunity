package com.leej.community.controller;

import com.leej.community.entity.Message;
import com.leej.community.entity.Page;
import com.leej.community.entity.User;
import com.leej.community.service.MessageService;
import com.leej.community.service.UserService;
import com.leej.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page){
        User user=hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.selectConversationCount(user.getId()));
        //查询会话列表
        List<Message> messages = messageService.selectConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> conversations=new ArrayList<>();
        if(messages!=null){
            for(Message message:messages){
                Map<String,Object> map=new HashMap<>();
                map.put("conversation",message);
                map.put("letterCount",messageService.selectLetterCount(message.getConversationId()));
                map.put("unreadCount",messageService.selectLetterUnreadCount(user.getId(),message.getConversationId()));
                int targetId=user.getId()==message.getFromId()?message.getToId():message.getFromId();
                map.put("target",userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);
        //查询总的未读消息
        int letterUnreadCount=messageService.selectLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        return "/site/letter";
    }
    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Page page,Model model){
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.selectLetterCount(conversationId));
        List<Message> messages = messageService.selectLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> letters=new ArrayList<>();
        if(messages!=null){
            for(Message message:messages){
                Map<String,Object> map =new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);
        model.addAttribute("target",getLetterTarget(conversationId));
        return "/site/letter-detail";
    }
    private User getLetterTarget(String conversationId){
        String[] ids =conversationId.split("_");
        int d0=Integer.parseInt(ids[0]);
        int d1=Integer.parseInt(ids[1]);
        if(hostHolder.getUser().getId()==d0){
          return userService.findUserById(d1);
        }
        else{
            return userService.findUserById(d0);
        }
    }
}
