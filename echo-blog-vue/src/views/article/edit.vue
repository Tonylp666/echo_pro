<template>
  <div class="article-edit-container app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑文章' : '创建文章' }}</span>
          <div class="right-menu">
            <el-button @click="goBack">返回</el-button>
          </div>
        </div>
      </template>
      
      <el-form
        ref="articleFormRef"
        :model="articleForm"
        :rules="articleRules"
        label-width="100px"
        class="article-form"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="articleForm.title" placeholder="请输入文章标题" />
        </el-form-item>
        
        <el-form-item label="分类" prop="articleCategoryId">
          <el-select v-model="articleForm.articleCategoryId" placeholder="请选择文章分类" style="width: 100%">
            <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="摘要" prop="summary">
          <el-input
            v-model="articleForm.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入文章摘要"
          />
        </el-form-item>
        
        <el-form-item label="封面图片" prop="coverImage">
          <el-upload
            class="cover-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
          >
            <img v-if="articleForm.coverImage" :src="articleForm.coverImage" class="cover-image" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="内容" prop="contentMarkdown">
          <div class="editor-container">
            <el-tabs v-model="activeTab" class="editor-tabs">
              <el-tab-pane label="编辑" name="edit">
                <el-input
                  v-model="articleForm.contentMarkdown"
                  type="textarea"
                  :rows="20"
                  placeholder="请输入文章内容（支持Markdown格式）"
                />
              </el-tab-pane>
              <el-tab-pane label="预览" name="preview">
                <div class="markdown-preview" v-html="renderedContent"></div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="articleForm.status">
            <el-radio label="DRAFT">草稿</el-radio>
            <el-radio label="PUBLISHED">发布</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">保存</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 平台发布对话框 -->
    <el-dialog
      v-model="publishDialogVisible"
      title="发布到平台"
      width="500px"
    >
      <el-form label-width="100px">
        <el-form-item label="选择平台">
          <el-checkbox-group v-model="selectedPlatforms">
            <el-checkbox
              v-for="item in platformList"
              :key="item.id"
              :label="item.id"
            >
              {{ item.platformName }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="publishDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="publishLoading" @click="handlePublishToPlatforms">
            发布
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'
import { getArticle, createArticle, updateArticle } from '@/api/article'
import { getCategories } from '@/api/category'
import { getPlatformConfigs, publishArticle } from '@/api/platform'

const route = useRoute()
const router = useRouter()

// 是否为编辑模式
const isEdit = computed(() => !!route.params.id)

// 表单引用
const articleFormRef = ref(null)
const submitLoading = ref(false)

// 文章表单
const articleForm = reactive({
  id: null,
  title: '',
  articleCategoryId: null,
  summary: '',
  content: '',
  contentHtml: '',
  contentMarkdown: '',
  coverImage: '',
  status: 'DRAFT'
})

// 表单验证规则
const articleRules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  articleCategoryId: [
    { required: true, message: '请选择文章分类', trigger: 'change' }
  ],
  contentMarkdown: [
    { required: true, message: '请输入文章内容', trigger: 'blur' }
  ]
}

// 分类列表
const categoryList = ref([])

// 编辑器相关
const activeTab = ref('edit')
const renderedContent = computed(() => {
  return marked(articleForm.contentMarkdown || '')
})

// 平台发布相关
const publishDialogVisible = ref(false)
const publishLoading = ref(false)
const platformList = ref([])
const selectedPlatforms = ref([])

// 监听Markdown内容变化，自动生成HTML内容
watch(() => articleForm.contentMarkdown, (val) => {
  if (val) {
    articleForm.contentHtml = marked(val)
    articleForm.content = val
  }
})

// 获取文章详情
const getArticleDetail = async (id) => {
  try {
    const { data } = await getArticle(id)
    Object.assign(articleForm, data)
  } catch (error) {
    console.error('获取文章详情失败:', error)
    ElMessage.error('获取文章详情失败')
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

// 获取平台列表
const getPlatformList = async () => {
  try {
    const { data } = await getPlatformConfigs()
    platformList.value = data
  } catch (error) {
    console.error('获取平台列表失败:', error)
  }
}

// 封面上传成功
const handleCoverSuccess = (res) => {
  articleForm.coverImage = res.data.url
  ElMessage.success('上传成功')
}

// 封面上传前校验
const beforeCoverUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
  }
  return isImage && isLt2M
}

// 提交表单
const submitForm = () => {
  articleFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateArticle(articleForm.id, articleForm)
          ElMessage.success('更新成功')
        } else {
          const { data } = await createArticle(articleForm)
          articleForm.id = data.id
          ElMessage.success('创建成功')
        }
        
        if (articleForm.status === 'PUBLISHED') {
          publishDialogVisible.value = true
        } else {
          goBack()
        }
      } catch (error) {
        console.error('保存文章失败:', error)
        ElMessage.error('保存文章失败')
      } finally {
        submitLoading.value = false
      }
    } else {
      return false
    }
  })
}

// 重置表单
const resetForm = () => {
  if (isEdit.value) {
    getArticleDetail(route.params.id)
  } else {
    articleFormRef.value.resetFields()
  }
}

// 发布到平台
const handlePublishToPlatforms = async () => {
  if (selectedPlatforms.value.length === 0) {
    ElMessage.warning('请选择至少一个平台')
    return
  }
  
  publishLoading.value = true
  try {
    for (const platformId of selectedPlatforms.value) {
      await publishArticle(articleForm.id, platformId)
    }
    ElMessage.success('发布成功')
    publishDialogVisible.value = false
    goBack()
  } catch (error) {
    console.error('发布到平台失败:', error)
    ElMessage.error('发布到平台失败')
  } finally {
    publishLoading.value = false
  }
}

// 返回列表页
const goBack = () => {
  router.push('/article')
}

onMounted(() => {
  getCategoryList()
  getPlatformList()
  
  if (isEdit.value) {
    getArticleDetail(route.params.id)
  }
})
</script>

<style lang="scss" scoped>
.article-edit-container {
  .article-form {
    max-width: 1000px;
  }
  
  .cover-uploader {
    width: 300px;
    
    .cover-image {
      width: 300px;
      height: 180px;
      object-fit: cover;
      border-radius: 4px;
    }
    
    .cover-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 300px;
      height: 180px;
      line-height: 180px;
      text-align: center;
      border: 1px dashed #d9d9d9;
      border-radius: 4px;
      cursor: pointer;
    }
  }
  
  .editor-container {
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    
    .editor-tabs {
      height: 100%;
      
      :deep(.el-tabs__content) {
        padding: 10px;
      }
    }
    
    .markdown-preview {
      min-height: 300px;
      max-height: 500px;
      overflow-y: auto;
      padding: 10px;
      
      h1, h2, h3, h4, h5, h6 {
        margin-top: 1em;
        margin-bottom: 0.5em;
      }
      
      p {
        margin: 0.5em 0;
      }
      
      img {
        max-width: 100%;
      }
      
      pre {
        background-color: #f5f7fa;
        padding: 10px;
        border-radius: 4px;
        overflow-x: auto;
      }
      
      code {
        background-color: #f5f7fa;
        padding: 2px 4px;
        border-radius: 2px;
      }
      
      blockquote {
        border-left: 4px solid #dcdfe6;
        padding-left: 10px;
        color: #606266;
        margin: 10px 0;
      }
      
      table {
        border-collapse: collapse;
        width: 100%;
        
        th, td {
          border: 1px solid #dcdfe6;
          padding: 8px;
        }
        
        th {
          background-color: #f5f7fa;
        }
      }
    }
  }
}
</style> 