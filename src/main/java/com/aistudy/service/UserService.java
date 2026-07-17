package com.aistudy.service;

import com.aistudy.entity.User;
import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {
    User register(String username, String email, String password, String confirmPassword);
    Map<String, Object> login(String username, String password, String ipAddress, String userAgent);
    User getCurrentUser(Integer userId);
    User updateProfile(Integer userId, String nickname);
    void changePassword(Integer userId, String oldPassword, String newPassword);
    String uploadAvatar(Integer userId, String uploadDir);
    List<Map<String, Object>> getLoginLogs(Integer userId);
    List<User> listAllUsers(int page, int pageSize);
    long countTotalUsers();
    void updateUserStatus(Integer userId, Integer status);
    void deleteUser(Integer userId);
}
