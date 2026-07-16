package com.aistudy.controller;

import com.aistudy.common.Result;
import com.aistudy.entity.User;
import com.aistudy.service.UserService;
import com.aistudy.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 用户中心控制器
 * 处理个人信息管理、头像上传等操作
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${app.upload-path}")
    private String uploadPath;

    /**
     * 获取个人信息
     */
    @GetMapping("/profile")
    public Result<User> getProfile(@RequestAttribute("userId") Integer userId) {
        User user = userService.getCurrentUser(userId);
        return Result.success(user);
    }

    /**
     * 上传头像
     */
    @PostMapping("/upload-avatar")
    public Result<String> uploadAvatar(
            @RequestAttribute("userId") Integer userId,
            @RequestParam("file") MultipartFile file) {
        try {
            String avatarDir = uploadPath + "/avatars";
            String filename = FileUtil.saveFile(file, avatarDir);
            User user = userService.getCurrentUser(userId);
            user.setAvatar(filename);
            // 更新数据库中的头像路径
            userService.updateProfile(userId, user.getNickname());
            return Result.success("头像上传成功", "/uploads/avatars/" + filename);
        } catch (Exception e) {
            return Result.error("头像上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取登录日志
     */
    @GetMapping("/login-logs")
    public Result<List<Map<String, Object>>> getLoginLogs(@RequestAttribute("userId") Integer userId) {
        List<Map<String, Object>> logs = userService.getLoginLogs(userId);
        return Result.success(logs);
    }
}
