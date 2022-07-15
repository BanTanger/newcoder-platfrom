package com.bantanger.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/6/21 22:08
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DiscussPost {
    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score; /*一个帖子的热度评级*/
}
