<template>
  <div class="app-wrapper">
    <!-- 侧边栏 -->
    <div class="sidebar-container" :class="{ 'is-collapsed': isCollapse }">
      <div class="logo-container">
        <img src="@/assets/logo.png" alt="Logo" class="logo" />
        <h1 v-if="!isCollapse" class="title">Echo博客</h1>
      </div>
      
      <el-scrollbar>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :unique-opened="true"
          :collapse-transition="false"
          class="sidebar-menu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router
        >
          <el-menu-item index="/dashboard">
            <el-icon><Odometer /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          
          <el-menu-item index="/article">
            <el-icon><Document /></el-icon>
            <span>文章管理</span>
          </el-menu-item>
          
          <el-menu-item index="/category">
            <el-icon><Collection /></el-icon>
            <span>分类管理</span>
          </el-menu-item>
          
          <el-menu-item index="/task">
            <el-icon><Clock /></el-icon>
            <span>任务管理</span>
          </el-menu-item>
          
          <el-menu-item index="/ai-tasks">
            <el-icon><Magic /></el-icon>
            <span>AI生成任务</span>
          </el-menu-item>
          
          <el-menu-item index="/crawler">
            <el-icon><Connection /></el-icon>
            <span>爬虫管理</span>
          </el-menu-item>
          
          <el-menu-item index="/platform">
            <el-icon><Share /></el-icon>
            <span>平台管理</span>
          </el-menu-item>
          
          <el-menu-item index="/profile">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
    </div>
    
    <!-- 主要内容区域 -->
    <div class="main-container">
      <!-- 顶部导航栏 -->
      <div class="navbar">
        <div class="left-area">
          <el-icon class="toggle-button" @click="toggleSidebar">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <breadcrumb />
        </div>
        
        <div class="right-menu">
          <el-dropdown trigger="click">
            <div class="avatar-container">
              <el-avatar :size="30" :src="userStore.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
              <span class="username">{{ userStore.name || userStore.username }}</span>
              <el-icon><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/profile')">
                  <el-icon><User /></el-icon>
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      
      <!-- 内容区域 -->
      <div class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <keep-alive>
              <component :is="Component" />
            </keep-alive>
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import Breadcrumb from './components/Breadcrumb.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 侧边栏折叠状态
const isCollapse = ref(false)

// 当前激活的菜单
const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta.activeMenu) {
    return meta.activeMenu
  }
  return path
})

// 切换侧边栏折叠状态
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 处理退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await userStore.logout()
    router.push('/login')
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  display: flex;
  height: 100vh;
  width: 100%;
  
  .sidebar-container {
    width: 210px;
    height: 100%;
    background-color: #304156;
    transition: width 0.3s;
    overflow: hidden;
    
    &.is-collapsed {
      width: 64px;
    }
    
    .logo-container {
      height: 60px;
      display: flex;
      align-items: center;
      padding: 0 10px;
      background-color: #2b3649;
      
      .logo {
        width: 32px;
        height: 32px;
        margin-right: 10px;
      }
      
      .title {
        color: #fff;
        font-size: 18px;
        font-weight: 600;
        margin: 0;
        white-space: nowrap;
      }
    }
    
    .sidebar-menu {
      border-right: none;
    }
  }
  
  .main-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    
    .navbar {
      height: 50px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 15px;
      box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
      background-color: #fff;
      
      .left-area {
        display: flex;
        align-items: center;
        
        .toggle-button {
          font-size: 20px;
          cursor: pointer;
          margin-right: 15px;
          color: #606266;
        }
      }
      
      .right-menu {
        display: flex;
        align-items: center;
        
        .avatar-container {
          display: flex;
          align-items: center;
          cursor: pointer;
          
          .username {
            margin: 0 5px;
            color: #606266;
          }
        }
      }
    }
    
    .app-main {
      flex: 1;
      padding: 15px;
      overflow-y: auto;
      background-color: #f0f2f5;
    }
  }
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}
</style> 