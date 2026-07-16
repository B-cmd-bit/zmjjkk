package com.aistudy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天历史实体类
 * 对应数据库 chat_history 表
 */
@Data
@TableName("chat_history")
public class ChatHistory {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String userMessage;
    private String aiResponse;
    private LocalDateTime createdAt;
}
