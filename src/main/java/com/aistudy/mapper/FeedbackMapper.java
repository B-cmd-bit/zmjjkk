package com.aistudy.mapper;

import com.aistudy.entity.Feedback;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 反馈Mapper接口
 */
@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {
}
