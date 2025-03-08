<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="user-card">
          <div class="user-info">
            <div class="avatar-container">
              <img :src="userInfo.avatar || defaultAvatar" class="avatar" alt="avatar">
              <el-upload
                class="avatar-uploader"
                action="/api/upload/avatar"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <el-button size="small" type="primary" class="upload-btn">更换头像</el-button>
              </el-upload>
            </div>
            <h3 class="username">{{ userInfo.nickname || userInfo.username }}</h3>
            <p class="email">{{ userInfo.email }}</p>
            <div class="user-roles">
              <el-tag v-for="role in userInfo.roles" :key="role" size="small" class="role-tag">
                {{ getRoleName(role) }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="16">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-card>
              <el-form :model="basicForm" :rules="basicRules" ref="basicFormRef" label-width="100px">
                <el-form-item label="用户名">
                  <el-input v-model="basicForm.username" disabled></el-input>
                </el-form-item>
                
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="basicForm.nickname" placeholder="请输入昵称"></el-input>
                </el-form-item>
                
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="basicForm.email" placeholder="请输入邮箱"></el-input>
                </el-form-item>
                
                <el-form-item>
                  <el-button type="primary" @click="updateBasicInfo">保存</el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-tab-pane>
          
          <el-tab-pane label="修改密码" name="password">
            <el-card>
              <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" show-password></el-input>
                </el-form-item>
                
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password></el-input>
                </el-form-item>
                
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" show-password></el-input>
                </el-form-item>
                
                <el-form-item>
                  <el-button type="primary" @click="updatePassword">保存</el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-tab-pane>
          
          <el-tab-pane label="账号安全" name="security">
            <el-card>
              <div class="security-item">
                <div class="security-info">
                  <h4>账号密码</h4>
                  <p>定期修改密码可以提高账号安全性</p>
                </div>
                <el-button @click="activeTab = 'password'">修改</el-button>
              </div>
              
              <div class="security-item">
                <div class="security-info">
                  <h4>邮箱绑定</h4>
                  <p>已绑定邮箱：{{ userInfo.email }}</p>
                </div>
                <el-button @click="activeTab = 'basic'">修改</el-button>
              </div>
            </el-card>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'Profile',
  setup() {
    const activeTab = ref('basic')
    const basicFormRef = ref(null)
    const passwordFormRef = ref(null)
    const defaultAvatar = '/src/assets/logo.png'
    
    // 模拟用户数据
    const userInfo = reactive({
      username: 'admin',
      nickname: '管理员',
      email: 'admin@example.com',
      avatar: '',
      roles: ['ADMIN', 'USER']
    })
    
    const basicForm = reactive({
      username: '',
      nickname: '',
      email: ''
    })
    
    const passwordForm = reactive({
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
    
    const basicRules = {
      nickname: [
        { required: true, message: '请输入昵称', trigger: 'blur' },
        { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
      ]
    }
    
    const passwordRules = {
      oldPassword: [
        { required: true, message: '请输入原密码', trigger: 'blur' }
      ],
      newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请确认新密码', trigger: 'blur' },
        {
          validator: (rule, value, callback) => {
            if (value !== passwordForm.newPassword) {
              callback(new Error('两次输入的密码不一致'))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
      ]
    }
    
    // 获取用户信息
    const fetchUserInfo = () => {
      // 模拟API请求
      setTimeout(() => {
        Object.assign(basicForm, {
          username: userInfo.username,
          nickname: userInfo.nickname,
          email: userInfo.email
        })
      }, 500)
    }
    
    // 更新基本信息
    const updateBasicInfo = async () => {
      if (!basicFormRef.value) return
      
      await basicFormRef.value.validate((valid) => {
        if (valid) {
          // 模拟API请求
          setTimeout(() => {
            Object.assign(userInfo, {
              nickname: basicForm.nickname,
              email: basicForm.email
            })
            ElMessage.success('基本信息更新成功')
          }, 500)
        }
      })
    }
    
    // 更新密码
    const updatePassword = async () => {
      if (!passwordFormRef.value) return
      
      await passwordFormRef.value.validate((valid) => {
        if (valid) {
          // 模拟API请求
          setTimeout(() => {
            ElMessage.success('密码修改成功')
            passwordForm.oldPassword = ''
            passwordForm.newPassword = ''
            passwordForm.confirmPassword = ''
          }, 500)
        }
      })
    }
    
    // 处理头像上传成功
    const handleAvatarSuccess = (res) => {
      userInfo.avatar = res.data.url
      ElMessage.success('头像上传成功')
    }
    
    // 上传前检查
    const beforeAvatarUpload = (file) => {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2
      
      if (!isImage) {
        ElMessage.error('上传头像图片只能是图片格式!')
      }
      
      if (!isLt2M) {
        ElMessage.error('上传头像图片大小不能超过 2MB!')
      }
      
      return isImage && isLt2M
    }
    
    // 获取角色名称
    const getRoleName = (role) => {
      const roleMap = {
        'ADMIN': '管理员',
        'USER': '普通用户',
        'EDITOR': '编辑'
      }
      return roleMap[role] || role
    }
    
    onMounted(() => {
      fetchUserInfo()
    })
    
    return {
      activeTab,
      userInfo,
      basicForm,
      passwordForm,
      basicFormRef,
      passwordFormRef,
      basicRules,
      passwordRules,
      defaultAvatar,
      updateBasicInfo,
      updatePassword,
      handleAvatarSuccess,
      beforeAvatarUpload,
      getRoleName
    }
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.user-card {
  text-align: center;
  height: 100%;
}

.avatar-container {
  margin-bottom: 20px;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  margin-bottom: 10px;
}

.upload-btn {
  margin-top: 10px;
}

.username {
  font-size: 20px;
  font-weight: 500;
  margin: 10px 0;
}

.email {
  color: #909399;
  margin-bottom: 15px;
}

.user-roles {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
}

.role-tag {
  margin: 0 5px 5px 0;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.security-item:last-child {
  border-bottom: none;
}

.security-info h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
}

.security-info p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}
</style> 