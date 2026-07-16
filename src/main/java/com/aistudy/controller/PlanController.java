package com.aistudy.controller;

import com.aistudy.common.Result;
import com.aistudy.dto.PlanDTO;
import com.aistudy.entity.StudyPlan;
import com.aistudy.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI学习计划控制器
 * 处理学习计划的生成、查询、删除等操作
 */
@RestController
@RequestMapping("/api/plan")
public class PlanController {

    @Autowired
    private PlanService planService;

    /**
     * 生成学习计划
     */
    @PostMapping("/generate")
    public Result<Map<String, Object>> generatePlan(
            @RequestAttribute("userId") Integer userId,
            @RequestBody PlanDTO dto) {
        Map<String, Object> result = planService.generatePlan(
                userId, dto.getMajor(), dto.getCourse(), dto.getGoal()
        );
        return Result.success("学习计划生成成功", result);
    }

    /**
     * 获取学习计划列表
     */
    @GetMapping("/list")
    public Result<List<StudyPlan>> getList(
            @RequestAttribute("userId") Integer userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<StudyPlan> list = planService.getList(userId, page, pageSize);
        return Result.success(list);
    }

    /**
     * 获取学习计划详情
     */
    @GetMapping("/{id}")
    public Result<StudyPlan> getDetail(
            @RequestAttribute("userId") Integer userId,
            @PathVariable("id") Integer planId) {
        StudyPlan plan = planService.getDetail(userId, planId);
        return Result.success(plan);
    }

    /**
     * 删除学习计划
     */
    @DeleteMapping("/{id}")
    public Result<Void> deletePlan(
            @RequestAttribute("userId") Integer userId,
            @PathVariable("id") Integer planId) {
        planService.deletePlan(userId, planId);
        return Result.success("删除成功");
    }
}
