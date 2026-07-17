package com.aistudy.service.impl;

import com.aistudy.entity.StudyPlan;
import com.aistudy.mapper.PlanMapper;
import com.aistudy.service.AIService;
import com.aistudy.service.PlanService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 学习计划服务实现类
 */
@Service
public class PlanServiceImpl implements PlanService {

    @Autowired
    private PlanMapper planMapper;

    @Autowired
    private AIService aiService;

    @Override
    @Transactional
    public Map<String, Object> generatePlan(Integer userId, String major, String course, String goal) {
        if (major == null || major.trim().isEmpty()) {
            throw new IllegalArgumentException("专业不能为空");
        }
        if (course == null || course.trim().isEmpty()) {
            throw new IllegalArgumentException("课程不能为空");
        }
        // 调用AI生成学习计划
        String planContent = aiService.generatePlan(major, course, goal);
        // 保存到数据库
        StudyPlan plan = new StudyPlan();
        plan.setUserId(userId);
        plan.setMajor(major.trim());
        plan.setCourse(course.trim());
        plan.setGoal(goal != null ? goal : "掌握" + course + "的核心知识和技能");
        plan.setPlanContent(planContent);
        plan.setCreatedAt(LocalDateTime.now());
        planMapper.insert(plan);
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("id", plan.getId());
        result.put("major", plan.getMajor());
        result.put("course", plan.getCourse());
        result.put("goal", plan.getGoal());
        result.put("planContent", plan.getPlanContent());
        result.put("createdAt", plan.getCreatedAt().toString());
        return result;
    }

    @Override
    public List<StudyPlan> getList(Integer userId, int page, int pageSize) {
        Page<StudyPlan> p = new Page<>(page, pageSize);
        QueryWrapper<StudyPlan> query = new QueryWrapper<>();
        query.eq("user_id", userId).orderByDesc("created_at");
        IPage<StudyPlan> result = planMapper.selectPage(p, query);
        return result.getRecords();
    }

    @Override
    public StudyPlan getDetail(Integer userId, Integer planId) {
        StudyPlan plan = planMapper.selectById(planId);
        if (plan == null || !Objects.equals(plan.getUserId(), userId)) {
            throw new IllegalArgumentException("学习计划不存在");
        }
        return plan;
    }

    @Override
    @Transactional
    public void deletePlan(Integer userId, Integer planId) {
        StudyPlan plan = planMapper.selectById(planId);
        if (plan == null || !Objects.equals(plan.getUserId(), userId)) {
            throw new IllegalArgumentException("学习计划不存在");
        }
        planMapper.deleteById(planId);
    }

    @Override
    public List<StudyPlan> listAllPlans(int page, int pageSize) {
        Page<StudyPlan> p = new Page<>(page, pageSize);
        QueryWrapper<StudyPlan> query = new QueryWrapper<>();
        query.orderByDesc("created_at");
        IPage<StudyPlan> result = planMapper.selectPage(p, query);
        return result.getRecords();
    }

    @Override
    public long countTotalPlans() {
        return planMapper.selectCount(null);
    }

    @Override
    @Transactional
    public void deletePlanByAdmin(Integer planId) {
        planMapper.deleteById(planId);
    }
}
