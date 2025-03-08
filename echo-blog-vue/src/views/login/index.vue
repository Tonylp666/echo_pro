<template>
  <div class="login-container">
    <div class="login-form-container">
      <div class="title-container">
        <h3 class="title">Echo博客管理系统</h3>
      </div>
      
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            type="text"
            prefix-icon="User"
            autocomplete="off"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            placeholder="密码"
            :type="passwordVisible ? 'text' : 'password'"
            prefix-icon="Lock"
            autocomplete="off"
          >
            <template #suffix>
              <el-icon class="password-icon" @click="passwordVisible = !passwordVisible">
                <View v-if="passwordVisible" />
                <Hide v-else />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button :loading="loading" type="primary" class="login-button" @click="handleLogin">
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
        
        <div class="tips">
          <span>用户名: admin</span>
          <span>密码: 123456</span>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

// 登录表单
const loginFormRef = ref(null)
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 密码可见性
const passwordVisible = ref(false)

// 加载状态
const loading = ref(false)

// 处理登录
const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.login(loginForm)
        await userStore.getUserInfo()
        ElMessage.success('登录成功')
        router.push('/')
      } catch (error) {
        ElMessage.error(error.message || '登录失败，请重试')
      } finally {
        loading.value = false
      }
    } else {
      return false
    }
  })
}
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #2d3a4b;
  
  .login-form-container {
    width: 350px;
    padding: 30px 35px;
    background-color: #fff;
    border-radius: 5px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    
    .title-container {
      text-align: center;
      margin-bottom: 30px;
      
      .title {
        font-size: 26px;
        color: #303133;
        margin: 0;
      }
    }
    
    .login-form {
      .password-icon {
        cursor: pointer;
      }
      
      .login-button {
        width: 100%;
      }
      
      .tips {
        margin-top: 15px;
        display: flex;
        justify-content: space-between;
        color: #909399;
        font-size: 13px;
      }
    }
  }
}
</style> 