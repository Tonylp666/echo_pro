<template>
  <div class="task-list-container">
    <div class="page-header">
      <h2>AI生成任务</h2>
      <el-button type="primary" @click="showCreateTaskDialog">
        <i class="el-icon-plus"></i> 创建任务
      </el-button>
    </div>
    
    <el-card class="filter-card">
      <div class="filter-container">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索任务名称"
          clearable
          @keyup.enter.native="handleSearch"
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
          <i class="el-icon-search"></i> 搜索
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
      <ai-content-generator @task-created="handleTaskCreated" />
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createTaskDialogVisible = false">关闭</el-button>
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
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';
import moment from 'moment';
import AIContentGenerator from '@/components/AIContentGenerator.vue';

export default {
  name: 'AIGenerationTaskList',
  components: {
    AIContentGenerator
  },
  setup() {
    const loading = ref(false);
    const tasks = ref([]);
    const total = ref(0);
    const currentPage = ref(1);
    const pageSize = ref(10);
    const searchKeyword = ref('');
    const taskType = ref('');
    const status = ref('');
    const createTaskDialogVisible = ref(false);
    const taskDetailDialogVisible = ref(false);
    const taskDetailLoading = ref(false);
    const taskDetail = reactive({});
    
    // 获取任务列表
    const fetchTasks = async () => {
      loading.value = true;
      try {
        const params = {
          current: currentPage.value,
          size: pageSize.value
        };
        
        if (searchKeyword.value) {
          params.keyword = searchKeyword.value;
        }
        
        if (taskType.value) {
          params.taskType = taskType.value;
        }
        
        if (status.value) {
          params.status = status.value;
        }
        
        const response = await axios.get('/api/ai/generation/tasks', { params });
        tasks.value = response.data.data.records;
        total.value = response.data.data.total;
      } catch (error) {
        console.error('获取任务列表失败:', error);
        ElMessage.error('获取任务列表失败');
      } finally {
        loading.value = false;
      }
    };
    
    // 搜索
    const handleSearch = () => {
      currentPage.value = 1;
      fetchTasks();
    };
    
    // 页码变化
    const handleCurrentChange = (page) => {
      currentPage.value = page;
      fetchTasks();
    };
    
    // 每页条数变化
    const handleSizeChange = (size) => {
      pageSize.value = size;
      currentPage.value = 1;
      fetchTasks();
    };
    
    // 显示创建任务对话框
    const showCreateTaskDialog = () => {
      createTaskDialogVisible.value = true;
    };
    
    // 处理任务创建成功
    const handleTaskCreated = () => {
      createTaskDialogVisible.value = false;
      fetchTasks();
    };
    
    // 查看任务详情
    const viewTaskDetail = async (id) => {
      taskDetailLoading.value = true;
      taskDetailDialogVisible.value = true;
      
      try {
        const response = await axios.get(`/api/ai/generation/task/${id}`);
        Object.assign(taskDetail, response.data.data);
      } catch (error) {
        console.error('获取任务详情失败:', error);
        ElMessage.error('获取任务详情失败');
      } finally {
        taskDetailLoading.value = false;
      }
    };
    
    // 查看生成的文章
    const viewGeneratedArticle = (articleId) => {
      if (!articleId) {
        ElMessage.warning('文章尚未生成');
        return;
      }
      
      window.open(`/article/view/${articleId}`, '_blank');
    };
    
    // 重试任务
    const retryTask = async (id) => {
      try {
        await axios.post(`/api/ai/generation/task/${id}/retry`);
        ElMessage.success('任务已重新提交');
        fetchTasks();
        
        if (taskDetailDialogVisible.value) {
          viewTaskDetail(id);
        }
      } catch (error) {
        console.error('重试任务失败:', error);
        ElMessage.error('重试任务失败');
      }
    };
    
    // 删除任务
    const deleteTask = (id) => {
      ElMessageBox.confirm('确定要删除该任务吗？', '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await axios.delete(`/api/ai/generation/task/${id}`);
          ElMessage.success('删除任务成功');
          fetchTasks();
        } catch (error) {
          console.error('删除任务失败:', error);
          ElMessage.error('删除任务失败');
        }
      }).catch(() => {});
    };
    
    // 获取任务类型文本
    const getTaskTypeText = (type) => {
      switch (type) {
        case 'CRAWLER':
          return '爬虫数据';
        case 'SEARCH':
          return '搜索关键词';
        case 'HOTSPOT':
          return '热点话题';
        default:
          return type;
      }
    };
    
    // 获取AI模型文本
    const getAIModelText = (model) => {
      switch (model) {
        case 'DEEPSEEK':
          return 'DeepSeek';
        case 'ALIYUN':
          return '阿里云百炼';
        default:
          return model;
      }
    };
    
    // 获取状态类型
    const getStatusType = (status) => {
      switch (status) {
        case 'COMPLETED':
          return 'success';
        case 'PROCESSING':
          return 'warning';
        case 'PENDING':
          return 'info';
        case 'FAILED':
          return 'danger';
        default:
          return 'info';
      }
    };
    
    // 获取状态文本
    const getStatusText = (status) => {
      switch (status) {
        case 'COMPLETED':
          return '已完成';
        case 'PROCESSING':
          return '处理中';
        case 'PENDING':
          return '待处理';
        case 'FAILED':
          return '失败';
        default:
          return status;
      }
    };
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '';
      return moment(dateString).format('YYYY-MM-DD HH:mm:ss');
    };
    
    // 格式化源数据
    const formatSourceData = (sourceData) => {
      if (!sourceData) return '';
      
      try {
        const data = JSON.parse(sourceData);
        return JSON.stringify(data, null, 2);
      } catch (e) {
        return sourceData;
      }
    };
    
    onMounted(() => {
      fetchTasks();
    });
    
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
      taskDetailDialogVisible,
      taskDetailLoading,
      taskDetail,
      handleSearch,
      handleCurrentChange,
      handleSizeChange,
      showCreateTaskDialog,
      handleTaskCreated,
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
    };
  }
};
</script>

<style scoped>
.task-list-container {
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