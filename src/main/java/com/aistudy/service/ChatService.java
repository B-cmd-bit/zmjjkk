package com.aistudy.service;

import com.aistudy.entity.ChatHistory;
import java.util.List;
import java.util.Map;

/**
 * 聊天服务接口
 */
public interface ChatService {
    Map<String, Object> sendMessage(Integer userId, String message);
    List<ChatHistory> getHistory(Integer userId, int page, int pageSize);
    ChatHistory regenerate(Integer userId, Integer chatId);
    void deleteChat(Integer userId, Integer chatId);
    void deleteAllChats(Integer userId);
    List<ChatHistory> listAllChats(int page, int pageSize);
    long countTotalChats();
    void deleteChatByAdmin(Integer chatId);
}
