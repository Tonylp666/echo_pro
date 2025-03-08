<template>
  <div class="platform-container">
    <div class="page-header">
      <h2>平台管理</h2>
      <el-button type="primary" @click="showAddPlatformDialog">
        <el-icon><Plus /></el-icon> 添加平台授权
      </el-button>
    </div>
    
    <el-table
      v-loading="loading"
      :data="platforms"
      style="width: 100%; margin-top: 20px;"
      border
    >
      <el-table-column prop="platformType" label="平台类型" width="150">
        <template #default="scope">
          {{ getPlatformName(scope.row.platformType) }}
        </template>
      </el-table-column>
      
      <el-table-column prop="platformUserName" label="平台用户名" min-width="150" />
      
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column prop="expireTime" label="过期时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.expireTime) }}
        </template>
      </el-table-column>
      
      <el-table-column prop="createdTime" label="创建时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createdTime) }}
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button
            size="small"
            type="primary"
            @click="editPlatform(scope.row)"
          >
            编辑
          </el-button>
          
          <el-button
            size="small"
            type="danger"
            @click="deletePlatform(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 添加/编辑平台对话框 -->
    <el-dialog
      :title="isEdit ? '编辑平台授权' : '添加平台授权'"
      v-model="platformDialogVisible"
      width="500px"
    >
      <el-form :model="platformForm" :rules="rules" ref="platformFormRef" label-width="100px">
        <el-form-item label="平台类型" prop="platformType">
          <el-select v-model="platformForm.platformType" placeholder="请选择平台类型" style="width: 100%;" :disabled="isEdit">
            <el-option label="微信公众号" value="WECHAT_MP" />
            <el-option label="知乎" value="ZHIHU" />
            <el-option label="今日头条" value="TOUTIAO" />
            <el-option label="小红书" value="XIAOHONGSHU" />
            <el-option label="掘金" value="JUEJIN" />
            <el-option label="CSDN" value="CSDN" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="授权令牌" prop="authToken">
          <el-input v-model="platformForm.authToken" placeholder="请输入授权令牌" type="password" show-password />
        </el-form-item>
        
        <el-form-item label="刷新令牌">
          <el-input v-model="platformForm.refreshToken" placeholder="请输入刷新令牌" type="password" show-password />
        </el-form-item>
        
        <el-form-item label="平台用户ID">
          <el-input v-model="platformForm.platformUserId" placeholder="请输入平台用户ID" />
        </el-form-item>
        
        <el-form-item label="平台用户名">
          <el-input v-model="platformForm.platformUserName" placeholder="请输入平台用户名" />
        </el-form-item>
        
        <el-form-item label="过期时间">
          <el-date-picker
            v-model="platformForm.expireTime"
            type="datetime"
            placeholder="选择过期时间"
            style="width: 100%;"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="platformDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPlatformForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import moment from 'moment'

