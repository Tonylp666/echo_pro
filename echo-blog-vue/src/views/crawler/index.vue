<template>
  <div class="crawler-container">
    <div class="page-header">
      <h2>爬虫管理</h2>
      <el-button type="primary" @click="$router.push('/crawler/rule')">
        <el-icon><Plus /></el-icon> 创建规则
      </el-button>
    </div>
    
    <el-card class="filter-card">
      <div class="filter-container">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索规则名称"
          clearable
          @keyup.enter="handleSearch"
          style="width: 200px; margin-right: 10px;"
        />
        
        <el-select
          v-model="status"
          placeholder="规则状态"
          clearable
          @change="handleSearch"
          style="width: 120px; margin-right: 10px;"
        >
          <el-option label="启用" value="ENABLED" />
          <el-option label="禁用" value="DISABLED" />
        </el-select>
        
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon> 搜索
        </el-button>
      </div>
    </el-card>
    
    <el-table
      v-loading="loading"
      :data="rules"
      style="width: 100%; margin-top: 20px;"
      border
    >
      <el-table-column prop="ruleName" label="规则名称" min-width="180" />
      
      <el-table-column prop="targetUrl" label="目标网站" min-width="200" />
      
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'ENABLED' ? 'success' : 'info'">
            {{ scope.row.status === 'ENABLED' ? '启用' : '禁用' }}
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
            @click="editRule(scope.row.id)"
          >
            编辑
          </el-button>
          
          <el-button
            size="small"
            :type="scope.row.status === 'ENABLED' ? 'warning' : 'success'"
            @click="toggleStatus(scope.row)"
          >
            {{ scope.row.status === 'ENABLED' ? '禁用' : '启用' }}
          </el-button>
          
          <el-button
            size="small"
            type="success"
            @click="testRule(scope.row.id)"
          >
            测试
          </el-button>
          
          <el-button
            size="small"
            type="danger"
            @click="deleteRule(scope.row.id)"
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
    
    <!-- 测试结果对话框 -->
    <el-dialog
      title="测试结果"
      v-model="testDialogVisible"
      width="800px"
    >
      <div v-loading="testLoading">
        <el-table
          v-if="testData.length > 0"
          :data="testData"
          style="width: 100%;"
          border
        >
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="url" label="URL" min-width="200" />
          <el-table-column prop="date" label="日期" width="150" />
          <el-table-column prop="author" label="作者" width="150" />
          <el-table-column label="内容预览" min-width="250">
            <template #default="scope">
              <div class="content-preview">{{ scope.row.content }}</div>
            </template>
          </el-table-column>
        </el-table>
        <div v-else class="empty-data">
          <el-empty description="暂无数据" />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import moment from 'moment'

export default {
  name: 'CrawlerIndex',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const rules = ref([])
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const searchKeyword = ref('')
    const status = ref('')
    
    const testDialogVisible = ref(false)
    const testLoading = ref(false)
    const testData = ref([])
    
    // 模拟数据
    const mockRules = [
      {
        id: 1,
        ruleName: '知乎热门',
        targetUrl: 'https://www.zhihu.com/hot',
        status: 'ENABLED',
        createdTime: '2023-01-01 12:00:00'
      },
      {
        id: 2,
        ruleName: '掘金文章',
        targetUrl: 'https://juejin.cn',
        status: 'ENABLED',
        createdTime: '2023-01-02 12:00:00'
      },
      {
        id: 3,
        ruleName: 'CSDN博客',
        targetUrl: 'https://blog.csdn.net',
        status: 'DISABLED',
        createdTime: '2023-01-03 12:00:00'
      }
    ]
    
    // 获取爬虫规则列表
    const fetchRules = () => {
      loading.value = true
      
      // 模拟API请求
      setTimeout(() => {
        rules.value = mockRules
        total.value = mockRules.length
        loading.value = false
      }, 500)
    }
    
    // 搜索
    const handleSearch = () => {
      currentPage.value = 1
      fetchRules()
    }
    
    // 页码变化
    const handleCurrentChange = (page) => {
      currentPage.value = page
      fetchRules()
    }
    
    // 每页条数变化
    const handleSizeChange = (size) => {
      pageSize.value = size
      currentPage.value = 1
      fetchRules()
    }
    
    // 编辑规则
    const editRule = (id) => {
      router.push(`/crawler/rule?id=${id}`)
    }
    
    // 切换状态
    const toggleStatus = (rule) => {
      const newStatus = rule.status === 'ENABLED' ? 'DISABLED' : 'ENABLED'
      
      // 模拟API请求
      rule.status = newStatus
      ElMessage.success(`规则已${newStatus === 'ENABLED' ? '启用' : '禁用'}`)
    }
    
    // 测试规则
    const testRule = (id) => {
      testDialogVisible.value = true
      testLoading.value = true
      
      // 模拟API请求
      setTimeout(() => {
        // 使用 id 参数模拟获取特定规则的测试数据
        console.log(`Testing rule with id: ${id}`)
        testData.value = [
          {
            title: '测试标题1',
            url: 'https://example.com/1',
            content: '这是测试内容1，用于展示爬虫规则的测试结果。',
            date: '2023-01-01',
            author: '作者1'
          },
          {
            title: '测试标题2',
            url: 'https://example.com/2',
            content: '这是测试内容2，用于展示爬虫规则的测试结果。',
            date: '2023-01-02',
            author: '作者2'
          }
        ]
        testLoading.value = false
      }, 1000)
    }
    
    // 删除规则
    const deleteRule = (id) => {
      ElMessageBox.confirm('确定要删除该规则吗？', '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 模拟API请求
        rules.value = rules.value.filter(item => item.id !== id)
        ElMessage.success('删除成功')
      }).catch(() => {})
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return ''
      return moment(dateString).format('YYYY-MM-DD HH:mm:ss')
    }
    
    onMounted(() => {
      fetchRules()
    })
    
    return {
      loading,
      rules,
      total,
      currentPage,
      pageSize,
      searchKeyword,
      status,
      testDialogVisible,
      testLoading,
      testData,
      handleSearch,
      handleCurrentChange,
      handleSizeChange,
      editRule,
      toggleStatus,
      testRule,
      deleteRule,
      formatDate
    }
  }
}
</script>

<style scoped>
.crawler-container {
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

.content-preview {
  max-height: 100px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
}

.empty-data {
  padding: 30px 0;
}
</style> 