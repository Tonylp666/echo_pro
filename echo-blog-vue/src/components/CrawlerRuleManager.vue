<template>
  <div class="crawler-rule-manager">
    <div class="rule-header">
      <h3>爬虫规则管理</h3>
      <el-button type="primary" @click="showAddRuleDialog">添加规则</el-button>
    </div>
    
    <el-table :data="rules" style="width: 100%" v-loading="loading">
      <el-table-column prop="ruleName" label="规则名称" />
      <el-table-column prop="targetUrl" label="目标网站" show-overflow-tooltip />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'ENABLED' ? 'success' : 'info'">
            {{ scope.row.status === 'ENABLED' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdTime" label="创建时间">
        <template #default="scope">
          {{ formatDate(scope.row.createdTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template #default="scope">
          <el-button 
            size="small" 
            type="primary" 
            @click="testRule(scope.row)"
            :loading="scope.row.testing">
            测试
          </el-button>
          <el-button 
            size="small" 
            :type="scope.row.status === 'ENABLED' ? 'warning' : 'success'"
            @click="toggleRuleStatus(scope.row)">
            {{ scope.row.status === 'ENABLED' ? '禁用' : '启用' }}
          </el-button>
          <el-button 
            size="small" 
            type="info" 
            @click="editRule(scope.row)">
            编辑
          </el-button>
          <el-button 
            size="small" 
            type="danger" 
            @click="deleteRule(scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 添加/编辑规则对话框 -->
    <el-dialog 
      :title="isEdit ? '编辑爬虫规则' : '添加爬虫规则'" 
      v-model="dialogVisible"
      width="650px">
      <el-form :model="ruleForm" label-width="120px" ref="ruleFormRef" :rules="rules">
        <el-form-item label="规则名称" prop="ruleName" :rules="[{ required: true, message: '请输入规则名称', trigger: 'blur' }]">
          <el-input v-model="ruleForm.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        
        <el-form-item label="目标网站URL" prop="targetUrl" :rules="[{ required: true, message: '请输入目标网站URL', trigger: 'blur' }]">
          <el-input v-model="ruleForm.targetUrl" placeholder="请输入目标网站URL" />
        </el-form-item>
        
        <el-form-item label="列表选择器" prop="listSelector" :rules="[{ required: true, message: '请输入列表选择器', trigger: 'blur' }]">
          <el-input v-model="ruleForm.listSelector" placeholder="请输入CSS选择器，例如: .article-list .item">
            <template #append>
              <el-tooltip content="CSS选择器，用于选择文章列表项" placement="top">
                <i class="el-icon-question">?</i>
              </el-tooltip>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="标题选择器" prop="titleSelector" :rules="[{ required: true, message: '请输入标题选择器', trigger: 'blur' }]">
          <el-input v-model="ruleForm.titleSelector" placeholder="请输入CSS选择器，例如: .title a" />
        </el-form-item>
        
        <el-form-item label="内容选择器" prop="contentSelector" :rules="[{ required: true, message: '请输入内容选择器', trigger: 'blur' }]">
          <el-input v-model="ruleForm.contentSelector" placeholder="请输入CSS选择器，例如: .article-content" />
        </el-form-item>
        
        <el-form-item label="日期选择器" prop="dateSelector">
          <el-input v-model="ruleForm.dateSelector" placeholder="请输入CSS选择器，例如: .publish-date" />
        </el-form-item>
        
        <el-form-item label="作者选择器" prop="authorSelector">
          <el-input v-model="ruleForm.authorSelector" placeholder="请输入CSS选择器，例如: .author" />
        </el-form-item>
        
        <el-form-item label="请求头" prop="headers">
          <el-input 
            v-model="ruleForm.headers" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入JSON格式的请求头，例如: {'User-Agent': 'Mozilla/5.0'}" />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="ruleForm.status">
            <el-radio label="ENABLED">启用</el-radio>
            <el-radio label="DISABLED">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveRule" :loading="saving">保存</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 测试结果对话框 -->
    <el-dialog 
      title="爬虫规则测试结果" 
      v-model="testResultVisible"
      width="800px">
      <div v-if="testLoading" class="test-loading">
        <el-skeleton :rows="10" animated />
      </div>
      <div v-else>
        <div v-if="testResults.length === 0" class="no-results">
          <el-empty description="未获取到数据，请检查爬虫规则" />
        </div>
        <div v-else class="test-results">
          <el-alert
            title="测试成功，以下是获取到的数据"
            type="success"
            :closable="false"
            style="margin-bottom: 15px;"
          />
          
          <el-collapse>
            <el-collapse-item 
              v-for="(result, index) in testResults" 
              :key="index"
              :title="result.title || '未获取到标题'"
              :name="index">
              <div class="result-item">
                <div v-if="result.title" class="result-field">
                  <strong>标题:</strong> {{ result.title }}
                </div>
                <div v-if="result.url" class="result-field">
                  <strong>链接:</strong> 
                  <el-link :href="result.url" target="_blank" type="primary">{{ result.url }}</el-link>
                </div>
                <div v-if="result.author" class="result-field">
                  <strong>作者:</strong> {{ result.author }}
                </div>
                <div v-if="result.date" class="result-field">
                  <strong>日期:</strong> {{ result.date }}
                </div>
                <div v-if="result.content" class="result-field">
                  <strong>内容预览:</strong>
                  <div class="content-preview">{{ result.content.substring(0, 200) }}...</div>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';
import moment from 'moment';

export default {
  name: 'CrawlerRuleManager',
  setup() {
    const rules = ref([]);
    const loading = ref(false);
    const dialogVisible = ref(false);
    const isEdit = ref(false);
    const saving = ref(false);
    const ruleFormRef = ref(null);
    const testResultVisible = ref(false);
    const testResults = ref([]);
    const testLoading = ref(false);
    
    // 规则表单
    const ruleForm = reactive({
      id: null,
      ruleName: '',
      targetUrl: '',
      listSelector: '',
      titleSelector: '',
      contentSelector: '',
      dateSelector: '',
      authorSelector: '',
      headers: '',
      status: 'ENABLED'
    });
    
    // 获取所有爬虫规则
    const fetchRules = async () => {
      loading.value = true;
      try {
        const response = await axios.get('/api/crawler/rules');
        rules.value = response.data;
      } catch (error) {
        console.error('获取爬虫规则失败:', error);
        ElMessage.error('获取爬虫规则失败');
      } finally {
        loading.value = false;
      }
    };
    
    // 显示添加规则对话框
    const showAddRuleDialog = () => {
      isEdit.value = false;
      resetRuleForm();
      dialogVisible.value = true;
    };
    
    // 编辑规则
    const editRule = (rule) => {
      isEdit.value = true;
      Object.keys(ruleForm).forEach(key => {
        if (key in rule) {
          ruleForm[key] = rule[key];
        }
      });
      dialogVisible.value = true;
    };
    
    // 重置表单
    const resetRuleForm = () => {
      if (ruleFormRef.value) {
        ruleFormRef.value.resetFields();
      }
      
      ruleForm.id = null;
      ruleForm.ruleName = '';
      ruleForm.targetUrl = '';
      ruleForm.listSelector = '';
      ruleForm.titleSelector = '';
      ruleForm.contentSelector = '';
      ruleForm.dateSelector = '';
      ruleForm.authorSelector = '';
      ruleForm.headers = '';
      ruleForm.status = 'ENABLED';
    };
    
    // 保存规则
    const saveRule = async () => {
      if (!ruleFormRef.value) return;
      
      await ruleFormRef.value.validate(async (valid) => {
        if (!valid) return;
        
        saving.value = true;
        try {
          if (isEdit.value) {
            await axios.put(`/api/crawler/rule/${ruleForm.id}`, ruleForm);
            ElMessage.success('更新规则成功');
          } else {
            await axios.post('/api/crawler/rule', ruleForm);
            ElMessage.success('添加规则成功');
          }
          
          dialogVisible.value = false;
          fetchRules();
        } catch (error) {
          console.error('保存规则失败:', error);
          ElMessage.error('保存规则失败');
        } finally {
          saving.value = false;
        }
      });
    };
    
    // 删除规则
    const deleteRule = (rule) => {
      ElMessageBox.confirm(
        `确定要删除规则 "${rule.ruleName}" 吗？`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(async () => {
        try {
          await axios.delete(`/api/crawler/rule/${rule.id}`);
          ElMessage.success('删除规则成功');
          fetchRules();
        } catch (error) {
          console.error('删除规则失败:', error);
          ElMessage.error('删除规则失败');
        }
      }).catch(() => {});
    };
    
    // 切换规则状态
    const toggleRuleStatus = async (rule) => {
      const newStatus = rule.status === 'ENABLED' ? 'DISABLED' : 'ENABLED';
      try {
        await axios.patch(`/api/crawler/rule/${rule.id}/status`, { status: newStatus });
        rule.status = newStatus;
        ElMessage.success(`规则已${newStatus === 'ENABLED' ? '启用' : '禁用'}`);
      } catch (error) {
        console.error('更新规则状态失败:', error);
        ElMessage.error('更新规则状态失败');
      }
    };
    
    // 测试规则
    const testRule = async (rule) => {
      rule.testing = true;
      testLoading.value = true;
      testResults.value = [];
      testResultVisible.value = true;
      
      try {
        const response = await axios.post(`/api/crawler/rule/${rule.id}/test`);
        testResults.value = response.data;
      } catch (error) {
        console.error('测试规则失败:', error);
        ElMessage.error('测试规则失败');
      } finally {
        rule.testing = false;
        testLoading.value = false;
      }
    };
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '';
      return moment(dateString).format('YYYY-MM-DD HH:mm:ss');
    };
    
    onMounted(() => {
      fetchRules();
    });
    
    return {
      rules,
      loading,
      dialogVisible,
      isEdit,
      ruleForm,
      ruleFormRef,
      saving,
      testResultVisible,
      testResults,
      testLoading,
      fetchRules,
      showAddRuleDialog,
      editRule,
      saveRule,
      deleteRule,
      toggleRuleStatus,
      testRule,
      formatDate
    };
  }
};
</script>

<style scoped>
.crawler-rule-manager {
  padding: 20px;
}

.rule-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.test-loading {
  padding: 20px;
}

.no-results {
  padding: 30px 0;
}

.test-results {
  max-height: 500px;
  overflow-y: auto;
}

.result-item {
  padding: 10px;
}

.result-field {
  margin-bottom: 10px;
}

.content-preview {
  margin-top: 5px;
  padding: 10px;
  background-color: #f8f8f8;
  border-radius: 4px;
  white-space: pre-wrap;
}
</style> 