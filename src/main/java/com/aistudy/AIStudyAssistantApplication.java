package com.aistudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI校园学习助手系统 - 启动类
 *
 * @SpringBootApplication 是一个组合注解，包含：
 * - @SpringBootConfiguration：标识配置类
 * - @EnableAutoConfiguration：启用自动配置
 * - @ComponentScan：自动扫描组件
 */
@SpringBootApplication
public class AIStudyAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(AIStudyAssistantApplication.class, args);
    }
}
