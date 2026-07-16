package com.aistudy.service;

/**
 * 代码解释服务接口
 */
public interface CodeService {
    String explainCode(String code);
    String optimizeCode(String code);
    String addComments(String code);
}
