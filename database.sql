-- ============================================================
-- AI校园学习助手系统 - 数据库脚本 (Java版)
-- 数据库名称：AIStudyAssistant
-- 数据库版本：MySQL 8.0+
-- 创建日期：2026-07-15
-- ============================================================

DROP DATABASE IF EXISTS AIStudyAssistant;

CREATE DATABASE AIStudyAssistant
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE AIStudyAssistant;

-- ============================================================
-- 1. 用户表 (users)
-- ============================================================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT 'BCrypt加密密码',
    nickname VARCHAR(50) DEFAULT '' COMMENT '昵称',
    avatar VARCHAR(255) DEFAULT 'default_avatar.png' COMMENT '头像路径',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常，0禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY idx_username (username),
    UNIQUE KEY idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================
-- 2. 管理员表 (admin)
-- ============================================================
CREATE TABLE admin (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '管理员ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password_hash VARCHAR(255) NOT NULL COMMENT '加密密码',
    nickname VARCHAR(50) DEFAULT '管理员' COMMENT '昵称',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常，0禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    last_login DATETIME DEFAULT NULL COMMENT '最后登录时间',
    UNIQUE KEY idx_admin_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- ============================================================
-- 3. 聊天历史表 (chat_history)
-- ============================================================
CREATE TABLE chat_history (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id INT NOT NULL COMMENT '用户ID',
    user_message TEXT NOT NULL COMMENT '用户消息',
    ai_response TEXT NOT NULL COMMENT 'AI回复',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_chat_user_id (user_id),
    INDEX idx_chat_created_at (created_at),
    CONSTRAINT fk_chat_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天历史表';

-- ============================================================
-- 4. 文章历史表 (article_history)
-- ============================================================
CREATE TABLE article_history (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '文章ID',
    user_id INT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) NOT NULL COMMENT '文章标题',
    content TEXT NOT NULL COMMENT '文章内容',
    article_type VARCHAR(50) NOT NULL COMMENT '文章类型',
    requirements TEXT COMMENT '写作要求',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_article_user_id (user_id),
    INDEX idx_article_type (article_type),
    CONSTRAINT fk_article_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章历史表';

-- ============================================================
-- 5. 学习计划表 (study_plan)
-- ============================================================
CREATE TABLE study_plan (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '计划ID',
    user_id INT NOT NULL COMMENT '用户ID',
    major VARCHAR(100) NOT NULL COMMENT '专业',
    course VARCHAR(200) NOT NULL COMMENT '课程',
    goal TEXT NOT NULL COMMENT '学习目标',
    plan_content TEXT NOT NULL COMMENT '计划内容',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_plan_user_id (user_id),
    CONSTRAINT fk_plan_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习计划表';

-- ============================================================
-- 6. 登录日志表 (login_log)
-- ============================================================
CREATE TABLE login_log (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id INT NOT NULL COMMENT '用户/管理员ID',
    user_type VARCHAR(20) NOT NULL COMMENT '类型：user/admin',
    ip_address VARCHAR(50) DEFAULT '' COMMENT 'IP地址',
    user_agent VARCHAR(500) DEFAULT '' COMMENT '设备信息',
    login_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    INDEX idx_log_user_id (user_id),
    INDEX idx_login_time (login_time),
    INDEX idx_user_type (user_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- ============================================================
-- 7. 反馈表 (feedback)
-- ============================================================
CREATE TABLE feedback (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '反馈ID',
    user_id INT NOT NULL COMMENT '用户ID',
    module VARCHAR(50) NOT NULL COMMENT '反馈模块',
    content TEXT NOT NULL COMMENT '反馈内容',
    status TINYINT DEFAULT 0 COMMENT '状态：0待处理，1已处理',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_feedback_user_id (user_id),
    INDEX idx_feedback_status (status),
    CONSTRAINT fk_feedback_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈表';

-- ============================================================
-- 插入测试数据
-- ============================================================

-- 管理员（密码：admin123，BCrypt加密）
INSERT INTO admin (username, password_hash, nickname, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 1);

-- 测试用户（密码：test123456，BCrypt加密）
INSERT INTO users (username, email, password_hash, nickname, avatar, status) VALUES
('zhangsan', 'zhangsan@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三', 'default_avatar.png', 1),
('lisi', 'lisi@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四', 'default_avatar.png', 1),
('wangwu', 'wangwu@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王五', 'default_avatar.png', 1);

-- 测试聊天记录
INSERT INTO chat_history (user_id, user_message, ai_response) VALUES
(1, '什么是Java的Spring Boot框架？', 'Spring Boot是基于Spring框架的快速开发脚手架，它简化了Spring应用的初始搭建和开发过程。主要特点包括：\n\n1. **自动配置**：根据依赖自动配置Spring应用\n2. **起步依赖**：一站式引入相关依赖\n3. **内嵌服务器**：内置Tomcat、Jetty等\n4. **Actuator**：生产级监控端点\n\n```java\n@SpringBootApplication\npublic class Application {\n    public static void main(String[] args) {\n        SpringApplication.run(Application.class, args);\n    }\n}\n```'),
(1, '如何理解Java多态？', '多态是面向对象编程的三大特性之一，指同一个方法调用在不同对象上有不同的行为。Java多态分为两种：\n\n**编译时多态（重载）**：\n```java\npublic class Calculator {\n    public int add(int a, int b) { return a + b; }\n    public double add(double a, double b) { return a + b; }\n}\n```\n\n**运行时多态（重写）**：\n```java\nAnimal animal = new Dog();\nanimal.makeSound(); // 调用Dog的方法\n```'),
(2, '请介绍一下MySQL索引优化', 'MySQL索引优化是数据库性能优化的核心。以下是关键要点：\n\n1. **最左前缀原则**：联合索引从最左边列开始匹配\n2. **覆盖索引**：查询列全部在索引中，避免回表\n3. **避免索引失效**：不要在索引列上使用函数\n4. **EXPLAIN分析**：使用EXPLAIN查看执行计划\n\n```sql\n-- 创建合适的联合索引\nCREATE INDEX idx_name_age ON users(name, age);\n\n-- 使用EXPLAIN分析查询\nEXPLAIN SELECT * FROM users WHERE name = ''Zhang'';\n```');

-- 测试文章
INSERT INTO article_history (user_id, title, content, article_type, requirements) VALUES
(1, 'Java在Web开发中的优势', '# Java在Web开发中的优势\n\nJava作为企业级开发的首选语言，在Web开发领域拥有显著优势。\n\n## 成熟稳定的生态\n\nSpring框架体系提供了完整的Web开发解决方案，从Spring MVC到Spring Boot，再到Spring Cloud微服务。\n\n## 高性能与可扩展性\n\nJVM的JIT编译技术和成熟的GC机制保证了Java应用的高性能运行。\n\n## 安全性\n\nJava的安全管理器、类加载机制和字节码验证为企业应用提供了多层次的安全保障。', '议论文', '论述Java在Web开发中的优势，800字左右'),
(2, '我的编程学习之路', '# 我的编程学习之路\n\n从零基础到能够独立开发Web应用，这是一段充满挑战和收获的旅程。\n\n## 起点\n\n大一时第一次接触到C语言，那些神秘的符号和语法让我既困惑又着迷。\n\n## 成长\n\n通过不断练习和实践，我逐渐掌握了Java、Spring Boot等技术，也参加了多个竞赛项目。', '记叙文', '描述编程学习经历，600字左右');

-- 测试学习计划
INSERT INTO study_plan (user_id, major, course, goal, plan_content) VALUES
(1, '软件工程', 'Spring Boot框架', '掌握Spring Boot开发，能够独立完成Web项目', '# Spring Boot学习计划\n\n## 第一周：基础入门\n- Spring Boot核心概念\n- 自动配置原理\n- 第一个RESTful API\n\n## 第二周：数据访问\n- MyBatis-Plus集成\n- 事务管理\n- 数据校验\n\n## 第三周：进阶特性\n- 安全认证（Spring Security/JWT）\n- 文件上传下载\n- 全局异常处理'),
(2, '计算机科学', 'Java高级编程', '深入理解Java高级特性，为毕业后工作做准备', '# Java高级编程学习计划\n\n## 第一阶段：JVM深入（2周）\n- 内存模型与GC\n- 类加载机制\n- JVM调优\n\n## 第二阶段：并发编程（2周）\n- 线程与线程池\n- synchronized与Lock\n- JUC工具类\n\n## 第三阶段：框架源码（2周）\n- Spring IoC源码\n- Spring AOP源码\n- MyBatis源码');

-- 测试登录日志
INSERT INTO login_log (user_id, user_type, ip_address, user_agent, login_time) VALUES
(1, 'user', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0', '2026-07-14 09:30:00'),
(1, 'user', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0', '2026-07-14 14:20:00'),
(2, 'user', '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Firefox/121.0', '2026-07-14 10:15:00'),
(1, 'admin', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0', '2026-07-14 08:00:00');

-- 测试反馈
INSERT INTO feedback (user_id, module, content, status) VALUES
(1, 'AI聊天', 'AI回答速度很快，但有时候精度不够，希望能提高回答质量。', 0),
(2, '作文生成', '生成的文章内容很丰富，但字数和格式有时不太一致。', 1);
