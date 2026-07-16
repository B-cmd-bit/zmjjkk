package com.aistudy.mapper;

import com.aistudy.entity.LoginLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 登录日志Mapper接口
 */
@Mapper
public interface LogMapper extends BaseMapper<LoginLog> {
    @Select("SELECT * FROM login_log WHERE user_id = #{userId} AND user_type = #{userType} ORDER BY login_time DESC")
    List<LoginLog> findByUserId(@Param("userId") Integer userId, @Param("userType") String userType);

    @Select("SELECT * FROM login_log ORDER BY login_time DESC LIMIT #{limit}")
    List<LoginLog> findRecent(@Param("limit") Integer limit);
}
