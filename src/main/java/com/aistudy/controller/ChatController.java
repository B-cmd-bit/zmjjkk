package com.aistudy.controller;

import com.aistudy.common.Result;
import com.aistudy.dto.ChatDTO;
import com.aistudy.entity.ChatHistory;
import com.aistudy.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI聊天控制器
 * 处理聊天消息的发送、历史查询、删除等操作
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 发送消息获取AI回复
     */
    @PostMapping("/send")
    public Result<Map<String, Object>> sendMessage(
            @RequestAttribute("userId") Integer userId,
            @RequestBody ChatDTO dto) {
        Map<String, Object> result = chatService.sendMessage(userId, dto.getMessage());
        return Result.success("获取回复成功", result);
    }

    /**
     * 获取聊天历史
     */
    @GetMapping("/history")
    public Result<List<ChatHistory>> getHistory(
            @RequestAttribute("userId") Integer userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        List<ChatHistory> list = chatService.getHistory(userId, page, pageSize);
        return Result.success(list);
    }

    /**
     * 重新生成回复
     */
    @PostMapping("/regenerate/{id}")
    public Result<ChatHistory> regenerate(
            @RequestAttribute("userId") Integer userId,
            @PathVariable("id") Integer chatId) {
        ChatHistory chat = chatService.regenerate(userId, chatId);
        return Result.success("重新生成成功", chat);
    }

    /**
     * 删除单条聊天记录
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteChat(
            @RequestAttribute("userId") Integer userId,
            @PathVariable("id") Integer chatId) {
        chatService.deleteChat(userId, chatId);
        return Result.success("删除成功");
    }

    /**
     * 删除全部聊天记录
     */
    @DeleteMapping("/all")
    public Result<Void> deleteAllChats(@RequestAttribute("userId") Integer userId) {
        chatService.deleteAllChats(userId);
        return Result.success("全部删除成功");
    }
}
