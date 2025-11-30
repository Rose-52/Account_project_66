# Account Project — 账户管理系统

> 一个基于 Java 控制台的多用户账本管理工具，支持本地记账、MySQL 持久化、账单导出与跨设备同步。  

[![Java](https://img.shields.io/badge/Java-8+-ED8B00?logo=java&logoColor=white)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.x+-C71A36?logo=apache-maven&logoColor=white)](https://maven.apache.org/)

## ✨ 核心功能

- 📝 用户注册 / 登录（多用户隔离）
- 💰 支持创建多个账户（如工资卡、零花钱等）
- 📊 添加收入 / 支出（含金额、时间、备注）
- 🔍 智能查询：
  - 查看全部账单
  - 按日期范围筛选
  - 关键词搜索（如“餐饮”、“工资”）
- 📤 导出账单为 `.txt` 文件（按日期自动命名）
- 🔄 网络同步（特色功能）：
  - 启动服务端提供文件共享
  - 客户端通过 IP 地址上传/下载账本（基于 Socket 协议）
- 🔐 数据安全：所有操作写入 MySQL，支持本地备份

## 🛠️ 技术栈

| 类别         | 技术                |
|--------------|---------------------|
| 语言         | Java SE 8+          |
| 数据库       | MySQL 8.0           |
| 构建工具     | Apache Maven        |
| 数据访问     | JDBC + 连接池       |
| 网络通信     | TCP Socket          |
| 文件处理     | Java IO             |
## 📁 项目结构
```
Account_project/
├── src/main/java/com/cuit/
│   ├── app/
│   │   └── MainApp.java                  # 主程序入口
│   ├── dao/
│   │   ├── UserDao.java                  # 用户数据访问
│   │   └── AccountDao.java               # 账户数据访问
│   ├── domain/
│   │   ├── User.java                     # 用户实体类
│   │   └── Account.java                  # 账户实体类
│   ├── service/
│   │   ├── UserService.java              # 用户业务逻辑
│   │   ├── AccountService.java           # 账户业务逻辑
│   │   ├── ClientService.java            # 客户端同步服务
│   │   └── Service.java                  # 服务端监听程序
│   ├── tools/
│   │   ├── DBUtil.java                   # 数据库连接管理
│   │   ├── FileWriterUtil.java           # 账单文件写入
│   │   └── PrintAccountUtil.java         # 格式化输出
│   └── view/
│       ├── LoginView.java                # 登录界面
│       └── MainView.java                 # 主菜单界面
├── src/main/resources/
│   └── jdbc.properties                   # 数据库配置
├── download/                             # 下载的远程账本存放目录
└── pom.xml                               # Maven 依赖管理
```


## 🚀 快速开始

### 1. 初始化数据库

确保 MySQL 服务已启动，执行以下 SQL 创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS `014jdbc`
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```
然后使用该数据库，并创建所需的表：
```sql
USE `014jdbc`;

-- 用户表（注意：字段名 usernmae 为项目原始拼写）
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usernmae` varchar(20) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `usernmae` (`usernmae`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 账单表
CREATE TABLE `account` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `amount` DECIMAL(12,2) NOT NULL,
  `remark` VARCHAR(200) DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 2. 配置数据库连接

```properties
drivername=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/014jdbc
username=root
password=your_password
```

### 3. 编译并运行
```bash
mvn clean compile exec:java -Dexec.mainClass="com.cuit.app.MainApp"
```

## 🌐 跨设备同步使用指南

- 启动服务端：运行 `Service.java`（监听 9999 端口）
- 客户端在主菜单选择“上传/下载账本”，输入对方 IP 即可同步

## 📂 目录说明

| 目录/文件        | 用途说明                     |
|------------------|------------------------------|
| `download/`      | 存放从其他设备下载的账本文件 |
| `resources/`     | 配置文件目录                 |
| `jdbc.properties`| 数据库连接配置               |

## 📜 许可证

本项目仅用于课程学习与技术交流，禁止用于商业用途。
 

> 💬 如果你觉得这个项目对你有帮助，欢迎 ⭐️ Star！  
> 🐞 发现问题？欢迎提交 Issue 或 Pull Request。
