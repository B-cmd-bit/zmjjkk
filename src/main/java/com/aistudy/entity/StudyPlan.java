package com.aistudy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习计划实体类
 * 对应数据库 study_plan 表
 */
@Data
@TableName("study_plan")
public class StudyPlan {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String major;
    private String course;
    private String goal;
    private String planContent;
    private LocalDateTime createdAt;
}
