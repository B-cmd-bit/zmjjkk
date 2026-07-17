package com.aistudy.service.impl;

import com.aistudy.service.AIService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI服务实现类（基于DeepSeek API）
 * 支持通过配置切换DeepSeek/OpenAI/Ollama
 */
@Service
public class AIServiceImpl implements AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIServiceImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ai.deepseek.api-key}")
    private String apiKey;

    @Value("${ai.deepseek.api-base}")
    private String apiBase;

    @Value("${ai.deepseek.model}")
    private String model;

    /**
     * 调用AI API的通用方法
     */
    private String callAI(String systemPrompt, String userPrompt) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = apiBase + "/chat/completions";
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            httpPost.setHeader("Content-Type", "application/json");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);

            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userPrompt);
            messages.add(userMsg);

            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 4096);
            requestBody.put("temperature", 0.7);

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                if (response.getCode() != 200) {
                    logger.error("AI API返回错误，状态码: {}, 响应: {}", response.getCode(), responseBody);
                    throw new RuntimeException("AI服务调用失败，状态码: " + response.getCode());
                }
                JsonNode root = objectMapper.readTree(responseBody);
                JsonNode choices = root.path("choices");
                if (choices.isArray() && choices.size() > 0) {
                    return choices.get(0).path("message").path("content").asText("");
                }
                return "AI未返回有效内容，请重试。";
            }
        } catch (Exception e) {
            logger.error("调用AI API异常: {}", e.getMessage(), e);
            throw new RuntimeException("AI服务调用异常: " + e.getMessage());
        }
    }

    @Override
    public String chat(String message) {
        String systemPrompt = "你是一个专业的AI校园学习助手，帮助学生解决学习中的问题。请用中文回答，内容专业、清晰、有条理。如果涉及代码，请使用Markdown代码块格式。";
        return callAI(systemPrompt, message);
    }

    @Override
    public String generateArticle(String title, String requirements, String articleType) {
        String systemPrompt = "你是一个专业的文章写作助手。请根据用户提供的标题、要求和文章类型，生成一篇结构完整、内容丰富的文章。使用Markdown格式，包含标题、分段、重点内容加粗等。";
        String userPrompt = String.format("请生成一篇%s。\n标题：%s\n要求：%s", articleType, title, requirements);
        return callAI(systemPrompt, userPrompt);
    }

    @Override
    public String generatePlan(String major, String course, String goal) {
        String systemPrompt = "你是一个专业的学习规划师。请根据学生的专业、课程和学习目标，制定一份详细的学习计划。包含阶段划分、每周任务、学习资源和检验标准。使用Markdown格式。";
        String userPrompt = String.format("请为以下学生制定学习计划：\n专业：%s\n课程：%s\n学习目标：%s", major, course, goal);
        return callAI(systemPrompt, userPrompt);
    }

    @Override
    public String explainCode(String code) {
        String systemPrompt = "你是一个专业的编程教师。请逐行解释以下代码的功能和原理，使用中文，尽量详细，让初学者也能够理解。使用Markdown格式。";
        return callAI(systemPrompt, "请解释以下代码：\n```\n" + code + "\n```");
    }

    @Override
    public String optimizeCode(String code) {
        String systemPrompt = "你是一个代码优化专家。请分析以下代码并提供优化建议和改进后的代码。说明优化前后的区别和性能提升。使用Markdown格式。";
        return callAI(systemPrompt, "请优化以下代码：\n```\n" + code + "\n```");
    }

    @Override
    public String addComments(String code) {
        String systemPrompt = "你是一个代码文档专家。请为以下代码添加详细的中文注释，包括函数说明、参数说明、逻辑解释等。保持原代码不变，仅添加注释。";
        return callAI(systemPrompt, "请为以下代码添加详细注释：\n```\n" + code + "\n```");
    }
}
