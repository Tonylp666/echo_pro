<template>
  <div class="article-container app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>文章管理</span>
          <div class="right-menu">
            <el-input
              v-model="queryParams.keyword"
              placeholder="搜索文章标题"
              style="width: 200px; margin-right: 10px"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleCreateArticle">
              <el-icon><Plus /></el-icon>
              创建文章
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table
        v-loading="loading"
        :data="articleList"
        style="width: 100%"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="分类" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ getCategoryName(row.articleCategoryId) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="100" align="center" />
        <el-table-column prop="likeCount" label="点赞量" width="100" align="center" />
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEditArticle(row.id)"
            >
              编辑
            </el-button>
            <el-button
              v-if="row.status === 'DRAFT'"
              type="success"
              size="small"
              @click="handlePublishArticle(row.id)"
            >
              发布
            </el-button>
            <el-button
              v-if="row.status === 'PUBLISHED'"
              type="warning"
              size="small"
              @click="handleUnpublishArticle(row.id)"
            >
              下线
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteArticle(row.id)"
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getArticles, deleteArticle, updateArticleStatus } from '@/api/article'
import { getCategories } from '@/api/category'

const router = useRouter()

// 文章列表
const articleList = ref([])
const loading = ref(false)
const total = ref(0)

// 分类列表
const categoryList = ref([])

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  keyword: ''
})

// 获取文章列表
const getArticleList = async () => {
  loading.value = true
  try {
    const { data } = await getArticles({
      current: queryParams.current,
      size: queryParams.size,
      keyword: queryParams.keyword
    })
    articleList.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('获取文章列表失败:', error)
    ElMessage.error('获取文章列表失败')
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

// 获取状态类型
const getStatusType = (status) => {
  const map = {
    'DRAFT': 'info',
    'PUBLISHED': 'success',
    'UNPUBLISHED': 'warning',
    'DELETED': 'danger'
  }
  return map[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    'DRAFT': '草稿',
    'PUBLISHED': '已发布',
    'UNPUBLISHED': '已下线',
    'DELETED': '已删除'
  }
  return map[status] || '未知'
}

// 创建文章
const handleCreateArticle = () => {
  router.push('/article/create')
}

// 编辑文章
const handleEditArticle = (id) => {
  router.push(`/article/edit/${id}`)
}

// 发布文章
const handlePublishArticle = async (id) => {
  try {
    await updateArticleStatus(id, 'PUBLISHED')
    ElMessage.success('文章发布成功')
    getArticleList()
  } catch (error) {
    console.error('发布文章失败:', error)
    ElMessage.error('发布文章失败')
  }
}

// 下线文章
const handleUnpublishArticle = async (id) => {
  try {
    await updateArticleStatus(id, 'UNPUBLISHED')
    ElMessage.success('文章已下线')
    getArticleList()
  } catch (error) {
    console.error('下线文章失败:', error)
    ElMessage.error('下线文章失败')
  }
}

// 删除文章
const handleDeleteArticle = (id) => {
  ElMessageBox.confirm('确定要删除该文章吗？删除后不可恢复！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteArticle(id)
      ElMessage.success('删除成功')
      getArticleList()
    } catch (error) {
      console.error('删除文章失败:', error)
      ElMessage.error('删除文章失败')
    }
  }).catch(() => {})
}

// 搜索
const handleSearch = () => {
  queryParams.current = 1
  getArticleList()
}

// 分页处理
const handleSizeChange = (size) => {
  queryParams.size = size
  getArticleList()
}

const handleCurrentChange = (current) => {
  queryParams.current = current
  getArticleList()
}

onMounted(() => {
  getArticleList()
  getCategoryList()
})
</script>

<style lang="scss" scoped>
.article-container {
  .right-menu {
    display: flex;
    align-items: center;
  }
}
</style> 