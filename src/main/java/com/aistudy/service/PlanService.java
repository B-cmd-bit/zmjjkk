package com.aistudy.service;

import com.aistudy.entity.StudyPlan;
import java.util.List;
import java.util.Map;

/**
 * 学习计划服务接口
 */
public interface PlanService {
    Map<String, Object> generatePlan(Integer userId, String major, String course, String goal);
    List<StudyPlan> getList(Integer userId, int page, int pageSize);
    StudyPlan getDetail(Integer userId, Integer planId);
    void deletePlan(Integer userId, Integer planId);
    List<StudyPlan> listAllPlans(int page, int pageSize);
    long countTotalPlans();
    void deletePlanByAdmin(Integer planId);
}
