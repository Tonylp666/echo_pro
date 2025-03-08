<template>
  <div class="article-list-container">
    <div class="page-header">
      <h2>文章列表</h2>
      <el-button type="primary" @click="$router.push('/article/create')">
        <i class="el-icon-plus"></i> 创建文章
      </el-button>
    </div>
    
    <el-card class="filter-card">
      <div class="filter-container">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索文章标题"
          clearable
          @keyup.enter.native="handleSearch"
          style="width: 200px; margin-right: 10px;"
        />
        
        <el-select
          v-model="categoryId"
          placeholder="选择分类"
          clearable
          @change="handleSearch"
          style="width: 150px; margin-right: 10px;"
        >
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.categoryName"
            :value="category.id"
          />
        </el-select>
        
        <el-select
          v-model="status"
          placeholder="文章状态"
          clearable
          @change="handleSearch"
          style="width: 120px; margin-right: 10px;"
        >
          <el-option label="草稿" value="DRAFT" />
          <el-option label="已发布" value="PUBLISHED" />
          <el-option label="私密" value="PRIVATE" />
        </el-select>
        
        <el-button type="primary" @click="handleSearch">
          <i class="el-icon-search"></i> 搜索
        </el-button>
      </div>
    </el-card>
    
    <el-table
      v-loading="loading"
      :data="articles"
      style="width: 100%; margin-top: 20px;"
      border
    >
      <el-table-column prop="title" label="标题" min-width="200">
        <template #default="scope">
          <el-link type="primary" @click="viewArticle(scope.row.id)">
            {{ scope.row.title }}
          </el-link>
        </template>
      </el-table-column>
      
      <el-table-column prop="articleCategoryName" label="分类" width="120" />
      
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column prop="viewCount" label="浏览量" width="100" />
      
      <el-table-column prop="likeCount" label="点赞数" width="100" />
      
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
            @click="editArticle(scope.row.id)"
          >
            编辑
          </el-button>
          
          <el-button
            size="small"
            type="success"
            @click="publishArticle(scope.row)"
            v-if="scope.row.status !== 'PUBLISHED'"
          >
            发布
          </el-button>
          
          <el-button
            size="small"
            type="warning"
            @click="publishToPlatforms(scope.row.id)"
            v-if="scope.row.status === 'PUBLISHED'"
          >
            分发
          </el-button>
          
          <el-button
            size="small"
            type="danger"
            @click="deleteArticle(scope.row.id)"
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
    
    <!-- 平台发布对话框 -->
    <el-dialog
      title="发布到平台"
      v-model="publishDialogVisible"
      width="650px"
    >
      <platform-publisher :article-id="selectedArticleId" />
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="publishDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';
import moment from 'moment';
import PlatformPublisher from '@/components/PlatformPublisher.vue';

export default {
  name: 'ArticleList',
  components: {
    PlatformPublisher
  },
  setup() {
    const loading = ref(false);
    const articles = ref([]);
    const categories = ref([]);
    const total = ref(0);
    const currentPage = ref(1);
    const pageSize = ref(10);
    const searchKeyword = ref('');
    const categoryId = ref('');
    const status = ref('');
    const publishDialogVisible = ref(false);
    const selectedArticleId = ref(null);
    
    // 获取文章列表
    const fetchArticles = async () => {
      loading.value = true;
      try {
        const params = {
          current: currentPage.value,
          size: pageSize.value
        };
        
        if (searchKeyword.value) {
          params.keyword = searchKeyword.value;
        }
        
        if (categoryId.value) {
          params.categoryId = categoryId.value;
        }
        
        if (status.value) {
          params.status = status.value;
        }
        
        const response = await axios.get('/api/articles', { params });
        articles.value = response.data.data.records;
        total.value = response.data.data.total;
      } catch (error) {
        console.error('获取文章列表失败:', error);
        ElMessage.error('获取文章列表失败');
      } finally {
        loading.value = false;
      }
    };
    
    // 获取分类列表
    const fetchCategories = async () => {
      try {
        const response = await axios.get('/api/article/categories');
        categories.value = response.data.data;
      } catch (error) {
        console.error('获取分类列表失败:', error);
        ElMessage.error('获取分类列表失败');
      }
    };
    
    // 搜索
    const handleSearch = () => {
      currentPage.value = 1;
      fetchArticles();
    };
    
    // 页码变化
    const handleCurrentChange = (page) => {
      currentPage.value = page;
      fetchArticles();
    };
    
    // 每页条数变化
    const handleSizeChange = (size) => {
      pageSize.value = size;
      currentPage.value = 1;
      fetchArticles();
    };
    
    // 查看文章
    const viewArticle = (id) => {
      window.open(`/article/view/${id}`, '_blank');
    };
    
    // 编辑文章
    const editArticle = (id) => {
      window.location.href = `/article/edit/${id}`;
    };
    
    // 发布文章
    const publishArticle = async (article) => {
      try {
        await axios.put(`/api/articles/${article.id}/status`, null, {
          params: { status: 'PUBLISHED' }
        });
        ElMessage.success('文章发布成功');
        fetchArticles();
      } catch (error) {
        console.error('发布文章失败:', error);
        ElMessage.error('发布文章失败');
      }
    };
    
    // 删除文章
    const deleteArticle = (id) => {
      ElMessageBox.confirm('确定要删除该文章吗？', '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await axios.delete(`/api/articles/${id}`);
          ElMessage.success('删除文章成功');
          fetchArticles();
        } catch (error) {
          console.error('删除文章失败:', error);
          ElMessage.error('删除文章失败');
        }
      }).catch(() => {});
    };
    
    // 发布到平台
    const publishToPlatforms = (id) => {
      selectedArticleId.value = id;
      publishDialogVisible.value = true;
    };
    
    // 获取状态类型
    const getStatusType = (status) => {
      switch (status) {
        case 'PUBLISHED':
          return 'success';
        case 'DRAFT':
          return 'info';
        case 'PRIVATE':
          return 'warning';
        default:
          return 'info';
      }
    };
    
    // 获取状态文本
    const getStatusText = (status) => {
      switch (status) {
        case 'PUBLISHED':
          return '已发布';
        case 'DRAFT':
          return '草稿';
        case 'PRIVATE':
          return '私密';
        default:
          return status;
      }
    };
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '';
      return moment(dateString).format('YYYY-MM-DD HH:mm:ss');
    };
    
    onMounted(() => {
      fetchArticles();
      fetchCategories();
    });
    
    return {
      loading,
      articles,
      categories,
      total,
      currentPage,
      pageSize,
      searchKeyword,
      categoryId,
      status,
      publishDialogVisible,
      selectedArticleId,
      handleSearch,
      handleCurrentChange,
      handleSizeChange,
      viewArticle,
      editArticle,
      publishArticle,
      deleteArticle,
      publishToPlatforms,
      getStatusType,
      getStatusText,
      formatDate
    };
  }
};
</script>

<style scoped>
.article-list-container {
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
</style> 