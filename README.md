# SocialCircle

## 项目简介
SocialCircle 是一个现代化的社交匹配与圈子管理系统，致力于为用户提供安全、高效、智能的社交体验。系统采用SpringBoot/Vue架构设计，支持高并发、高可用，并遵循企业级开发规范。

## 技术栈
- **后端框架**: Spring Boot 2.6.13
- **数据库**: MySQL
- **ORM框架**: MyBatis-Plus 3.5.3.1
- **缓存**: Redis
- **对象存储**: MinIO
- **API文档**: Knife4j (基于 Swagger)
- **安全框架**: JBCrypt
- **开发工具**: Lombok, Maven

## 主要功能
- 用户管理（注册、登录、信息管理）
- 角色权限管理
- 文件上传与管理
- 分布式会话管理
- 定时任务调度
- 系统监控

## 项目结构
```
src/main/java/org/wei/usercenterweb/
├── config/         # 配置类
├── controller/     # 控制器
├── service/        # 服务层
├── mapper/         # 数据访问层
├── domain/         # 实体类
├── common/         # 公共组件
├── exception/      # 异常处理
├── handler/        # 处理器
├── scheduler/      # 定时任务
└── utile/          # 工具类
```

## 快速开始

### 环境要求
- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 6.0+
- MinIO Server

### 配置说明
1. 克隆项目
```bash
git clone [项目地址]
```

2. 修改配置
- 在 `application.yml` 中配置数据库连接信息
- 配置 Redis 连接信息
- 配置 MinIO 连接信息

3. 启动项目
```bash
mvn spring-boot:run
```

## 开发规范
1. 代码规范
   - 遵循阿里巴巴Java开发手册
   - 使用统一的代码格式化工具
   - 类名、方法名、变量名采用驼峰命名法

2. 提交规范
   - feat: 新功能
   - fix: 修复bug
   - docs: 文档更新
   - style: 代码格式调整
   - refactor: 重构
   - test: 测试用例
   - chore: 构建过程或辅助工具的变动

3. 分支管理
   - master: 主分支，用于生产环境
   - develop: 开发分支
   - feature/*: 功能分支
   - hotfix/*: 紧急修复分支

## 文档
- [API文档](http://localhost:8080/doc.html)
- [数据库设计文档](docs/database.md)
- [部署文档](docs/deploy.md)

## 贡献指南
1. Fork 本仓库
2. 创建新的功能分支
3. 提交代码
4. 创建 Pull Request

## 版本历史
- v0.0.1-SNAPSHOT: 项目初始化

## 许可证
本项目采用 [MIT](LICENSE) 许可证

## 联系方式
- 项目负责人：[姓名]
- 邮箱：[邮箱地址] 
