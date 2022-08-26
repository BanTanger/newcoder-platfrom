package com.bantanger.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/8/24 18:28
 */

/**
 * 将系统通知包装成一个实体，提高扩展性
 */
public class Event {

    /*事件的类型*/
    private String topic;
    /*来源，事件触发者*/
    private int userId;
    /*操作的对象，实体类型*/
    private int entityType;
    /*实体对应的id*/
    private int entityId;
    /*实体的作者id*/
    private int entityUserId;
    /*特殊数据，自由数据，提高扩展性*/
    private Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
