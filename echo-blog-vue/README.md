# Echo博客管理系统前端

基于Vue 3 + Element Plus的博客管理系统前端项目。

## 技术栈

- Vue 3
- Vue Router
- Pinia
- Element Plus
- Axios
- SCSS

## 项目结构

```
src/
├── api/                # API接口
├── assets/             # 静态资源
│   └── styles/         # 样式文件
├── components/         # 公共组件
├── layout/             # 布局组件
├── router/             # 路由配置
├── store/              # 状态管理
├── utils/              # 工具函数
└── views/              # 页面组件
    ├── admin/          # 管理页面
    ├── article/        # 文章相关页面
    ├── category/       # 分类相关页面
    ├── dashboard/      # 仪表盘页面
    ├── error/          # 错误页面
    ├── login/          # 登录页面
    ├── platform/       # 平台相关页面
    ├── profile/        # 个人中心页面
    └── task/           # 任务相关页面
```

## 开发环境

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run serve
```

## 生产环境

```bash
# 构建生产版本
npm run build
```

## 功能模块

- 用户认证（登录/登出）
- 文章管理（创建/编辑/删除/发布）
- 分类管理
- 任务管理
- AI生成任务
- 爬虫管理
- 平台管理（多平台发布）

## 接口文档

接口基础路径: `/api`

详细接口文档请参考后端项目的API文档。 