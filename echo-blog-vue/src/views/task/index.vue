<template>
  <div class="task-container app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>任务管理</span>
          <el-button type="primary" @click="handleCreateTask">
            <el-icon><Plus /></el-icon>
            创建任务
          </el-button>
        </div>
      </template>
      
      <el-table
        v-loading="loading"
        :data="taskList"
        style="width: 100%"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="taskName" label="任务名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="title" label="文章标题" min-width="150" show-overflow-tooltip />
        <el-table-column label="分类" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ getCategoryName(row.articleCategoryId) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="调度类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getScheduleTypeTag(row.scheduleType)" size="small">
              {{ getScheduleTypeText(row.scheduleType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="handleViewTaskDetail(scope.row.id)"
            >
              查看详情
            </el-button>
            
            <el-button
              size="small"
              type="warning"
              @click="handleEditTask(scope.row.id)"
            >
              编辑
            </el-button>
            
            <el-button
              v-if="scope.row.status === 'FAILED'"
              size="small"
              type="success"
              @click="handleRetryTask(scope.row.id)"
            >
              重试
            </el-button>
            
            <el-button
              size="small"
              type="danger"
              @click="handleDeleteTask(scope.row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.current"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 任务详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="任务详情"
      width="60%"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="任务名称">{{ currentTask.taskName }}</el-descriptions-item>
        <el-descriptions-item label="文章标题">{{ currentTask.title }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ getCategoryName(currentTask.articleCategoryId) }}</el-descriptions-item>
        <el-descriptions-item label="调度类型">{{ getScheduleTypeText(currentTask.scheduleType) }}</el-descriptions-item>
        <el-descriptions-item v-if="currentTask.scheduleType === 'CRON'" label="CRON表达式">
          {{ currentTask.scheduleCron }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTag(currentTask.status)">
            {{ getStatusText(currentTask.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="任务描述">{{ currentTask.description || '无' }}</el-descriptions-item>
        <el-descriptions-item label="提示词">{{ currentTask.prompt }}</el-descriptions-item>
        <el-descriptions-item label="源数据">
          <pre>{{ currentTask.sourceData || '无' }}</pre>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentTask.errorMessage" label="错误信息">
          <pre class="error-message">{{ currentTask.errorMessage }}</pre>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentTask.lastExecuteTime" label="最后执行时间">
          {{ currentTask.lastExecuteTime }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentTask.nextExecuteTime" label="下次执行时间">
          {{ currentTask.nextExecuteTime }}
        </el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button
            v-if="currentTask.resultArticleId"
            type="primary"
            @click="viewArticle(currentTask.resultArticleId)"
          >
            查看文章
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTasks, deleteTask, updateTaskStatus } from '@/api/task'
import { getCategories } from '@/api/category'

const router = useRouter()

// 任务列表
const taskList = ref([])
const loading = ref(false)
const total = ref(0)

// 分类列表
const categoryList = ref([])

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10
})

// 当前任务
const currentTask = ref({})
const detailDialogVisible = ref(false)

// 获取任务列表
const getTaskList = async () => {
  loading.value = true
  try {
    const { data } = await getTasks({
      current: queryParams.current,
      size: queryParams.size
    })
    taskList.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('获取任务列表失败:', error)
    ElMessage.error('获取任务列表失败')
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const getCategoryList = async () => {
  try {
    const { data } = await getCategories()
    categoryList.value = data
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 获取分类名称
const getCategoryName = (categoryId) => {
  const category = categoryList.value.find(item => item.id === categoryId)
  return category ? category.name : '未分类'
}

// 获取调度类型标签
const getScheduleTypeTag = (type) => {
  const map = {
    'ONCE': 'info',
    'CRON': 'warning'
  }
  return map[type] || 'info'
}

// 获取调度类型文本
const getScheduleTypeText = (type) => {
  const map = {
    'ONCE': '一次性',
    'CRON': '定时'
  }
  return map[type] || '未知'
}

// 获取状态标签
const getStatusTag = (status) => {
  const map = {
    'PENDING': 'info',
    'PROCESSING': 'warning',
    'COMPLETED': 'success',
    'FAILED': 'danger'
  }
  return map[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    'PENDING': '待处理',
    'PROCESSING': '处理中',
    'COMPLETED': '已完成',
    'FAILED': '失败'
  }
  return map[status] || '未知'
}

// 创建任务
const handleCreateTask = () => {
  router.push('/task/create')
}

// 编辑任务
const handleEditTask = (id) => {
  router.push(`/task/edit/${id}`)
}

// 查看任务详情
const handleViewTaskDetail = (id) => {
  console.log(`Viewing task details for id: ${id}`)
  // TODO: 实现查看任务详情的逻辑
}

// 重试任务
const handleRetryTask = async (id) => {
  try {
    await updateTaskStatus(id, 'PENDING')
    ElMessage.success('任务已重新提交')
    getTaskList()
  } catch (error) {
    console.error('重试任务失败:', error)
    ElMessage.error('重试任务失败')
  }
}

// 查看文章
const viewArticle = (id) => {
  router.push(`/article/edit/${id}`)
}

// 删除任务
const handleDeleteTask = (id) => {
  ElMessageBox.confirm('确定要删除该任务吗？删除后不可恢复！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteTask(id)
      ElMessage.success('删除成功')
      getTaskList()
    } catch (error) {
      console.error('删除任务失败:', error)
      ElMessage.error('删除任务失败')
    }
  }).catch(() => {})
}

// 分页处理
const handleSizeChange = (size) => {
  queryParams.size = size
  getTaskList()
}

const handleCurrentChange = (current) => {
  queryParams.current = current
  getTaskList()
}

onMounted(() => {
  getTaskList()
  getCategoryList()
})
</script>

<style lang="scss" scoped>
.task-container {
  .error-message {
    color: #f56c6c;
    margin: 0;
    white-space: pre-wrap;
    word-break: break-all;
  }
  
  pre {
    margin: 0;
    white-space: pre-wrap;
    word-break: break-all;
  }
}
</style> 