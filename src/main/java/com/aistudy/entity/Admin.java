package com.aistudy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员实体类
 * 对应数据库 admin 表
 */
@Data
@TableName("admin")
public class Admin {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String passwordHash;
    private String nickname;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
