package com.aistudy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志实体类
 * 对应数据库 login_log 表
 */
@Data
@TableName("login_log")
public class LoginLog {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String userType;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime loginTime;
}
