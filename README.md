# 智能简历系统后端

## 项目简介

基于AI的简历优化与岗位匹配推荐系统后端，使用Spring Boot 2.7.18开发，集成了DashScope Qwen API进行智能简历解析和优化。

## 技术栈

- Spring Boot 2.7.18
- MyBatis-Plus
- MySQL
- JWT 认证
- DashScope Qwen API
- RESTful API

## 核心功能

1. **智能简历解析**：使用AI技术自动提取简历中的结构化信息
2. **简历优化建议**：基于AI分析生成简历优化建议
3. **岗位匹配算法**：根据技能、经验、学历等维度计算简历与职位的匹配度
4. **职位推荐**：为简历推荐最匹配的职位
5. **简历管理**：支持简历的上传、下载、解析和管理

## 项目结构

```
src/main/java/com/smartresume/
├── config/            # 配置类
├── controller/        # 控制器
├── model/             # 数据模型
├── service/           # 业务逻辑
├── mapper/            # 数据访问
├── common/            # 通用工具
└── Application.java   # 应用入口
```

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+

### 配置说明

1. 复制 `application.yml.example` 为 `application.yml`
2. 修改数据库连接信息
3. 添加 DashScope API Key

### 启动项目

```bash
# 编译项目
mvn clean package

# 运行项目
java -jar target/smart-resume-system-1.0.0.jar
```

## API文档

### 简历相关接口

- `POST /api/resume/upload` - 上传简历
- `POST /api/resume/parse` - 解析简历
- `GET /api/resume/list` - 获取简历列表
- `GET /api/resume/detail/{id}` - 获取简历详情

### 职位匹配接口

- `POST /api/job-match/match-resumes` - 匹配简历
- `POST /api/job-match/calculate-match` - 计算匹配度
- `GET /api/job-match/recommend-jobs` - 推荐职位

### AI服务接口

- `POST /api/ai/parse-resume` - 智能解析简历
- `POST /api/ai/optimize-resume` - 生成简历优化建议

## 测试

项目包含单元测试，可通过以下命令运行：

```bash
mvn test
```

## 许可证

MIT License
