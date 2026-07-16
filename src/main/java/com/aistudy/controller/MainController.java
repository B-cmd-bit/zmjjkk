package com.aistudy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面路由控制器
 * 处理所有HTML页面的导航
 */
@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "main/index";
    }

    @GetMapping("/auth/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/auth/register")
    public String registerPage() {
        return "auth/register";
    }

    @GetMapping("/chat")
    public String chatPage() {
        return "chat/index";
    }

    @GetMapping("/article")
    public String articlePage() {
        return "article/index";
    }

    @GetMapping("/plan")
    public String planPage() {
        return "plan/index";
    }

    @GetMapping("/code")
    public String codePage() {
        return "code/index";
    }

    @GetMapping("/user/profile")
    public String profilePage() {
        return "user/profile";
    }

    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin/login";
    }

    @GetMapping("/admin")
    public String adminIndex() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String adminUsers() {
        return "admin/users";
    }

    @GetMapping("/admin/chats")
    public String adminChats() {
        return "admin/chats";
    }

    @GetMapping("/admin/articles")
    public String adminArticles() {
        return "admin/articles";
    }

    @GetMapping("/admin/plans")
    public String adminPlans() {
        return "admin/plans";
    }

    @GetMapping("/admin/logs")
    public String adminLogs() {
        return "admin/logs";
    }
}
