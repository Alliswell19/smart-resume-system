# 🎓 智能简历管理系统 - 毕业设计完整版

## 📋 项目概述

**智能简历管理系统**是一个基于Spring Boot + Vue.js的现代化求职平台，集成了AI智能解析、职位匹配、简历优化等核心功能，为求职者和企业提供一站式的简历管理服务。

### 🎯 项目特色

- **🤖 AI智能解析** - 基于深度学习的简历内容自动提取和分析
- **🎯 智能匹配** - 精准的职位-简历匹配算法
- **✨ 简历优化** - AI驱动的简历内容优化建议
- **📊 数据可视化** - 丰富的统计分析和数据报表
- **🔒 安全可靠** - 完整的权限管理和数据安全保障

## 🏗️ 系统架构

### 技术栈

#### 后端技术栈
- **框架**: Spring Boot 2.7.18
- **安全**: Spring Security + JWT
- **ORM**: MyBatis-Plus 3.5.3.1
- **数据库**: MySQL 8.0
- **缓存**: Redis (可选)
- **文档**: Swagger/OpenAPI 3.0

#### 前端技术栈
- **框架**: Vue 3 + Vite
- **UI组件**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router
- **构建工具**: Vite

### 系统架构图

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端展示层     │    │   业务逻辑层     │    │   数据访问层     │
│  (Vue 3 + Vite) │◄──►│  (Spring Boot)  │◄──►│   (MySQL)      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   用户界面层     │    │   API网关层      │    │   缓存层         │
│ (Element Plus)  │    │  (Controller)    │    │   (Redis)       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🚀 快速开始

### 环境要求

- JDK 11+
- MySQL 8.0+
- Node.js 16+
- Maven 3.6+

### 后端部署

1. **克隆项目**
```bash
git clone <repository-url>
cd smart-resume-system
```

2. **数据库配置**
```sql
-- 创建数据库
CREATE DATABASE smart_resume CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 执行初始化脚本（位于 docs/database_design.md）
```

3. **配置文件**
编辑 `src/main/resources/application-dev.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smart_resume
    username: your_username
    password: your_password
```

4. **启动应用**
```bash
mvn spring-boot:run
```

### 前端部署

1. **进入前端目录**
```bash
cd my-vite-vue3
```

2. **安装依赖**
```bash
npm install
```

3. **启动开发服务器**
```bash
npm run dev
```

4. **构建生产版本**
```bash
npm run build
```

## 📁 项目结构

### 后端项目结构

```
smart-resume-system/
├── src/main/java/com/smartresume/
│   ├── common/           # 通用组件
│   ├── config/           # 配置类
│   ├── controller/       # 控制器层
│   ├── entity/          # 实体类
│   ├── mapper/          # 数据访问层
│   ├── service/         # 服务层
│   └── utils/           # 工具类
├── src/main/resources/
│   ├── mapper/          # MyBatis映射文件
│   ├── static/          # 静态资源
│   └── templates/       # 模板文件
└── docs/                # 项目文档
```

### 前端项目结构

```
my-vite-vue3/
├── src/
│   ├── api/             # API接口
│   ├── components/      # 组件
│   ├── router/          # 路由配置
│   ├── store/           # 状态管理
│   ├── utils/           # 工具函数
│   └── views/           # 页面视图
├── public/              # 公共资源
└── docs/               # 前端文档
```

## 🔧 核心功能模块

### 1. 用户管理模块
- 用户注册/登录
- 权限管理 (USER/ADMIN/HR)
- 个人信息管理
- 密码重置

### 2. 简历管理模块
- 简历上传 (PDF/DOC/DOCX/TXT)
- 智能内容解析
- 简历存储管理
- 简历分享功能

### 3. AI解析模块
- 基本信息提取
- 技能标签识别
- 工作经历分析
- 教育背景解析

### 4. 职位匹配模块
- 智能职位推荐
- 匹配度评分
- 职位搜索筛选
- 申请流程管理

### 5. 简历优化模块
- 内容优化建议
- 结构优化指导
- 关键词优化
- 智能评分系统

