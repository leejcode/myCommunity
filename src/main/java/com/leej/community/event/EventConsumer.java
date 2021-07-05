package com.leej.community.event;

import com.alibaba.fastjson.JSONObject;
import com.leej.community.entity.DiscussPost;
import com.leej.community.entity.Event;
import com.leej.community.entity.Message;
import com.leej.community.service.DiscussPostService;
import com.leej.community.service.ElasticsearchService;
import com.leej.community.service.MessageService;
import com.leej.community.utils.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Controller
public class EventConsumer implements CommunityConstant {
    private static final Logger logger=LoggerFactory.getLogger(EventConsumer.class);
    @Autowired
    private MessageService messageService;//用于发送系统消息
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record){
        if(record==null||record.value()==null){
            logger.error("消息内容为空");
            return;
        }
        Event event= JSONObject.parseObject(record.value().toString(),Event.class);
        if(event==null){
            logger.error("消息格式错误");
            return;
        }
        //发送站内通知
        Message message =new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setStatus(0);//默认0状态，正常状态
        message.setCreateTime(new Date());
        Map<String,Object> content = new HashMap<>();
        content.put("userId",event.getUserId());//触发者
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId()); //链接到对应的实体
        if(!event.getData().isEmpty()){//其他信息传入
            for(Map.Entry<String,Object> entry:event.getData().entrySet()){
                content.put(entry.getKey(),entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record){
        if(record==null||record.value()==null){
            logger.error("消息内容为空");
            return;
        }
        Event event= JSONObject.parseObject(record.value().toString(),Event.class);
        if(event==null){
            logger.error("消息格式错误");
            return;
        }
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);
    }
}
