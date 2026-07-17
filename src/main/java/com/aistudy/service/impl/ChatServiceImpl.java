package com.aistudy.service.impl;

import com.aistudy.entity.ChatHistory;
import com.aistudy.mapper.ChatMapper;
import com.aistudy.service.AIService;
import com.aistudy.service.ChatService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 聊天服务实现类
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private AIService aiService;

    @Override
    @Transactional
    public Map<String, Object> sendMessage(Integer userId, String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("消息不能为空");
        }
        // 调用AI获取回复
        String aiResponse = aiService.chat(message);
        // 保存聊天记录
        ChatHistory chat = new ChatHistory();
        chat.setUserId(userId);
        chat.setUserMessage(message.trim());
        chat.setAiResponse(aiResponse);
        chat.setCreatedAt(LocalDateTime.now());
        chatMapper.insert(chat);
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("id", chat.getId());
        result.put("userMessage", chat.getUserMessage());
        result.put("aiResponse", chat.getAiResponse());
        result.put("createdAt", chat.getCreatedAt().toString());
        return result;
    }

    @Override
    public List<ChatHistory> getHistory(Integer userId, int page, int pageSize) {
        Page<ChatHistory> p = new Page<>(page, pageSize);
        QueryWrapper<ChatHistory> query = new QueryWrapper<>();
        query.eq("user_id", userId).orderByDesc("created_at");
        IPage<ChatHistory> result = chatMapper.selectPage(p, query);
        return result.getRecords();
    }

    @Override
    @Transactional
    public ChatHistory regenerate(Integer userId, Integer chatId) {
        ChatHistory chat = chatMapper.selectById(chatId);
        if (chat == null || !Objects.equals(chat.getUserId(), userId)) {
            throw new IllegalArgumentException("聊天记录不存在");
        }
        // 重新调用AI
        String newResponse = aiService.chat(chat.getUserMessage());
        chat.setAiResponse(newResponse);
        chat.setCreatedAt(LocalDateTime.now());
        chatMapper.updateById(chat);
        return chat;
    }

    @Override
    @Transactional
    public void deleteChat(Integer userId, Integer chatId) {
        ChatHistory chat = chatMapper.selectById(chatId);
        if (chat == null || !Objects.equals(chat.getUserId(), userId)) {
            throw new IllegalArgumentException("聊天记录不存在");
        }
        chatMapper.deleteById(chatId);
    }

    @Override
    @Transactional
    public void deleteAllChats(Integer userId) {
        QueryWrapper<ChatHistory> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        chatMapper.delete(query);
    }

    @Override
    public List<ChatHistory> listAllChats(int page, int pageSize) {
        Page<ChatHistory> p = new Page<>(page, pageSize);
        QueryWrapper<ChatHistory> query = new QueryWrapper<>();
        query.orderByDesc("created_at");
        IPage<ChatHistory> result = chatMapper.selectPage(p, query);
        return result.getRecords();
    }

    @Override
    public long countTotalChats() {
        return chatMapper.selectCount(null);
    }

    @Override
    @Transactional
    public void deleteChatByAdmin(Integer chatId) {
        chatMapper.deleteById(chatId);
    }
}
