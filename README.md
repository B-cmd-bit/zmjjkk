# AI校园学习助手系统（Java版）

## 项目介绍

AI校园学习助手系统是一个基于Spring Boot 3.2框架开发的智能学习辅助平台。系统集成了DeepSeek大语言模型API，为大学生提供AI智能聊天、作文生成、学习计划制定、代码解释等功能，帮助学生提高学习效率，实现个性化学习。

本系统是"课程综合实训"项目，采用标准的MVC三层架构，前端使用Bootstrap 5响应式布局，后端使用Spring Boot + MyBatis-Plus + MySQL 8，认证采用JWT Token方案。

## 技术栈

| 分层 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.2 |
| ORM框架 | MyBatis-Plus 3.5.5 |
| 数据库 | MySQL 8.0 |
| 模板引擎 | Thymeleaf |
| 前端框架 | Bootstrap 5.3 |
| 认证方案 | JWT (JSON Web Token) |
| AI接口 | DeepSeek API（兼容OpenAI格式） |
| 构建工具 | Maven |
| JDK版本 | Java 17 |

## 功能模块

- **用户系统**：注册、登录(JWT)、退出、修改密码、修改头像、修改昵称、登录日志
- **AI聊天**：智能对话、Markdown渲染、代码高亮、保存/删除聊天记录、重新生成
- **AI作文生成**：输入标题和要点、选择文章类型、AI生成文章、导出Markdown/Word
- **AI学习计划**：输入专业/课程/目标、AI生成详细学习计划、保存和查看历史
- **AI代码解释**：代码逐行解释、代码优化建议、自动生成注释
- **管理员后台**：用户管理、聊天/文章/计划管理、数据统计、系统日志

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 导入数据库

```bash
# 使用MySQL客户端导入
mysql -u root -p < database.sql
```

数据库名称：`AIStudyAssistant`

默认管理员：`admin` / `admin123`
测试用户：`zhangsan` / `test123456`

### 3. 配置AI API Key

编辑 `src/main/resources/application.yml`，修改DeepSeek API Key：

```yaml
ai:
  deepseek:
    api-key: 你的DeepSeek-API-Key
```

### 4. 运行项目

```bash
# 编译打包
mvn clean package -DskipTests

# 运行
mvn spring-boot:run

# 或者直接运行JAR
java -jar target/AIStudyAssistant-1.0.0.jar
```

### 5. 访问系统

- 前台首页：http://localhost:8080/
- 管理后台：http://localhost:8080/admin/

## 项目结构

```
AIStudyAssistant/
├── pom.xml                              # Maven配置
├── database.sql                         # 数据库脚本
├── README.md                            # 项目说明
├── src/main/java/com/aistudy/
│   ├── AIStudyAssistantApplication.java # 启动类
│   ├── config/                          # 配置类
│   │   ├── WebMvcConfig.java
│   │   └── MyBatisPlusConfig.java
│   ├── controller/                      # 控制器
│   │   ├── AuthController.java          # 认证（注册/登录/JWT）
│   │   ├── ChatController.java          # AI聊天
│   │   ├── ArticleController.java       # AI作文
│   │   ├── PlanController.java          # AI学习计划
│   │   ├── CodeController.java          # AI代码解释
│   │   ├── UserController.java          # 用户中心
│   │   ├── AdminController.java         # 管理员后台
│   │   └── MainController.java          # 页面路由
│   ├── service/                         # 服务层
│   │   ├── AIService.java               # AI服务接口
│   │   ├── UserService.java
│   │   ├── ChatService.java
│   │   ├── ArticleService.java
│   │   ├── PlanService.java
│   │   ├── CodeService.java
│   │   └── impl/                        # 实现类
│   ├── mapper/                          # 数据访问层
│   │   ├── UserMapper.java
│   │   ├── ChatMapper.java
│   │   ├── ArticleMapper.java
│   │   ├── PlanMapper.java
│   │   ├── AdminMapper.java
│   │   ├── LogMapper.java
│   │   └── FeedbackMapper.java
│   ├── entity/                          # 实体类
│   ├── dto/                             # 数据传输对象
│   ├── common/                          # 公共类
│   │   ├── Result.java                  # 统一返回结果
│   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   ├── interceptor/
│   │   └── JwtInterceptor.java          # JWT拦截器
│   └── util/
│       ├── JwtUtil.java                 # JWT工具
│       ├── PasswordUtil.java            # 密码加密
│       ├── FileUtil.java                # 文件工具
│       └── LogUtil.java                 # 日志工具
└── src/main/resources/
    ├── application.yml                  # 应用配置
    ├── templates/                       # Thymeleaf模板
    └── static/                          # 静态资源
```

## 配置说明

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| server.port | 服务端口 | 8080 |
| spring.datasource.url | 数据库连接 | jdbc:mysql://localhost:3306/AIStudyAssistant |
| jwt.secret | JWT签名密钥 | 需修改 |
| jwt.expiration | Token过期时间(ms) | 86400000 (24小时) |
| ai.provider | AI提供商 | deepseek |
| ai.deepseek.api-key | DeepSeek API Key | 需配置 |

## 实训报告对应

| 实训报告章节 | 对应项目内容 |
|-------------|-------------|
| 第一章 实训目标 | 项目背景、技术选型 |
| 第二章 项目描述 | 功能需求、页面设计 |
| 第三章 需求分析 | 六大功能模块分析 |
| 第四章 系统设计 | Mermaid架构图、E-R图、接口设计 |
| 第五章 核心代码 | Service层、Controller层代码 |
| 第六章 实训结果 | 运行截图、功能演示 |
| 第七章 实训总结 | 开发总结、改进方向 |

## 设计文档

详细设计文档见：`01_项目设计文档.md`

包含完整的系统架构图(Mermaid)、功能模块图、数据库E-R图、登录流程图、AI调用流程图。
