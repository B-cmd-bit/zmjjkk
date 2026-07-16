package com.aistudy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 * 封装日志记录功能
 */
public class LogUtil {

    private static final Logger logger = LoggerFactory.getLogger("AIStudyAssistant");

    /**
     * 记录信息日志
     */
    public static void info(String message) {
        logger.info(message);
    }

    /**
     * 记录警告日志
     */
    public static void warn(String message) {
        logger.warn(message);
    }

    /**
     * 记录错误日志
     */
    public static void error(String message, Exception e) {
        logger.error(message, e);
    }

    /**
     * 记录调试日志
     */
    public static void debug(String message) {
        logger.debug(message);
    }
}
