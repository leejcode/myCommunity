package com.leej.community.entity;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
@Getter
@ToString
public class Event {
    private String topic;
    private int userId;//触发者
    private int entityType; //被触发类型
    private int entityId;
    private int entityUserId;//实体拥有者
    private Map<String,Object> data = new HashMap<>();//扩展使用

    public Event setTopic(String topic) { //链式编程使用
        this.topic = topic;
        return this;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Event setData(String key,Object value) {
        this.data.put(key,value);
        return this;
    }
}
