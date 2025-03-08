<template>
  <div class="task-edit-container app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑任务' : '创建任务' }}</span>
          <div class="right-menu">
            <el-button @click="goBack">返回</el-button>
          </div>
        </div>
      </template>
      
      <el-form
        ref="taskFormRef"
        :model="taskForm"
        :rules="taskRules"
        label-width="100px"
        class="task-form"
      >
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="taskForm.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="taskForm.title" placeholder="请输入文章标题" />
        </el-form-item>
        
        <el-form-item label="分类" prop="articleCategoryId">
          <el-select v-model="taskForm.articleCategoryId" placeholder="请选择文章分类" style="width: 100%">
            <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="任务描述" prop="description">
          <el-input
            v-model="taskForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入任务描述"
          />
        </el-form-item>
        
        <el-form-item label="提示词" prop="prompt">
          <el-input
            v-model="taskForm.prompt"
            type="textarea"
            :rows="5"
            placeholder="请输入提示词"
          />
        </el-form-item>
        
        <el-form-item label="源数据" prop="sourceData">
          <el-input
            v-model="taskForm.sourceData"
            type="textarea"
            :rows="5"
            placeholder="请输入源数据（可选）"
          />
        </el-form-item>
        
        <el-form-item label="调度类型" prop="scheduleType">
          <el-radio-group v-model="taskForm.scheduleType">
            <el-radio label="ONCE">一次性</el-radio>
            <el-radio label="CRON">定时</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item
          v-if="taskForm.scheduleType === 'CRON'"
          label="CRON表达式"
          prop="scheduleCron"
        >
          <el-input v-model="taskForm.scheduleCron" placeholder="请输入CRON表达式，例如: 0 0 12 * * ?" />
          <div class="cron-helper">
            <p>常用CRON表达式:</p>
            <ul>
              <li><el-button type="text" @click="setCron('0 0 12 * * ?')">每天中午12点执行</el-button></li>
              <li><el-button type="text" @click="setCron('0 0 0 * * ?')">每天凌晨0点执行</el-button></li>
              <li><el-button type="text" @click="setCron('0 0 0 ? * MON')">每周一凌晨0点执行</el-button></li>
              <li><el-button type="text" @click="setCron('0 0 0 1 * ?')">每月1号凌晨0点执行</el-button></li>
            </ul>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">保存</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTask, createTask, updateTask } from '@/api/task'
import { getCategories } from '@/api/category'

const route = useRoute()
const router = useRouter()

// 是否为编辑模式
const isEdit = computed(() => !!route.params.id)

// 表单引用
const taskFormRef = ref(null)
const submitLoading = ref(false)

// 任务表单
const taskForm = reactive({
  id: null,
  taskName: '',
  title: '',
  articleCategoryId: null,
  description: '',
  prompt: '',
  sourceData: '',
  scheduleType: 'ONCE',
  scheduleCron: ''
})

// 表单验证规则
const taskRules = {
  taskName: [
    { required: true, message: '请输入任务名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  articleCategoryId: [
    { required: true, message: '请选择文章分类', trigger: 'change' }
  ],
  prompt: [
    { required: true, message: '请输入提示词', trigger: 'blur' }
  ],
  scheduleType: [
    { required: true, message: '请选择调度类型', trigger: 'change' }
  ],
  scheduleCron: [
    { 
      required: true, 
      message: '请输入CRON表达式', 
      trigger: 'blur',
      validator: (rule, value, callback) => {
        if (taskForm.scheduleType === 'CRON' && !value) {
          callback(new Error('请输入CRON表达式'))
        } else {
          callback()
        }
      }
    }
  ]
}

// 分类列表
const categoryList = ref([])

// 获取任务详情
const getTaskDetail = async (id) => {
  try {
    const { data } = await getTask(id)
    Object.assign(taskForm, data)
  } catch (error) {
    console.error('获取任务详情失败:', error)
    ElMessage.error('获取任务详情失败')
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

// 设置CRON表达式
const setCron = (cron) => {
  taskForm.scheduleCron = cron
}

// 提交表单
const submitForm = () => {
  taskFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateTask(taskForm.id, taskForm)
          ElMessage.success('更新成功')
        } else {
          await createTask(taskForm)
          ElMessage.success('创建成功')
        }
        goBack()
      } catch (error) {
        console.error('保存任务失败:', error)
        ElMessage.error('保存任务失败')
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
    getTaskDetail(route.params.id)
  } else {
    taskFormRef.value.resetFields()
  }
}

// 返回列表页
const goBack = () => {
  router.push('/task')
}

onMounted(() => {
  getCategoryList()
  
  if (isEdit.value) {
    getTaskDetail(route.params.id)
  }
})
</script>

<style lang="scss" scoped>
.task-edit-container {
  .task-form {
    max-width: 800px;
  }
  
  .cron-helper {
    margin-top: 10px;
    font-size: 14px;
    color: #606266;
    
    p {
      margin: 5px 0;
    }
    
    ul {
      margin: 5px 0;
      padding-left: 20px;
    }
  }
}
</style> 