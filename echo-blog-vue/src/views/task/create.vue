<template>
  <div class="task-create-container">
    <div class="page-header">
      <h2>创建任务</h2>
      <el-button @click="$router.push('/task')">返回列表</el-button>
    </div>
    
    <el-form :model="taskForm" :rules="rules" ref="taskFormRef" label-width="100px">
      <el-form-item label="任务名称" prop="name">
        <el-input v-model="taskForm.name" placeholder="请输入任务名称"></el-input>
      </el-form-item>
      
      <el-form-item label="任务类型" prop="type">
        <el-select v-model="taskForm.type" placeholder="请选择任务类型" style="width: 100%;">
          <el-option label="文章发布" value="PUBLISH"></el-option>
          <el-option label="内容采集" value="COLLECT"></el-option>
          <el-option label="数据分析" value="ANALYZE"></el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item label="执行时间" prop="executeTime">
        <el-date-picker
          v-model="taskForm.executeTime"
          type="datetime"
          placeholder="选择执行时间"
          style="width: 100%;"
        ></el-date-picker>
      </el-form-item>
      
      <el-form-item label="任务描述" prop="description">
        <el-input
          v-model="taskForm.description"
          type="textarea"
          :rows="4"
          placeholder="请输入任务描述"
        ></el-input>
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="submitForm">创建任务</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'TaskCreate',
  setup() {
    const router = useRouter()
    const taskFormRef = ref(null)
    
    const taskForm = reactive({
      name: '',
      type: '',
      executeTime: '',
      description: ''
    })
    
    const rules = {
      name: [
        { required: true, message: '请输入任务名称', trigger: 'blur' },
        { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
      ],
      type: [
        { required: true, message: '请选择任务类型', trigger: 'change' }
      ],
      executeTime: [
        { required: true, message: '请选择执行时间', trigger: 'change' }
      ]
    }
    
    const submitForm = async () => {
      if (!taskFormRef.value) return
      
      await taskFormRef.value.validate((valid) => {
        if (valid) {
          // 这里应该调用API创建任务
          ElMessage.success('任务创建成功')
          router.push('/task')
        }
      })
    }
    
    const resetForm = () => {
      if (taskFormRef.value) {
        taskFormRef.value.resetFields()
      }
    }
    
    return {
      taskFormRef,
      taskForm,
      rules,
      submitForm,
      resetForm
    }
  }
}
</script>

<style scoped>
.task-create-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style> 