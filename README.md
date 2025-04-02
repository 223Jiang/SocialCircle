# SocialCircle

## 项目简介
SocialCircle 是一个现代化的社交匹配与圈子管理系统，致力于为用户提供安全、高效、智能的社交体验。系统采用SpringBoot/Vue架构设计，支持高并发、高可用，并遵循企业级开发规范。

**使用地址**：https://sc.creativityhq.club/#/

## 技术栈
- **后端框架**: Spring Boot 2.6.13
- **数据库**: MySQL
- **ORM框架**: MyBatis-Plus 3.5.3.1
- **缓存**: Redis
- **分布式锁**: Redisson
- **对象存储**: MinIO
- **API文档**: Knife4j (基于 Swagger)
- **安全框架**: JBCrypt
- **开发工具**: Lombok, Maven

## 主要功能
- [用户管理（注册、登录、信息管理）](https://github.com/223Jiang/user-center)
- 角色权限管理
- 文件上传与管理
- 分布式会话管理
- 定时任务调度
- 智能用户匹配
- 圈子管理

## 技术亮点
### 1.分布式缓存与锁机制
- 使用 Redisson 实现分布式缓存和分布式锁
- 设置合理的锁超时时间
- 实现了分布式锁的看门狗机制（防止死锁）
- 防止并发加入队伍时的数据不一致问题

### 2.团队协作功能
- 实现了团队管理
- 支持多种队伍类型（公开、私密、加密）
- 支持团队成员管理
- 实现了团队权限控制

### 3.安全性设计
- 使用 BCrypt 加密队伍密码
- 密码验证机制
- 权限校验系统

### 4.并发控制
- 队伍人数限制
- 并发加入控制
- 状态检查机制

### 5.高级搜索功能
- 实现了基于标签的用户搜索系统
- 支持用户推荐功能（采用Jaccard Similarity算法）
- 采用内存机制获取用户数据，减少数据库压力
- 使用 MyBatis-Plus 实现高效的分页查询

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
git clone https://github.com/223Jiang/SocialCircle.git
```

2. 修改配置
- 在 `application.yml` 中配置数据库连接信息
- 配置 Redis 连接信息
- 配置 MinIO 连接信息

3. 启动项目
```bash
cd user-center-web
mvn spring-boot:run

cd user-dating-ui
npm run dev
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

## 贡献指南
1. Fork 本仓库
2. 创建新的功能分支
3. 提交代码
4. 创建 Pull Request

## 版本历史
- v1.0-SNAPSHOT: 项目v1.0

## 许可证
本项目采用 [MIT](LICENSE) 许可证

## 联系方式
- 项目负责人：果冻
- 邮箱：2879838778jiang@gmail.com
