package com.bantanger.event;

import com.alibaba.fastjson.JSONObject;
import com.bantanger.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/8/24 18:35
 */

@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    // 处理事件
    public void fireEvent(Event event) {
        // 将事件发送到指定主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }

}
