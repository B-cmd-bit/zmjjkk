package com.aistudy.controller;

import com.aistudy.common.Result;
import com.aistudy.dto.CodeDTO;
import com.aistudy.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * AI代码解释控制器
 * 处理代码的解释、优化和注释生成
 */
@RestController
@RequestMapping("/api/code")
public class CodeController {

    @Autowired
    private CodeService codeService;

    /**
     * 解释代码
     */
    @PostMapping("/explain")
    public Result<String> explainCode(@RequestBody CodeDTO dto) {
        String result = codeService.explainCode(dto.getCode());
        return Result.success("代码解释完成", result);
    }

    /**
     * 优化代码
     */
    @PostMapping("/optimize")
    public Result<String> optimizeCode(@RequestBody CodeDTO dto) {
        String result = codeService.optimizeCode(dto.getCode());
        return Result.success("代码优化完成", result);
    }

    /**
     * 生成注释
     */
    @PostMapping("/comment")
    public Result<String> addComments(@RequestBody CodeDTO dto) {
        String result = codeService.addComments(dto.getCode());
        return Result.success("注释生成完成", result);
    }
}
