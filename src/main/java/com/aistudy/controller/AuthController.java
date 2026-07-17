package com.aistudy.controller;

import com.aistudy.common.Result;
import com.aistudy.dto.LoginDTO;
import com.aistudy.dto.RegisterDTO;
import com.aistudy.entity.User;
import com.aistudy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * 处理用户注册、登录、退出等认证操作
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody RegisterDTO dto) {
        User user = userService.register(
                dto.getUsername(), dto.getEmail(),
                dto.getPassword(), dto.getConfirmPassword()
        );
        Map<String, Object> data = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail()
        );
        return Result.success("注册成功", data);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) userAgent = "";
        Map<String, Object> result = userService.login(
                dto.getUsername(), dto.getPassword(), ipAddress, userAgent
        );
        return Result.success("登录成功", result);
    }

    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public Result<Void> logout() {
        return Result.success("已退出登录");
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<Void> changePassword(
            @RequestAttribute("userId") Integer userId,
            @RequestBody Map<String, String> body) {
        userService.changePassword(userId, body.get("oldPassword"), body.get("newPassword"));
        return Result.success("密码修改成功");
    }

    /**
     * 更新个人资料
     */
    @PostMapping("/update-profile")
    public Result<User> updateProfile(
            @RequestAttribute("userId") Integer userId,
            @RequestBody Map<String, String> body) {
        User user = userService.updateProfile(userId, body.get("nickname"));
        return Result.success("资料更新成功", user);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current-user")
    public Result<User> getCurrentUser(@RequestAttribute("userId") Integer userId) {
        User user = userService.getCurrentUser(userId);
        return Result.success(user);
    }
}
