package com.leej.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.leej.community.entity.Message;
import com.leej.community.entity.Page;
import com.leej.community.entity.User;
import com.leej.community.service.MessageService;
import com.leej.community.service.UserService;
import com.leej.community.utils.CommunityConstant;
import com.leej.community.utils.CommunityUtil;
import com.leej.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {
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
        int noticeUnreadCount =messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
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
        //设置已读
        List<Integer> letterIds = getLetterIds(messages);
        if(!letterIds.isEmpty()){
            messageService.readMessage(letterIds);
        }
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
    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if(letterList!=null){
            for(Message message:letterList){
                if(hostHolder.getUser().getId()==message.getToId()&&message.getStatus()==0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }
    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName,String content){
        User target = userService.findUserByName(toName);
        if(target==null){
            return CommunityUtil.getJsonString(1,"目标用户不存在");//实现前后端分离的一种方式
        }
        Message message = new Message();
        message.setContent(content);
        message.setToId(target.getId());
        message.setFromId(hostHolder.getUser().getId());
        if(message.getFromId()<message.getToId())
        {
            message.setConversationId(message.getFromId()+"_"+ message.getToId());
        }else{
            message.setConversationId(message.getToId()+"_"+ message.getFromId());
        }
        message.setCreateTime(new Date());
        message.setStatus(0);
        messageService.addMessage(message);
        return CommunityUtil.getJsonString(0);
    }
    @GetMapping("/notice/list")
    public String getNoticeList(Model model){
        User user=hostHolder.getUser();
        //查询评论类
        Message message =messageService.findLatestNotice(user.getId(),TOPIC_COMMENT);
        Map<String,Object>map = new HashMap<>();
        if(message!=null){
            map.put("message",message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap data = JSONObject.parseObject(content, HashMap.class);
            map.put("user",userService.findUserById((Integer) data.get("userId")));
            map.put("entityType",data.get("entityType"));
            map.put("entityId",data.get("entityId"));
            map.put("postId",data.get("postId"));
            int count = messageService.findNoticeCount(user.getId(),TOPIC_COMMENT);
            map.put("count",count);
            int unread = messageService.findNoticeUnreadCount(user.getId(),TOPIC_COMMENT);
            map.put("unread",unread);
        }
        model.addAttribute("commentNotice",map);
        //查询点赞类
        message =messageService.findLatestNotice(user.getId(),TOPIC_LIKE);
        map = new HashMap<>();
        if(message!=null){
            map.put("message",message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap data = JSONObject.parseObject(content, HashMap.class);
            map.put("user",userService.findUserById((Integer) data.get("userId")));
            map.put("entityType",data.get("entityType"));
            map.put("entityId",data.get("entityId"));
            map.put("postId",data.get("postId"));
            int count = messageService.findNoticeCount(user.getId(),TOPIC_LIKE);
            map.put("count",count);
            int unread = messageService.findNoticeUnreadCount(user.getId(),TOPIC_LIKE);
            map.put("unread",unread);
        }
        model.addAttribute("likeNotice",map);
        //查询关注类
        message =messageService.findLatestNotice(user.getId(),TOPIC_FOLLOW);
        map = new HashMap<>();
        if(message!=null){
            map.put("message",message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap data = JSONObject.parseObject(content, HashMap.class);
            map.put("user",userService.findUserById((Integer) data.get("userId")));
            map.put("entityType",data.get("entityType"));
            map.put("entityId",data.get("entityId"));
            int count = messageService.findNoticeCount(user.getId(),TOPIC_FOLLOW);
            map.put("count",count);
            int unread = messageService.findNoticeUnreadCount(user.getId(),TOPIC_FOLLOW);
            map.put("unread",unread);
        }
        model.addAttribute("followNotice",map);
        //查询未读消息数量
        int letterUnreadCount = messageService.selectLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount =messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        return "/site/notice";
    }
    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic")String topic,Page page,Model model){
        User user =hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/notice/detail/"+topic);
        page.setRows(messageService.findNoticeCount(user.getId(),topic));
        List<Message> notices = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String,Object>> noticesList=new ArrayList<>();
        if(notices!=null){
            for(Message notice:notices){
                Map<String,Object> map=new HashMap<>();
                map.put("notice",notice);
                //解析内容
                String content=HtmlUtils.htmlUnescape(notice.getContent());
                Map<String,Object> data =JSONObject.parseObject(content,HashMap.class);
                map.put("user",userService.findUserById((Integer) data.get("userId")));
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));
                //通知作者
                map.put("fromUser",userService.findUserById(notice.getFromId()));
                noticesList.add(map);
            }
        }
        model.addAttribute("notices",noticesList);
        //设置已读
        List<Integer> ids = getLetterIds(notices);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";
    }
}
