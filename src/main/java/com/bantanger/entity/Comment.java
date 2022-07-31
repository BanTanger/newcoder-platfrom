package com.bantanger.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.soap.Text;
import java.util.Date;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/7/31 18:51
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Comment {
    private int id;
    private int userId;
    private int entityType;
    private int entityId;
    private int targetId;
    private String content;
    private int status;
    private Date createTime;
}
