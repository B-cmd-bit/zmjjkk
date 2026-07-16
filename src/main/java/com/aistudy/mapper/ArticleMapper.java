package com.aistudy.mapper;

import com.aistudy.entity.ArticleHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文章Mapper接口
 */
@Mapper
public interface ArticleMapper extends BaseMapper<ArticleHistory> {
    @Select("SELECT * FROM article_history WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<ArticleHistory> findByUserId(@Param("userId") Integer userId);

    @Select("SELECT COUNT(*) FROM article_history WHERE DATE(created_at) = CURDATE()")
    Long countTodayArticles();
}
