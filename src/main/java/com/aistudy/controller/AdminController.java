package com.aistudy.controller;

import com.aistudy.common.Result;
import com.aistudy.dto.LoginDTO;
import com.aistudy.entity.*;
import com.aistudy.mapper.AdminMapper;
import com.aistudy.service.*;
import com.aistudy.util.JwtUtil;
import com.aistudy.util.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 管理员控制器
 * 处理管理员登录、数据统计、用户管理等功能
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private PlanService planService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto, HttpServletRequest request) {
        Admin admin = adminMapper.findByUsername(dto.getUsername());
        if (admin == null) {
            return Result.error("管理员账号不存在");
        }
        if (!PasswordUtil.matches(dto.getPassword(), admin.getPasswordHash())) {
            return Result.error("密码错误");
        }
        if (admin.getStatus() == 0) {
            return Result.error("管理员账户已被禁用");
        }
        // 更新最后登录时间
        admin.setLastLogin(LocalDateTime.now());
        adminMapper.updateById(admin);
        // 生成Token
        String token = jwtUtil.generateToken(admin.getId(), "admin");
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("adminId", admin.getId());
        data.put("username", admin.getUsername());
        data.put("nickname", admin.getNickname());
        return Result.success("登录成功", data);
    }

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userService.countTotalUsers());
        data.put("totalChats", chatService.countTotalChats());
        data.put("totalArticles", articleService.countTotalArticles());
        data.put("totalPlans", planService.countTotalPlans());
        return Result.success(data);
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/users")
    public Result<List<User>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        List<User> users = userService.listAllUsers(page, pageSize);
        return Result.success(users);
    }

    /**
     * 修改用户状态
     */
    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(
            @PathVariable("id") Integer userId,
            @RequestBody Map<String, Integer> body) {
        userService.updateUserStatus(userId, body.get("status"));
        return Result.success("状态更新成功");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable("id") Integer userId) {
        userService.deleteUser(userId);
        return Result.success("用户删除成功");
    }

    /**
     * 获取聊天记录列表
     */
    @GetMapping("/chats")
    public Result<List<ChatHistory>> getChats(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        List<ChatHistory> chats = chatService.listAllChats(page, pageSize);
        return Result.success(chats);
    }

    /**
     * 删除聊天记录
     */
    @DeleteMapping("/chats/{id}")
    public Result<Void> deleteChat(@PathVariable("id") Integer chatId) {
        chatService.deleteChatByAdmin(chatId);
        return Result.success("聊天记录删除成功");
    }

    /**
     * 获取文章列表
     */
    @GetMapping("/articles")
    public Result<List<ArticleHistory>> getArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        List<ArticleHistory> articles = articleService.listAllArticles(page, pageSize);
        return Result.success(articles);
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/articles/{id}")
    public Result<Void> deleteArticle(@PathVariable("id") Integer articleId) {
        articleService.deleteArticleByAdmin(articleId);
        return Result.success("文章删除成功");
    }

    /**
     * 获取学习计划列表
     */
    @GetMapping("/plans")
    public Result<List<StudyPlan>> getPlans(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        List<StudyPlan> plans = planService.listAllPlans(page, pageSize);
        return Result.success(plans);
    }

    /**
     * 删除学习计划
     */
    @DeleteMapping("/plans/{id}")
    public Result<Void> deletePlan(@PathVariable("id") Integer planId) {
        planService.deletePlanByAdmin(planId);
        return Result.success("学习计划删除成功");
    }
}
