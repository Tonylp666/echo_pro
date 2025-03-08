<template>
  <div class="ai-tasks-container">
    <div class="page-header">
      <h2>AI生成任务</h2>
      <el-button type="primary" @click="showCreateTaskDialog">
        <el-icon><Plus /></el-icon> 创建任务
      </el-button>
    </div>
    
    <el-card class="filter-card">
      <div class="filter-container">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索任务名称"
          clearable
          @keyup.enter="handleSearch"
          style="width: 200px; margin-right: 10px;"
        />
        
        <el-select
          v-model="taskType"
          placeholder="任务类型"
          clearable
          @change="handleSearch"
          style="width: 150px; margin-right: 10px;"
        >
          <el-option label="爬虫数据" value="CRAWLER" />
          <el-option label="搜索关键词" value="SEARCH" />
          <el-option label="热点话题" value="HOTSPOT" />
        </el-select>
        
        <el-select
          v-model="status"
          placeholder="任务状态"
          clearable
          @change="handleSearch"
          style="width: 120px; margin-right: 10px;"
        >
          <el-option label="待处理" value="PENDING" />
          <el-option label="处理中" value="PROCESSING" />
          <el-option label="已完成" value="COMPLETED" />
          <el-option label="失败" value="FAILED" />
        </el-select>
        
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon> 搜索
        </el-button>
      </div>
    </el-card>
    
    <el-table
      v-loading="loading"
      :data="tasks"
      style="width: 100%; margin-top: 20px;"
      border
    >
      <el-table-column prop="taskName" label="任务名称" min-width="180" />
      
      <el-table-column prop="taskType" label="任务类型" width="120">
        <template #default="scope">
          {{ getTaskTypeText(scope.row.taskType) }}
        </template>
      </el-table-column>
      
      <el-table-column prop="aiModel" label="AI模型" width="120">
        <template #default="scope">
          {{ getAIModelText(scope.row.aiModel) }}
        </template>
      </el-table-column>
      
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column prop="createdTime" label="创建时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createdTime) }}
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="scope">
          <el-button
            size="small"
            type="primary"
            @click="viewTaskDetail(scope.row.id)"
          >
            详情
          </el-button>
          
          <el-button
            size="small"
            type="success"
            @click="viewGeneratedArticle(scope.row.resultArticleId)"
            v-if="scope.row.status === 'COMPLETED' && scope.row.resultArticleId"
          >
            查看文章
          </el-button>
          
          <el-button
            size="small"
            type="warning"
            @click="retryTask(scope.row.id)"
            v-if="scope.row.status === 'FAILED'"
          >
            重试
          </el-button>
          
          <el-button
            size="small"
            type="danger"
            @click="deleteTask(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-container">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-size="pageSize"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    
    <!-- 创建任务对话框 -->
    <el-dialog
      title="创建AI生成任务"
      v-model="createTaskDialogVisible"
      width="800px"
    >
      <el-form :model="taskForm" :rules="taskRules" ref="taskFormRef" label-width="120px">
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="taskForm.taskName" placeholder="请输入任务名称"></el-input>
        </el-form-item>
        
        <el-form-item label="任务类型" prop="taskType">
          <el-select v-model="taskForm.taskType" placeholder="请选择任务类型" style="width: 100%;">
            <el-option label="爬虫数据" value="CRAWLER" />
            <el-option label="搜索关键词" value="SEARCH" />
            <el-option label="热点话题" value="HOTSPOT" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="AI模型" prop="aiModel">
          <el-select v-model="taskForm.aiModel" placeholder="请选择AI模型" style="width: 100%;">
            <el-option label="DeepSeek" value="DEEPSEEK" />
            <el-option label="阿里云百炼" value="ALIYUN" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="文章分类" prop="articleCategoryId">
          <el-select v-model="taskForm.articleCategoryId" placeholder="请选择文章分类" style="width: 100%;">
            <el-option label="技术" :value="1" />
            <el-option label="生活" :value="2" />
            <el-option label="旅游" :value="3" />
          </el-select>
        </el-form-item>
        
        <el-form-item 
          label="爬虫规则" 
          prop="crawlerRuleId" 
          v-if="taskForm.taskType === 'CRAWLER'"
        >
          <el-select v-model="taskForm.crawlerRuleId" placeholder="请选择爬虫规则" style="width: 100%;">
            <el-option label="知乎热门" :value="1" />
            <el-option label="掘金文章" :value="2" />
            <el-option label="CSDN博客" :value="3" />
          </el-select>
        </el-form-item>
        
        <el-form-item 
          label="搜索关键词" 
          prop="searchKeywords" 
          v-if="taskForm.taskType === 'SEARCH'"
        >
          <el-input v-model="taskForm.searchKeywords" placeholder="请输入搜索关键词"></el-input>
        </el-form-item>
        
        <el-form-item 
          label="热点来源" 
          prop="hotspotSource" 
          v-if="taskForm.taskType === 'HOTSPOT'"
        >
          <el-select v-model="taskForm.hotspotSource" placeholder="请选择热点来源" style="width: 100%;">
            <el-option label="百度" value="baidu" />
            <el-option label="微博" value="weibo" />
            <el-option label="知乎" value="zhihu" />
            <el-option label="今日头条" value="toutiao" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="提示词">
          <el-input
            v-model="taskForm.prompt"
            type="textarea"
            :rows="4"
            placeholder="请输入提示词，用于指导AI生成内容"
          ></el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createTaskDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitTaskForm">创建</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 任务详情对话框 -->
    <el-dialog
      title="任务详情"
      v-model="taskDetailDialogVisible"
      width="800px"
    >
      <div v-loading="taskDetailLoading">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="任务名称">{{ taskDetail.taskName }}</el-descriptions-item>
          <el-descriptions-item label="任务类型">{{ getTaskTypeText(taskDetail.taskType) }}</el-descriptions-item>
          <el-descriptions-item label="AI模型">{{ getAIModelText(taskDetail.aiModel) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(taskDetail.status)">{{ getStatusText(taskDetail.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(taskDetail.createdTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDate(taskDetail.updatedTime) }}</el-descriptions-item>
          <el-descriptions-item label="提示词">{{ taskDetail.prompt }}</el-descriptions-item>
          <el-descriptions-item label="源数据">
            <pre class="source-data">{{ formatSourceData(taskDetail.sourceData) }}</pre>
          </el-descriptions-item>
          <el-descriptions-item v-if="taskDetail.errorMessage" label="错误信息">
            <div class="error-message">{{ taskDetail.errorMessage }}</div>
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="task-actions" v-if="taskDetail.id">
          <el-button
            type="success"
            @click="viewGeneratedArticle(taskDetail.resultArticleId)"
            v-if="taskDetail.status === 'COMPLETED' && taskDetail.resultArticleId"
          >
            查看生成的文章
          </el-button>
          
          <el-button
            type="warning"
            @click="retryTask(taskDetail.id)"
            v-if="taskDetail.status === 'FAILED'"
          >
            重试任务
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import moment from 'moment'

export default {
  name: 'AITasks',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const tasks = ref([])
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const searchKeyword = ref('')
    const taskType = ref('')
    const status = ref('')
    
    const createTaskDialogVisible = ref(false)
    const taskFormRef = ref(null)
    
    const taskDetailDialogVisible = ref(false)
    const taskDetailLoading = ref(false)
    const taskDetail = reactive({})
    
    const taskForm = reactive({
      taskName: '',
      taskType: '',
      aiModel: '',
      articleCategoryId: null,
      crawlerRuleId: null,
      searchKeywords: '',
      hotspotSource: '',
      prompt: ''
    })
    
    const taskRules = {
      taskName: [
        { required: true, message: '请输入任务名称', trigger: 'blur' }
      ],
      taskType: [
        { required: true, message: '请选择任务类型', trigger: 'change' }
      ],
      aiModel: [
        { required: true, message: '请选择AI模型', trigger: 'change' }
      ],
      articleCategoryId: [
        { required: true, message: '请选择文章分类', trigger: 'change' }
      ],
      crawlerRuleId: [
        { required: true, message: '请选择爬虫规则', trigger: 'change' }
      ],
      searchKeywords: [
        { required: true, message: '请输入搜索关键词', trigger: 'blur' }
      ],
      hotspotSource: [
        { required: true, message: '请选择热点来源', trigger: 'change' }
      ]
    }
    
    // 模拟数据
    const mockTasks = [
      {
        id: 1,
        taskName: '知乎热门文章生成',
        taskType: 'CRAWLER',
        aiModel: 'DEEPSEEK',
        status: 'COMPLETED',
        resultArticleId: 101,
        createdTime: '2023-01-01 12:00:00',
        updatedTime: '2023-01-01 12:05:00'
      },
      {
        id: 2,
        taskName: '搜索AI相关内容',
        taskType: 'SEARCH',
        aiModel: 'ALIYUN',
        status: 'PROCESSING',
        resultArticleId: null,
        createdTime: '2023-01-02 12:00:00',
        updatedTime: '2023-01-02 12:01:00'
      },
      {
        id: 3,
        taskName: '百度热搜话题分析',
        taskType: 'HOTSPOT',
        aiModel: 'DEEPSEEK',
        status: 'FAILED',
        resultArticleId: null,
        createdTime: '2023-01-03 12:00:00',
        updatedTime: '2023-01-03 12:02:00',
        errorMessage: 'AI模型调用失败，请稍后重试'
      }
    ]
    
    // 获取任务列表
    const fetchTasks = () => {
      loading.value = true
      
      // 模拟API请求
      setTimeout(() => {
        tasks.value = mockTasks
        total.value = mockTasks.length
        loading.value = false
      }, 500)
    }
    
    // 搜索
    const handleSearch = () => {
      currentPage.value = 1
      fetchTasks()
    }
    
    // 页码变化
    const handleCurrentChange = (page) => {
      currentPage.value = page
      fetchTasks()
    }
    
    // 每页条数变化
    const handleSizeChange = (size) => {
      pageSize.value = size
      currentPage.value = 1
      fetchTasks()
    }
    
    // 显示创建任务对话框
    const showCreateTaskDialog = () => {
      resetTaskForm()
      createTaskDialogVisible.value = true
    }
    
    // 重置任务表单
    const resetTaskForm = () => {
      Object.assign(taskForm, {
        taskName: '',
        taskType: '',
        aiModel: '',
        articleCategoryId: null,
        crawlerRuleId: null,
        searchKeywords: '',
        hotspotSource: '',
        prompt: ''
      })
      if (taskFormRef.value) {
        taskFormRef.value.resetFields()
      }
    }
    
    // 提交任务表单
    const submitTaskForm = async () => {
      if (!taskFormRef.value) return
      
      await taskFormRef.value.validate((valid, fields) => {
        if (valid) {
          // 模拟API请求
          setTimeout(() => {
            ElMessage.success('任务创建成功')
            createTaskDialogVisible.value = false
            fetchTasks()
          }, 500)
        } else {
          console.log('表单验证失败:', fields)
        }
      })
    }
    
    // 查看任务详情
    const viewTaskDetail = (id) => {
      taskDetailDialogVisible.value = true
      taskDetailLoading.value = true
      
      // 模拟API请求
      setTimeout(() => {
        const task = mockTasks.find(item => item.id === id)
        if (task) {
          Object.assign(taskDetail, task, {
            sourceData: JSON.stringify([
              {
                title: '示例标题1',
                content: '示例内容1'
              },
              {
                title: '示例标题2',
                content: '示例内容2'
              }
            ], null, 2)
          })
        }
        taskDetailLoading.value = false
      }, 500)
    }
    
    // 查看生成的文章
    const viewGeneratedArticle = (articleId) => {
      if (!articleId) {
        ElMessage.warning('文章尚未生成')
        return
      }
      
      router.push(`/article/view/${articleId}`)
    }
    
    // 重试任务
    const retryTask = (id) => {
      // 模拟API请求
      setTimeout(() => {
        const index = tasks.value.findIndex(item => item.id === id)
        if (index !== -1) {
          tasks.value[index].status = 'PENDING'
          tasks.value[index].errorMessage = null
          tasks.value[index].updatedTime = moment().format('YYYY-MM-DD HH:mm:ss')
        }
        
        if (taskDetailDialogVisible.value && taskDetail.id === id) {
          taskDetail.status = 'PENDING'
          taskDetail.errorMessage = null
          taskDetail.updatedTime = moment().format('YYYY-MM-DD HH:mm:ss')
        }
        
        ElMessage.success('任务已重新提交')
      }, 500)
    }
    
    // 删除任务
    const deleteTask = (id) => {
      ElMessageBox.confirm('确定要删除该任务吗？', '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 模拟API请求
        setTimeout(() => {
          tasks.value = tasks.value.filter(item => item.id !== id)
          total.value = tasks.value.length
          ElMessage.success('删除成功')
        }, 500)
      }).catch(() => {})
    }
    
    // 获取任务类型文本
    const getTaskTypeText = (type) => {
      switch (type) {
        case 'CRAWLER':
          return '爬虫数据'
        case 'SEARCH':
          return '搜索关键词'
        case 'HOTSPOT':
          return '热点话题'
        default:
          return type
      }
    }
    
    // 获取AI模型文本
    const getAIModelText = (model) => {
      switch (model) {
        case 'DEEPSEEK':
          return 'DeepSeek'
        case 'ALIYUN':
          return '阿里云百炼'
        default:
          return model
      }
    }
    
    // 获取状态类型
    const getStatusType = (status) => {
      switch (status) {
        case 'COMPLETED':
          return 'success'
        case 'PROCESSING':
          return 'warning'
        case 'PENDING':
          return 'info'
        case 'FAILED':
          return 'danger'
        default:
          return 'info'
      }
    }
    
    // 获取状态文本
    const getStatusText = (status) => {
      switch (status) {
        case 'COMPLETED':
          return '已完成'
        case 'PROCESSING':
          return '处理中'
        case 'PENDING':
          return '待处理'
        case 'FAILED':
          return '失败'
        default:
          return status
      }
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return ''
      return moment(dateString).format('YYYY-MM-DD HH:mm:ss')
    }
    
    // 格式化源数据
    const formatSourceData = (sourceData) => {
      if (!sourceData) return ''
      
      try {
        const data = JSON.parse(sourceData)
        return JSON.stringify(data, null, 2)
      } catch (e) {
        return sourceData
      }
    }
    
    onMounted(() => {
      fetchTasks()
    })
    
    return {
      loading,
      tasks,
      total,
      currentPage,
      pageSize,
      searchKeyword,
      taskType,
      status,
      createTaskDialogVisible,
      taskFormRef,
      taskForm,
      taskRules,
      taskDetailDialogVisible,
      taskDetailLoading,
      taskDetail,
      handleSearch,
      handleCurrentChange,
      handleSizeChange,
      showCreateTaskDialog,
      submitTaskForm,
      viewTaskDetail,
      viewGeneratedArticle,
      retryTask,
      deleteTask,
      getTaskTypeText,
      getAIModelText,
      getStatusType,
      getStatusText,
      formatDate,
      formatSourceData
    }
  }
}
</script>

<style scoped>
.ai-tasks-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-container {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.source-data {
  max-height: 300px;
  overflow-y: auto;
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  font-family: monospace;
  white-space: pre-wrap;
  word-break: break-all;
}

.error-message {
  color: #f56c6c;
  white-space: pre-wrap;
  word-break: break-all;
}

.task-actions {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 10px;
}
</style> 