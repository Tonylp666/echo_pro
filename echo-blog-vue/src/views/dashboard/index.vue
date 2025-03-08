<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h2>欢迎使用 Echo博客管理系统</h2>
      <p>今天是 {{ currentDate }}</p>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="data-card">
          <div class="data-item">
            <div class="data-icon article-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="data-info">
              <div class="data-title">文章数量</div>
              <div class="data-value">{{ stats.articleCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="data-card">
          <div class="data-item">
            <div class="data-icon category-icon">
              <el-icon><Collection /></el-icon>
            </div>
            <div class="data-info">
              <div class="data-title">分类数量</div>
              <div class="data-value">{{ stats.categoryCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="data-card">
          <div class="data-item">
            <div class="data-icon task-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="data-info">
              <div class="data-title">任务数量</div>
              <div class="data-value">{{ stats.taskCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="data-card">
          <div class="data-item">
            <div class="data-icon view-icon">
              <el-icon><View /></el-icon>
            </div>
            <div class="data-info">
              <div class="data-title">总浏览量</div>
              <div class="data-value">{{ stats.totalViews }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>最近文章</span>
              <el-button type="text" @click="$router.push('/article')">查看更多</el-button>
            </div>
          </template>
          <el-table :data="recentArticles" style="width: 100%">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="createdTime" label="创建时间" width="180" />
            <el-table-column prop="viewCount" label="浏览量" width="100" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="text" @click="$router.push(`/article/edit/${row.id}`)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>最近任务</span>
              <el-button type="text" @click="$router.push('/task')">查看更多</el-button>
            </div>
          </template>
          <el-table :data="recentTasks" style="width: 100%">
            <el-table-column prop="taskName" label="任务名称" />
            <el-table-column prop="createdTime" label="创建时间" width="180" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="text" @click="$router.push(`/task/edit/${row.id}`)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getArticles } from '@/api/article'
import { getTasks } from '@/api/task'

// 统计数据
const stats = ref({
  articleCount: 0,
  categoryCount: 0,
  taskCount: 0,
  totalViews: 0
})

// 最近文章
const recentArticles = ref([])

// 最近任务
const recentTasks = ref([])

// 当前日期
const currentDate = new Date().toLocaleDateString('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  weekday: 'long'
})

// 获取最近文章
const fetchRecentArticles = async () => {
  try {
    const { data } = await getArticles({
      current: 1,
      size: 5,
      sort: 'createdTime,desc'
    })
    recentArticles.value = data.records || []
    stats.value.articleCount = data.total || 0
    
    // 计算总浏览量
    stats.value.totalViews = recentArticles.value.reduce((sum, article) => sum + (article.viewCount || 0), 0)
  } catch (error) {
    console.error('获取最近文章失败:', error)
  }
}

// 获取最近任务
const fetchRecentTasks = async () => {
  try {
    const { data } = await getTasks({
      current: 1,
      size: 5,
      sort: 'createdTime,desc'
    })
    recentTasks.value = data.records || []
    stats.value.taskCount = data.total || 0
  } catch (error) {
    console.error('获取最近任务失败:', error)
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const map = {
    'PENDING': 'info',
    'PROCESSING': 'warning',
    'COMPLETED': 'success',
    'FAILED': 'danger'
  }
  return map[status] || 'info'
}

// 模拟获取分类数量
const fetchCategoryCount = () => {
  // 实际项目中应该从API获取
  stats.value.categoryCount = 5
}

onMounted(() => {
  fetchRecentArticles()
  fetchRecentTasks()
  fetchCategoryCount()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  .dashboard-header {
    margin-bottom: 20px;
    
    h2 {
      margin: 0;
      font-size: 24px;
      color: #303133;
    }
    
    p {
      margin: 10px 0 0;
      color: #909399;
    }
  }
  
  .data-card {
    margin-bottom: 20px;
    
    .data-item {
      display: flex;
      align-items: center;
      
      .data-icon {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-right: 15px;
        
        .el-icon {
          font-size: 30px;
          color: #fff;
        }
        
        &.article-icon {
          background-color: #409EFF;
        }
        
        &.category-icon {
          background-color: #67C23A;
        }
        
        &.task-icon {
          background-color: #E6A23C;
        }
        
        &.view-icon {
          background-color: #F56C6C;
        }
      }
      
      .data-info {
        .data-title {
          font-size: 14px;
          color: #909399;
        }
        
        .data-value {
          font-size: 24px;
          font-weight: bold;
          color: #303133;
        }
      }
    }
  }
  
  .chart-row {
    margin-top: 20px;
  }
  
  .chart-card {
    margin-bottom: 20px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style> 