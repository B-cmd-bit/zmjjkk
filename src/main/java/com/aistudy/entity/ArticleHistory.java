package com.aistudy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章历史实体类
 * 对应数据库 article_history 表
 */
@Data
@TableName("article_history")
public class ArticleHistory {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String title;
    private String content;
    private String articleType;
    private String requirements;
    private LocalDateTime createdAt;
}
