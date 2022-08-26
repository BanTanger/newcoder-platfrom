package com.bantanger.event;

import com.alibaba.fastjson.JSONObject;
import com.bantanger.controller.advice.ExceptionAdvice;
import com.bantanger.entity.DiscussPost;
import com.bantanger.entity.Event;
import com.bantanger.entity.Message;
import com.bantanger.service.DiscussPostService;
import com.bantanger.service.ElasticsearchService;
import com.bantanger.service.MessageService;
import com.bantanger.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/8/24 18:50
 */

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    /**
     * 消费者通道，订阅三种类型实体：评论，点赞，粉丝
     * 系统调用 MessageService 往订阅通道发送消息
     * @param record
     */
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空！");
            return ;
        }

        // 原始数据
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误！");
            return ;
        }

        // 发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID); // 发送消息的对象
        message.setToId(event.getEntityUserId()); // 接收消息的对象
        message.setConversationId(event.getTopic()); // 消息通道
        message.setCreateTime(new Date()); // 消息创建时间

        // 数据详细内容
        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if(!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    /**
     * 消费发帖事件
     * @param record
     */
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublicMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空！");
            return ;
        }

        // 原始数据
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误！");
            return ;
        }

        // 查询帖子
        DiscussPost discussPost = discussPostService.findDiscussPostById(event.getEntityId());
        // 存储到 es服务器上
        elasticsearchService.saveDiscussPost(discussPost);
    }
}
