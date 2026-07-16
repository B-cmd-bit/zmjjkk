package com.aistudy.service;

/**
 * AI服务接口
 * 定义AI功能的统一行为，支持切换DeepSeek/OpenAI/Ollama
 */
public interface AIService {
    String chat(String message);
    String generateArticle(String title, String requirements, String articleType);
    String generatePlan(String major, String course, String goal);
    String explainCode(String code);
    String optimizeCode(String code);
    String addComments(String code);
}
