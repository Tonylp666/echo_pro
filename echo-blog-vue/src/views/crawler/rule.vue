<template>
  <div class="crawler-rule-container">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑爬虫规则' : '创建爬虫规则' }}</h2>
      <el-button @click="$router.push('/crawler')">返回列表</el-button>
    </div>
    
    <el-form :model="ruleForm" :rules="rules" ref="ruleFormRef" label-width="120px">
      <el-form-item label="规则名称" prop="ruleName">
        <el-input v-model="ruleForm.ruleName" placeholder="请输入规则名称"></el-input>
      </el-form-item>
      
      <el-form-item label="目标网站URL" prop="targetUrl">
        <el-input v-model="ruleForm.targetUrl" placeholder="请输入目标网站URL"></el-input>
      </el-form-item>
      
      <el-form-item label="列表选择器" prop="listSelector">
        <el-input v-model="ruleForm.listSelector" placeholder="请输入列表选择器，例如：.list-item"></el-input>
      </el-form-item>
      
      <el-form-item label="标题选择器" prop="titleSelector">
        <el-input v-model="ruleForm.titleSelector" placeholder="请输入标题选择器，例如：.title"></el-input>
      </el-form-item>
      
      <el-form-item label="内容选择器" prop="contentSelector">
        <el-input v-model="ruleForm.contentSelector" placeholder="请输入内容选择器，例如：.content"></el-input>
      </el-form-item>
      
      <el-form-item label="日期选择器">
        <el-input v-model="ruleForm.dateSelector" placeholder="请输入日期选择器，例如：.date"></el-input>
      </el-form-item>
      
      <el-form-item label="作者选择器">
        <el-input v-model="ruleForm.authorSelector" placeholder="请输入作者选择器，例如：.author"></el-input>
      </el-form-item>
      
      <el-form-item label="请求头">
        <el-input
          v-model="ruleForm.headers"
          type="textarea"
          :rows="4"
          placeholder="请输入请求头，JSON格式，例如：{'User-Agent': 'Mozilla/5.0'}"
        ></el-input>
      </el-form-item>
      
      <el-form-item label="状态">
        <el-radio-group v-model="ruleForm.status">
          <el-radio label="ENABLED">启用</el-radio>
          <el-radio label="DISABLED">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="submitForm">{{ isEdit ? '保存' : '创建' }}</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'CrawlerRule',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const ruleFormRef = ref(null)
    
    const ruleForm = reactive({
      ruleName: '',
      targetUrl: '',
      listSelector: '',
      titleSelector: '',
      contentSelector: '',
      dateSelector: '',
      authorSelector: '',
      headers: '',
      status: 'ENABLED'
    })
    
    const rules = {
      ruleName: [
        { required: true, message: '请输入规则名称', trigger: 'blur' },
        { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
      ],
      targetUrl: [
        { required: true, message: '请输入目标网站URL', trigger: 'blur' }
      ],
      listSelector: [
        { required: true, message: '请输入列表选择器', trigger: 'blur' }
      ],
      titleSelector: [
        { required: true, message: '请输入标题选择器', trigger: 'blur' }
      ],
      contentSelector: [
        { required: true, message: '请输入内容选择器', trigger: 'blur' }
      ]
    }
    
    const isEdit = computed(() => {
      return route.query.id !== undefined
    })
    
    // 获取规则详情
    const fetchRuleDetail = (id) => {
      // 模拟API请求
      setTimeout(() => {
        // 使用 id 参数模拟获取特定规则的详情
        console.log(`Fetching rule details for id: ${id}`)
        Object.assign(ruleForm, {
          ruleName: '知乎热门',
          targetUrl: 'https://www.zhihu.com/hot',
          listSelector: '.HotItem',
          titleSelector: '.HotItem-title',
          contentSelector: '.HotItem-excerpt',
          dateSelector: '',
          authorSelector: '',
          headers: '{"User-Agent": "Mozilla/5.0"}',
          status: 'ENABLED'
        })
      }, 500)
    }
    
    const submitForm = async () => {
      if (!ruleFormRef.value) return
      
      await ruleFormRef.value.validate((valid) => {
        if (valid) {
          // 模拟API请求
          setTimeout(() => {
            ElMessage.success(isEdit.value ? '规则更新成功' : '规则创建成功')
            router.push('/crawler')
          }, 500)
        }
      })
    }
    
    const resetForm = () => {
      if (ruleFormRef.value) {
        ruleFormRef.value.resetFields()
      }
    }
    
    onMounted(() => {
      if (isEdit.value) {
        fetchRuleDetail(route.query.id)
      }
    })
    
    return {
      ruleFormRef,
      ruleForm,
      rules,
      isEdit,
      submitForm,
      resetForm
    }
  }
}
</script>

<style scoped>
.crawler-rule-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style> 