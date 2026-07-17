package com.aistudy.mapper;

import com.aistudy.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT COUNT(*) FROM users WHERE status = 1")
    Long countActiveUsers();

    @Select("SELECT COUNT(*) FROM users WHERE DATE(created_at) = CURDATE()")
    Long countTodayNewUsers();
}
