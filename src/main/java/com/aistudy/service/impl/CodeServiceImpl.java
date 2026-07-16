package com.aistudy.service.impl;

import com.aistudy.service.AIService;
import com.aistudy.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 代码解释服务实现类
 */
@Service
public class CodeServiceImpl implements CodeService {

    @Autowired
    private AIService aiService;

    @Override
    public String explainCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("代码不能为空");
        }
        return aiService.explainCode(code.trim());
    }

    @Override
    public String optimizeCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("代码不能为空");
        }
        return aiService.optimizeCode(code.trim());
    }

    @Override
    public String addComments(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("代码不能为空");
        }
        return aiService.addComments(code.trim());
    }
}
