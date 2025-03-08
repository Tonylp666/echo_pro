<template>
  <div class="category-container app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>分类管理</span>
          <el-button type="primary" @click="handleCreateCategory">
            <el-icon><Plus /></el-icon>
            添加分类
          </el-button>
        </div>
      </template>
      
      <el-table
        v-loading="loading"
        :data="categoryList"
        style="width: 100%"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ENABLED' ? 'success' : 'danger'" size="small">
              {{ row.status === 'ENABLED' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEditCategory(row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteCategory(row.id)"
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
    
    <!-- 分类表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑分类' : '添加分类'"
      width="500px"
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryForm"
        :rules="categoryRules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="categoryForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
          />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="categoryForm.status">
            <el-radio label="ENABLED">启用</el-radio>
            <el-radio label="DISABLED">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategories, getCategory, createCategory, updateCategory, deleteCategory } from '@/api/category'

// 分类列表
const categoryList = ref([])
const loading = ref(false)
const total = ref(0)

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10
})

// 分类表单
const categoryFormRef = ref(null)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const categoryForm = reactive({
  id: null,
  name: '',
  description: '',
  status: 'ENABLED'
})

// 表单验证规则
const categoryRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ]
}

// 获取分类列表
const getCategoryList = async () => {
  loading.value = true
  try {
    const { data } = await getCategories({
      current: queryParams.current,
      size: queryParams.size
    })
    categoryList.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

// 添加分类
const handleCreateCategory = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑分类
const handleEditCategory = async (row) => {
  isEdit.value = true
  resetForm()
  
  try {
    const { data } = await getCategory(row.id)
    Object.assign(categoryForm, data)
    dialogVisible.value = true
  } catch (error) {
    console.error('获取分类详情失败:', error)
    ElMessage.error('获取分类详情失败')
  }
}

// 删除分类
const handleDeleteCategory = (id) => {
  ElMessageBox.confirm('确定要删除该分类吗？删除后不可恢复！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCategory(id)
      ElMessage.success('删除成功')
      getCategoryList()
    } catch (error) {
      console.error('删除分类失败:', error)
      ElMessage.error('删除分类失败')
    }
  }).catch(() => {})
}

// 提交表单
const submitForm = () => {
  categoryFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateCategory(categoryForm.id, categoryForm)
          ElMessage.success('更新成功')
        } else {
          await createCategory(categoryForm)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        getCategoryList()
      } catch (error) {
        console.error('保存分类失败:', error)
        ElMessage.error('保存分类失败')
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
  categoryForm.id = null
  categoryForm.name = ''
  categoryForm.description = ''
  categoryForm.status = 'ENABLED'
  
  if (categoryFormRef.value) {
    categoryFormRef.value.resetFields()
  }
}

// 分页处理
const handleSizeChange = (size) => {
  queryParams.size = size
  getCategoryList()
}

const handleCurrentChange = (current) => {
  queryParams.current = current
  getCategoryList()
}

onMounted(() => {
  getCategoryList()
})
</script>

<style lang="scss" scoped>
.category-container {
  .pagination-container {
    margin-top: 20px;
  }
}
</style> 