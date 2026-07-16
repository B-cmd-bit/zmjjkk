package com.aistudy.mapper;

import com.aistudy.entity.Admin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 管理员Mapper接口
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    @Select("SELECT * FROM admin WHERE username = #{username}")
    Admin findByUsername(@Param("username") String username);
}
