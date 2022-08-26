package com.bantanger.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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
@Document(indexName = "discusspost", type = "_doc", shards = 6, replicas = 3)
public class DiscussPost {

    @Id
    private int id;

    @Field(type = FieldType.Integer)
    private int userId;

    /**
     * analyzer = "ik_max_word" 存储分词器，拆分出尽可能多的词
     * searchAnalyzer = "ik_smart" 查询分词器，拆分可能需要的词
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Integer)
    private int type;

    @Field(type = FieldType.Integer)
    private int status;

    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Integer)
    private int commentCount;

    @Field(type = FieldType.Double)
    private double score; /*一个帖子的热度评级*/

}
