import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

// 路由配置
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', isPublic: true }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' }
      },
      {
        path: 'article',
        name: 'Article',
        component: () => import('@/views/article/index.vue'),
        meta: { title: '文章管理', icon: 'Document' }
      },
      {
        path: 'article/create',
        name: 'ArticleCreate',
        component: () => import('@/views/article/create.vue'),
        meta: { title: '创建文章', activeMenu: '/article' }
      },
      {
        path: 'article/edit/:id',
        name: 'ArticleEdit',
        component: () => import('@/views/article/edit.vue'),
        meta: { title: '编辑文章', activeMenu: '/article' }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/category/index.vue'),
        meta: { title: '分类管理', icon: 'Collection' }
      },
      {
        path: 'task',
        name: 'Task',
        component: () => import('@/views/task/index.vue'),
        meta: { title: '任务管理', icon: 'Clock' }
      },
      {
        path: 'task/create',
        name: 'TaskCreate',
        component: () => import('@/views/task/create.vue'),
        meta: { title: '创建任务', activeMenu: '/task' }
      },
      {
        path: 'task/edit/:id',
        name: 'TaskEdit',
        component: () => import('@/views/task/edit.vue'),
        meta: { title: '编辑任务', activeMenu: '/task' }
      },
      {
        path: 'ai-tasks',
        name: 'AITasks',
        component: () => import('@/views/admin/AITasks.vue'),
        meta: { title: 'AI生成任务', icon: 'Magic' }
      },
      {
        path: 'crawler',
        name: 'Crawler',
        component: () => import('@/views/crawler/index.vue'),
        meta: { title: '爬虫管理', icon: 'Connection' }
      },
      {
        path: 'crawler/rule',
        name: 'CrawlerRule',
        component: () => import('@/views/crawler/rule.vue'),
        meta: { title: '爬虫规则', activeMenu: '/crawler' }
      },
      {
        path: 'platform',
        name: 'Platform',
        component: () => import('@/views/platform/index.vue'),
        meta: { title: '平台管理', icon: 'Share' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', icon: 'User' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404', isPublic: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  NProgress.start()
  document.title = to.meta.title ? `${to.meta.title} - Echo博客管理系统` : 'Echo博客管理系统'
  
  const userStore = useUserStore()
  const token = userStore.token
  
  if (!to.meta.isPublic && !token) {
    next('/login')
  } else {
    next()
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router 