### 6. 系统管理模块
- 用户管理
- 数据统计
- 系统监控
- 日志管理

## 📊 数据库设计

### 核心数据表

| 表名 | 描述 | 主要字段 |
|------|------|----------|
| user | 用户表 | id, username, password, email, role |
| resume | 简历表 | id, user_id, title, file_path, parse_status |
| company | 公司表 | id, name, industry, scale, description |
| job | 职位表 | id, company_id, title, description, requirements |
| application | 申请记录表 | id, user_id, job_id, resume_id, status |
| system_log | 系统日志表 | id, user_id, module, action, status |

### 实体关系图

```
用户(User) ──── 简历(Resume) ──── 职位(Job) ──── 公司(Company)
    │               │                │               │
    │               │                │               │
申请记录(Application)  解析结果(Analysis)  匹配记录(Match)  行业分类(Industry)
```

## 🔐 安全设计

### 认证授权
- JWT Token认证
- 基于角色的访问控制 (RBAC)
- 密码BCrypt加密
- 会话管理

### 数据安全
- SQL注入防护
- XSS攻击防护
- CSRF防护
- 文件上传安全

### 隐私保护
- 数据脱敏处理
- 访问日志记录
- 敏感信息加密

## 📈 性能优化

### 数据库优化
- 合理的索引设计
- 查询优化
- 连接池配置
- 读写分离（可选）

### 缓存策略
- Redis缓存热点数据
- 页面静态化
- CDN加速

### 前端优化
- 组件懒加载
- 图片压缩
- 代码分割
- 缓存策略

## 🧪 测试策略

### 单元测试
- Service层测试
- Controller层测试
- 工具类测试

### 集成测试
- API接口测试
- 数据库操作测试
- 安全测试

### 性能测试
- 压力测试
- 并发测试
- 响应时间测试

## 📋 API文档

### 主要API接口

| 模块 | 接口 | 方法 | 描述 |
|------|------|------|------|
| 认证 | /api/auth/login | POST | 用户登录 |
| 用户 | /api/user/profile | GET | 获取用户信息 |
| 简历 | /api/resume/upload | POST | 上传简历 |
| 解析 | /api/ai/parse | POST | AI解析简历 |
| 职位 | /api/jobs/search | GET | 搜索职位 |
| 申请 | /api/application/apply | POST | 申请职位 |

### Swagger文档
启动应用后访问：`http://localhost:8080/swagger-ui.html`

## 🚀 部署指南

### 生产环境部署

1. **环境准备**
```bash
# 安装Java环境
sudo apt install openjdk-11-jdk

# 安装MySQL
sudo apt install mysql-server

# 安装Nginx
sudo apt install nginx
```

2. **应用部署**
```bash
# 构建应用
mvn clean package -DskipTests

# 部署JAR包
java -jar target/smartresume-0.0.1-SNAPSHOT.jar
```

3. **Nginx配置**
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location / {
        root /path/to/frontend/dist;
        try_files $uri $uri/ /index.html;
    }
    
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 📚 开发文档

### 开发规范
- 代码风格遵循阿里巴巴Java开发规范
- 前端代码遵循ESLint规范
- 提交信息使用约定式提交
- 分支管理使用Git Flow

### 开发工具
- IDE: IntelliJ IDEA / VS Code
- 数据库工具: MySQL Workbench
- API测试: Postman
- 版本控制: Git

## 🤝 贡献指南

### 问题反馈
如果您发现任何问题，请提交Issue，包括：
- 问题描述
- 重现步骤
- 期望结果
- 实际结果

### 功能建议
欢迎提出新功能建议，请描述：
- 功能需求
- 使用场景
- 预期效果

### 代码贡献
1. Fork项目
2. 创建功能分支
3. 提交代码
4. 创建Pull Request

## 📄 许可证

本项目采用MIT许可证，详见LICENSE文件。

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者！

---

**智能简历管理系统** - 让求职更智能，让招聘更高效！ 🚀