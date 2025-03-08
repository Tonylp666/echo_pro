<template>
  <div class="article-edit-container">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑文章' : '创建文章' }}</h2>
      <div class="header-actions">
        <el-button @click="$router.push('/articles')">返回列表</el-button>
        <el-button type="primary" @click="saveArticle" :loading="saving">保存</el-button>
        <el-button type="success" @click="publishArticle" :loading="publishing" v-if="!isEdit || article.status !== 'PUBLISHED'">发布</el-button>
      </div>
    </div>
    
    <el-form :model="article" label-width="100px" ref="articleForm" :rules="rules">
      <el-row :gutter="20">
        <el-col :span="18">
          <el-form-item label="标题" prop="title">
            <el-input v-model="article.title" placeholder="请输入文章标题" />
          </el-form-item>
          
          <el-form-item label="内容" prop="contentMarkdown">
            <markdown-editor v-model="article.contentMarkdown" @html-content="updateHtmlContent" />
          </el-form-item>
        </el-col>
        
        <el-col :span="6">
          <el-card class="article-settings">
            <template #header>
              <div class="card-header">
                <span>文章设置</span>
              </div>
            </template>
            
            <el-form-item label="分类" prop="articleCategoryId">
              <el-select v-model="article.articleCategoryId" placeholder="请选择分类" style="width: 100%;">
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.categoryName"
                  :value="category.id"
                />
              </el-select>
            </el-form-item>
            
            <el-form-item label="状态">
              <el-select v-model="article.status" placeholder="请选择状态" style="width: 100%;">
                <el-option label="草稿" value="DRAFT" />
                <el-option label="已发布" value="PUBLISHED" />
                <el-option label="私密" value="PRIVATE" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="摘要">
              <el-input
                v-model="article.summary"
                type="textarea"
                :rows="4"
                placeholder="请输入文章摘要，如不填写将自动提取正文前150个字"
              />
            </el-form-item>
            
            <el-form-item label="封面图">
              <el-upload
                class="cover-uploader"
                action="/api/upload/image"
                :show-file-list="false"
                :on-success="handleCoverSuccess"
                :before-upload="beforeCoverUpload"
              >
                <img v-if="article.coverImage" :src="article.coverImage" class="cover-image" />
                <el-icon v-else class="cover-uploader-icon"><i class="el-icon-plus">+</i></el-icon>
              </el-upload>
            </el-form-item>
          </el-card>
          
          <el-card class="ai-assistant" style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <span>AI助手</span>
              </div>
            </template>
            
            <div class="ai-actions">
              <el-button @click="generateTitle" :disabled="!article.contentMarkdown">生成标题</el-button>
              <el-button @click="generateSummary" :disabled="!article.contentMarkdown">生成摘要</el-button>
              <el-button @click="improveContent" :disabled="!article.contentMarkdown">优化内容</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import MarkdownEditor from '@/components/MarkdownEditor.vue';

