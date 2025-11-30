# 贡献指南

感谢你对 **Account Project** 的关注！欢迎提交 Issue 或 Pull Request 进行改进。


## 🐞 如何提交 Issue

请在 [Issues](https://github.com/your-username/Account_project/issues) 中提交问题，并包含以下信息：

- **标题**：简明描述问题（如“登录失败”、“账单导出路径错误”）
- **复现步骤**：
  1. 操作环境（操作系统、Java 版本、MySQL 版本）
  2. 具体操作流程
  3. 实际结果 vs 预期结果
- **附加信息**：错误日志、截图（如有）

❌ 请勿提交：
- 与项目无关的问题
- 重复 Issue
- 纯主观建议（如“界面不好看”）

## 🔧 如何提交 Pull Request (PR)

1. Fork 本仓库
2. 创建新分支（命名如 `fix-login-bug` 或 `feature-export-enhance`）
3. 在本地修改代码并测试通过
4. 提交 PR 到 `main` 分支，并说明：
   - 修改目的
   - 是否影响现有功能
   - 测试方法

✅ 接受的 PR 类型：
- 修复已知 Bug
- 优化代码结构（不改变逻辑）
- 完善文档或注释
- 改进异常处理

❌ 不接受的 PR：
- 新增与课程要求无关的功能（如 GUI、Web 前端）
- 修改数据库字段名（如将 `usernmae` 改为 `username`，因 Java 代码强依赖）
- 删除现有功能

## 📜 代码规范

- 使用 UTF-8 编码
- 注释使用中文
- 异常需打印日志或提示用户
- 不得硬编码密码或路径

---
> 💬 本项目由 Rose-52 维护，所有贡献将被认真审阅。  
> 感谢你的支持！
