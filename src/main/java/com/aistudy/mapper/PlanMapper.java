package com.aistudy.mapper;

import com.aistudy.entity.StudyPlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学习计划Mapper接口
 */
@Mapper
public interface PlanMapper extends BaseMapper<StudyPlan> {
    @Select("SELECT * FROM study_plan WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<StudyPlan> findByUserId(@Param("userId") Integer userId);

    @Select("SELECT COUNT(*) FROM study_plan WHERE DATE(created_at) = CURDATE()")
    Long countTodayPlans();
}
