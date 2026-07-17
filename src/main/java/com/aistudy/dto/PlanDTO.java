package com.aistudy.dto;

import lombok.Data;

/**
 * 学习计划请求DTO
 */
@Data
public class PlanDTO {
    private String major;
    private String course;
    private String goal;
}
