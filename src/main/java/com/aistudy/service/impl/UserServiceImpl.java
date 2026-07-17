package com.aistudy.service.impl;

import com.aistudy.entity.LoginLog;
import com.aistudy.entity.User;
import com.aistudy.mapper.LogMapper;
import com.aistudy.mapper.UserMapper;
import com.aistudy.service.UserService;
import com.aistudy.util.JwtUtil;
import com.aistudy.util.PasswordUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public User register(String username, String email, String password, String confirmPassword) {
        if (!Objects.equals(password, confirmPassword)) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("密码长度不能少于6位");
        }
        if (username.length() < 3 || username.length() > 20) {
            throw new IllegalArgumentException("用户名长度应在3-20个字符之间");
        }
        // 检查用户名是否已存在
        QueryWrapper<User> userQuery = new QueryWrapper<>();
        userQuery.eq("username", username);
        if (userMapper.selectOne(userQuery) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        // 检查邮箱是否已存在
        QueryWrapper<User> emailQuery = new QueryWrapper<>();
        emailQuery.eq("email", email);
        if (userMapper.selectOne(emailQuery) != null) {
            throw new IllegalArgumentException("邮箱已被注册");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(PasswordUtil.encode(password));
        user.setNickname(username);
        user.setAvatar("default_avatar.png");
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }

    @Override
    @Transactional
    public Map<String, Object> login(String username, String password, String ipAddress, String userAgent) {
        // 支持用户名或邮箱登录
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", username).or().eq("email", username);
        User user = userMapper.selectOne(query);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (!PasswordUtil.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("密码错误");
        }
        if (user.getStatus() == 0) {
            throw new IllegalArgumentException("账户已被禁用，请联系管理员");
        }
        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), "user");
        // 记录登录日志
        LoginLog log = new LoginLog();
        log.setUserId(user.getId());
        log.setUserType("user");
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setLoginTime(LocalDateTime.now());
        logMapper.insert(log);
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", buildUserInfo(user));
        return result;
    }

    @Override
    public User getCurrentUser(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return user;
    }

    @Override
    public User updateProfile(Integer userId, String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("昵称不能为空");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        user.setNickname(nickname.trim());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return user;
    }

    @Override
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("新密码长度不能少于6位");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (!PasswordUtil.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("原密码错误");
        }
        user.setPasswordHash(PasswordUtil.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public String uploadAvatar(Integer userId, String uploadDir) {
        // 头像更新由Controller通过MultipartFile处理
        // 此方法留作扩展
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return user.getAvatar();
    }

    @Override
    public List<Map<String, Object>> getLoginLogs(Integer userId) {
        List<LoginLog> logs = logMapper.findByUserId(userId, "user");
        List<Map<String, Object>> result = new ArrayList<>();
        for (LoginLog log : logs) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", log.getId());
            item.put("ipAddress", log.getIpAddress());
            item.put("userAgent", log.getUserAgent());
            item.put("loginTime", log.getLoginTime() != null ? log.getLoginTime().toString() : "");
            result.add(item);
        }
        return result;
    }

    @Override
    public List<User> listAllUsers(int page, int pageSize) {
        Page<User> p = new Page<>(page, pageSize);
        IPage<User> result = userMapper.selectPage(p, new QueryWrapper<User>().orderByDesc("created_at"));
        return result.getRecords();
    }

    @Override
    public long countTotalUsers() {
        return userMapper.selectCount(null);
    }

    @Override
    public void updateUserStatus(Integer userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        userMapper.deleteById(userId);
    }

    /**
     * 构建返回给前端的用户信息（脱敏）
     */
    private Map<String, Object> buildUserInfo(User user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("email", user.getEmail());
        info.put("nickname", user.getNickname());
        info.put("avatar", user.getAvatar());
        info.put("status", user.getStatus());
        return info;
    }
}
