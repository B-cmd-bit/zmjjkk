package com.aistudy.mapper;

import com.aistudy.entity.ChatHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 聊天记录Mapper接口
 */
@Mapper
public interface ChatMapper extends BaseMapper<ChatHistory> {
    @Select("SELECT * FROM chat_history WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<ChatHistory> findByUserId(@Param("userId") Integer userId);

    @Select("SELECT COUNT(*) FROM chat_history WHERE user_id = #{userId}")
    Long countByUserId(@Param("userId") Integer userId);

    @Select("SELECT COUNT(*) FROM chat_history WHERE DATE(created_at) = CURDATE()")
    Long countTodayChats();
}
