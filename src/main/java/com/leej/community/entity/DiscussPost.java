package com.leej.community.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
@Getter
@Setter
@ToString
@Document(indexName ="discusspost",type="_doc",shards = 6,replicas = 3)//实体映射到es的数据库中
public class DiscussPost {
    @Id
    private Integer id;
    @Field(type = FieldType.Integer)
    private Integer userId;
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")//搜索字段需要解析器
    private String title;
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")//搜索字段需要解析器
    private String content;
    @Field(type = FieldType.Integer)
    private Integer type;
    @Field(type = FieldType.Integer)
    private Integer status;
    @Field(type = FieldType.Date)
    private Date createTime;
    @Field(type = FieldType.Integer)
    private Integer commentCount;
    @Field(type = FieldType.Double)
    private double score;
}