export default {
  name: 'ArticleEdit',
  components: {
    MarkdownEditor
  },
  setup() {
    const route = useRoute();
    const router = useRouter();
    const articleForm = ref(null);
    const saving = ref(false);
    const publishing = ref(false);
    const categories = ref([]);
    
    // 文章表单
    const article = reactive({
      id: null,
      title: '',
      contentMarkdown: '',
      contentHtml: '',
      summary: '',
      articleCategoryId: null,
      status: 'DRAFT',
      coverImage: ''
    });
    
    // 表单验证规则
    const rules = {
      title: [
        { required: true, message: '请输入文章标题', trigger: 'blur' },
        { min: 2, max: 100, message: '标题长度在2到100个字符之间', trigger: 'blur' }
      ],
      contentMarkdown: [
        { required: true, message: '请输入文章内容', trigger: 'blur' }
      ],
      articleCategoryId: [
        { required: true, message: '请选择文章分类', trigger: 'change' }
      ]
    };
    
    // 是否为编辑模式
    const isEdit = computed(() => {
      return !!article.id;
    });
    
    // 获取文章详情
    const fetchArticle = async (id) => {
      try {
        const response = await axios.get(`/api/articles/${id}`);
        const articleData = response.data.data;
        
        Object.keys(article).forEach(key => {
          if (key in articleData) {
            article[key] = articleData[key];
          }
        });
      } catch (error) {
        console.error('获取文章详情失败:', error);
        ElMessage.error('获取文章详情失败');
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
    
    // 更新HTML内容
    const updateHtmlContent = (html) => {
      article.contentHtml = html;
      
      // 如果没有摘要，自动提取
      if (!article.summary && article.contentMarkdown) {
        const text = article.contentMarkdown.replace(/[#*`>-]/g, '').trim();
        article.summary = text.substring(0, 150) + (text.length > 150 ? '...' : '');
      }
    };
    
    // 保存文章
    const saveArticle = async () => {
      if (!articleForm.value) return;
      
      await articleForm.value.validate(async (valid) => {
        if (!valid) return;
        
        saving.value = true;
        try {
          let response;
          
          if (isEdit.value) {
            response = await axios.put(`/api/articles/${article.id}`, article);
          } else {
            response = await axios.post('/api/articles', article);
          }
          
          ElMessage.success('保存成功');
          
          // 如果是新建，跳转到编辑页
          if (!isEdit.value) {
            router.push(`/article/edit/${response.data.data.id}`);
          }
        } catch (error) {
          console.error('保存文章失败:', error);
          ElMessage.error('保存文章失败');
        } finally {
          saving.value = false;
        }
      });
    };
    
    // 发布文章
    const publishArticle = async () => {
      if (!articleForm.value) return;
      
      await articleForm.value.validate(async (valid) => {
        if (!valid) return;
        
        publishing.value = true;
        try {
          article.status = 'PUBLISHED';
          
          if (isEdit.value) {
            await axios.put(`/api/articles/${article.id}`, article);
          } else {
            const response = await axios.post('/api/articles', article);
            article.id = response.data.data.id;
          }
          
          ElMessage.success('发布成功');
          
          // 如果是新建，跳转到编辑页
          if (!article.id) {
            router.push(`/article/edit/${article.id}`);
          }
        } catch (error) {
          console.error('发布文章失败:', error);
          ElMessage.error('发布文章失败');
        } finally {
          publishing.value = false;
        }
      });
    };
    
    // 处理封面上传成功
    const handleCoverSuccess = (res, file) => {
      article.coverImage = res.data.url;
    };
    
    // 上传前检查
    const beforeCoverUpload = (file) => {
      const isImage = file.type.startsWith('image/');
      const isLt2M = file.size / 1024 / 1024 < 2;
      
      if (!isImage) {
        ElMessage.error('上传封面图片只能是图片格式!');
      }
      
      if (!isLt2M) {
        ElMessage.error('上传封面图片大小不能超过 2MB!');
      }
      
      return isImage && isLt2M;
    };
    
    // AI生成标题
    const generateTitle = async () => {
      try {
        const response = await axios.post('/api/ai/generate/title', {
          content: article.contentMarkdown
        });
        
        article.title = response.data.data;
        ElMessage.success('标题生成成功');
      } catch (error) {
        console.error('生成标题失败:', error);
        ElMessage.error('生成标题失败');
      }
    };
    
    // AI生成摘要
    const generateSummary = async () => {
      try {
        const response = await axios.post('/api/ai/generate/summary', {
          content: article.contentMarkdown
        });
        
        article.summary = response.data.data;
        ElMessage.success('摘要生成成功');
      } catch (error) {
        console.error('生成摘要失败:', error);
        ElMessage.error('生成摘要失败');
      }
    };
    
    // AI优化内容
    const improveContent = async () => {
      try {
        const response = await axios.post('/api/ai/generate/improve', {
          content: article.contentMarkdown
        });
        
        article.contentMarkdown = response.data.data;
        ElMessage.success('内容优化成功');
      } catch (error) {
        console.error('优化内容失败:', error);
        ElMessage.error('优化内容失败');
      }
    };
    
    onMounted(async () => {
      await fetchCategories();
      
      const id = route.params.id;
      if (id) {
        article.id = parseInt(id);
        await fetchArticle(id);
      }
    });
    
    return {
      articleForm,
      article,
      categories,
      rules,
      isEdit,
      saving,
      publishing,
      updateHtmlContent,
      saveArticle,
      publishArticle,
      handleCoverSuccess,
      beforeCoverUpload,
      generateTitle,
      generateSummary,
      improveContent
    };
  }
};
</script>

<style scoped>
.article-edit-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.article-settings {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cover-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100%;
  height: 150px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.cover-uploader:hover {
  border-color: #409eff;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.ai-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style> 