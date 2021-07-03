package com.leej.community.service;

import com.leej.community.dao.MessageMapper;
import com.leej.community.entity.Message;
import com.leej.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;
    //查询当前用户的会话列表,针对每个会话返回最新的私信
    @Autowired
    private SensitiveFilter sensitiveFilter;
    public List<Message> selectConversations(int userId, int offset, int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }
    //查询会话数量
    public int selectConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }
    //查询某个对话包含的私信列表
    public List<Message> selectLetters(String conversationId,int offset,int limit){
        return messageMapper.selectLetters(conversationId,offset,limit);
    }
    //查询某个会话所包含的私信数量
    public int selectLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }
    //查询未读的私信数量
    public int selectLetterUnreadCount(int userId,String conversationId){
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }
    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);

    }
    //
    public int readMessage(List<Integer> ids){
//        Map<String,Object> map = new HashMap<>();
//        map.put("list",ids);
//        map.put("status",1);
        return messageMapper.updateStatus(ids,1);
    }
}
