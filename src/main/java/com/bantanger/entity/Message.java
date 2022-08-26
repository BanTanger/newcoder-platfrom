package com.bantanger.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/8/20 13:00
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message {

    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    private int status;
    private Date createTime;

}
