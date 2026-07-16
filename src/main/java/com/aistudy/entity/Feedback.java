package com.aistudy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈实体类
 * 对应数据库 feedback 表
 */
@Data
@TableName("feedback")
public class Feedback {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String module;
    private String content;
    private Integer status;
    private LocalDateTime createdAt;
}
