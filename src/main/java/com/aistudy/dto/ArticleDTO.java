package com.aistudy.dto;

import lombok.Data;

/**
 * 文章生成请求DTO
 */
@Data
public class ArticleDTO {
    private String title;
    private String requirements;
    private String articleType;
}