export default {
  name: 'PlatformIndex',
  setup() {
    const loading = ref(false)
    const platforms = ref([])
    const platformDialogVisible = ref(false)
    const platformFormRef = ref(null)
    const isEdit = ref(false)
    
    const platformForm = reactive({
      id: null,
      platformType: '',
      authToken: '',
      refreshToken: '',
      platformUserId: '',
      platformUserName: '',
      expireTime: ''
    })
    
    const rules = {
      platformType: [
        { required: true, message: '请选择平台类型', trigger: 'change' }
      ],
      authToken: [
        { required: true, message: '请输入授权令牌', trigger: 'blur' }
      ]
    }
    
    // 模拟数据
    const mockPlatforms = [
      {
        id: 1,
        platformType: 'WECHAT_MP',
        platformUserName: '测试公众号',
        status: 'ACTIVE',
        expireTime: '2023-12-31 23:59:59',
        createdTime: '2023-01-01 12:00:00'
      },
      {
        id: 2,
        platformType: 'ZHIHU',
        platformUserName: '测试用户',
        status: 'ACTIVE',
        expireTime: '2023-12-31 23:59:59',
        createdTime: '2023-01-02 12:00:00'
      },
      {
        id: 3,
        platformType: 'TOUTIAO',
        platformUserName: '头条号',
        status: 'EXPIRED',
        expireTime: '2022-12-31 23:59:59',
        createdTime: '2023-01-03 12:00:00'
      }
    ]
    
    // 获取平台列表
    const fetchPlatforms = () => {
      loading.value = true
      
      // 模拟API请求
      setTimeout(() => {
        platforms.value = mockPlatforms
        loading.value = false
      }, 500)
    }
    
    // 显示添加平台对话框
    const showAddPlatformDialog = () => {
      isEdit.value = false
      resetPlatformForm()
      platformDialogVisible.value = true
    }
    
    // 编辑平台
    const editPlatform = (platform) => {
      isEdit.value = true
      Object.assign(platformForm, {
        id: platform.id,
        platformType: platform.platformType,
        authToken: '******',
        refreshToken: '******',
        platformUserId: platform.platformUserId || '',
        platformUserName: platform.platformUserName || '',
        expireTime: platform.expireTime ? new Date(platform.expireTime) : ''
      })
      platformDialogVisible.value = true
    }
    
    // 删除平台
    const deletePlatform = (id) => {
      ElMessageBox.confirm('确定要删除该平台授权吗？', '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 模拟API请求
        platforms.value = platforms.value.filter(item => item.id !== id)
        ElMessage.success('删除成功')
      }).catch(() => {})
    }
    
    // 提交平台表单
    const submitPlatformForm = async () => {
      if (!platformFormRef.value) return
      
      await platformFormRef.value.validate((valid) => {
        if (valid) {
          // 模拟API请求
          setTimeout(() => {
            if (isEdit.value) {
              const index = platforms.value.findIndex(item => item.id === platformForm.id)
              if (index !== -1) {
                platforms.value[index] = {
                  ...platforms.value[index],
                  platformUserName: platformForm.platformUserName,
                  expireTime: platformForm.expireTime ? moment(platformForm.expireTime).format('YYYY-MM-DD HH:mm:ss') : null
                }
              }
              ElMessage.success('平台授权更新成功')
            } else {
              platforms.value.push({
                id: Date.now(),
                platformType: platformForm.platformType,
                platformUserName: platformForm.platformUserName,
                status: 'ACTIVE',
                expireTime: platformForm.expireTime ? moment(platformForm.expireTime).format('YYYY-MM-DD HH:mm:ss') : null,
                createdTime: moment().format('YYYY-MM-DD HH:mm:ss')
              })
              ElMessage.success('平台授权添加成功')
            }
            platformDialogVisible.value = false
          }, 500)
        }
      })
    }
    
    // 重置平台表单
    const resetPlatformForm = () => {
      Object.assign(platformForm, {
        id: null,
        platformType: '',
        authToken: '',
        refreshToken: '',
        platformUserId: '',
        platformUserName: '',
        expireTime: ''
      })
      if (platformFormRef.value) {
        platformFormRef.value.resetFields()
      }
    }
    
    // 获取平台名称
    const getPlatformName = (type) => {
      const platformMap = {
        'WECHAT_MP': '微信公众号',
        'ZHIHU': '知乎',
        'TOUTIAO': '今日头条',
        'XIAOHONGSHU': '小红书',
        'JUEJIN': '掘金',
        'CSDN': 'CSDN'
      }
      return platformMap[type] || type
    }
    
    // 获取状态类型
    const getStatusType = (status) => {
      switch (status) {
        case 'ACTIVE':
          return 'success'
        case 'EXPIRED':
          return 'danger'
        default:
          return 'info'
      }
    }
    
    // 获取状态文本
    const getStatusText = (status) => {
      switch (status) {
        case 'ACTIVE':
          return '有效'
        case 'EXPIRED':
          return '已过期'
        default:
          return status
      }
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return ''
      return moment(dateString).format('YYYY-MM-DD HH:mm:ss')
    }
    
    onMounted(() => {
      fetchPlatforms()
    })
    
    return {
      loading,
      platforms,
      platformDialogVisible,
      platformFormRef,
      platformForm,
      rules,
      isEdit,
      showAddPlatformDialog,
      editPlatform,
      deletePlatform,
      submitPlatformForm,
      getPlatformName,
      getStatusType,
      getStatusText,
      formatDate
    }
  }
}
</script>

<style scoped>
.platform-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style> 