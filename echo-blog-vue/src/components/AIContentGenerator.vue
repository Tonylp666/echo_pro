<template>
  <div class="ai-content-generator">
    <h3>AI内容生成</h3>
    
    <el-form :model="generationForm" label-width="100px" ref="formRef">
      <el-form-item label="任务名称" prop="taskName" :rules="[{ required: true, message: '请输入任务名称', trigger: 'blur' }]">
        <el-input v-model="generationForm.taskName" placeholder="请输入任务名称" />
      </el-form-item>
      
      <el-form-item label="任务类型" prop="taskType" :rules="[{ required: true, message: '请选择任务类型', trigger: 'change' }]">
        <el-select v-model="generationForm.taskType" placeholder="请选择任务类型" @change="handleTaskTypeChange">
          <el-option label="爬虫数据" value="CRAWLER" />
          <el-option label="搜索关键词" value="SEARCH" />
          <el-option label="热点话题" value="HOTSPOT" />
        </el-select>
      </el-form-item>
      
      <!-- 爬虫数据源 -->
      <template v-if="generationForm.taskType === 'CRAWLER'">
        <el-form-item label="爬虫规则" prop="crawlerRuleId" :rules="[{ required: true, message: '请选择爬虫规则', trigger: 'change' }]">
          <el-select v-model="generationForm.crawlerRuleId" placeholder="请选择爬虫规则" @change="fetchCrawlerData">
            <el-option 
              v-for="rule in crawlerRules" 
              :key="rule.id" 
              :label="rule.ruleName" 
              :value="rule.id" 
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="数据预览" v-if="crawlerData.length > 0">
          <el-table :data="crawlerData" style="width: 100%" max-height="300">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="url" label="链接" width="180">
              <template #default="scope">
                <el-link :href="scope.row.url" target="_blank" type="primary">查看原文</el-link>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-checkbox v-model="scope.row.selected" @change="updateSelectedData" />
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </template>
      
      <!-- 搜索关键词 -->
      <template v-if="generationForm.taskType === 'SEARCH'">
        <el-form-item label="搜索关键词" prop="searchKeywords" :rules="[{ required: true, message: '请输入搜索关键词', trigger: 'blur' }]">
          <el-input v-model="generationForm.searchKeywords" placeholder="请输入搜索关键词" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="searchKeywords" :loading="searching">搜索</el-button>
        </el-form-item>
        
        <el-form-item label="搜索结果" v-if="searchResults.length > 0">
          <el-table :data="searchResults" style="width: 100%" max-height="300">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="snippet" label="摘要" show-overflow-tooltip />
            <el-table-column prop="url" label="链接" width="180">
              <template #default="scope">
                <el-link :href="scope.row.url" target="_blank" type="primary">查看原文</el-link>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-checkbox v-model="scope.row.selected" @change="updateSelectedData" />
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </template>
      
      <!-- 热点话题 -->
      <template v-if="generationForm.taskType === 'HOTSPOT'">
        <el-form-item label="热点来源" prop="hotspotSource">
          <el-select v-model="generationForm.hotspotSource" placeholder="请选择热点来源" @change="fetchHotspots">
            <el-option label="百度热搜" value="BAIDU" />
            <el-option label="微博热搜" value="WEIBO" />
            <el-option label="知乎热榜" value="ZHIHU" />
            <el-option label="头条热榜" value="TOUTIAO" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="热点话题" v-if="hotspots.length > 0">
          <el-table :data="hotspots" style="width: 100%" max-height="300">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="hot" label="热度" width="100" />
            <el-table-column prop="url" label="链接" width="180">
              <template #default="scope">
                <el-link :href="scope.row.url" target="_blank" type="primary">查看详情</el-link>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-checkbox v-model="scope.row.selected" @change="updateSelectedData" />
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </template>
      
      <el-form-item label="AI模型" prop="aiModel" :rules="[{ required: true, message: '请选择AI模型', trigger: 'change' }]">
        <el-select v-model="generationForm.aiModel" placeholder="请选择AI模型">
          <el-option label="DeepSeek" value="DEEPSEEK" />
          <el-option label="阿里云百炼" value="ALIYUN" />
        </el-select>
      </el-form-item>
      
      <el-form-item label="提示词" prop="prompt">
        <el-input 
          v-model="generationForm.prompt" 
          type="textarea" 
          :rows="4" 
          placeholder="请输入提示词，指导AI生成内容的风格、结构等"
        />
      </el-form-item>
      
      <el-form-item label="文章分类" prop="articleCategoryId" :rules="[{ required: true, message: '请选择文章分类', trigger: 'change' }]">
        <el-select v-model="generationForm.articleCategoryId" placeholder="请选择文章分类">
          <el-option 
            v-for="category in articleCategories" 
            :key="category.id" 
            :label="category.categoryName" 
            :value="category.id" 
          />
        </el-select>
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="submitGenerationTask" :loading="submitting">
          创建生成任务
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';

export default {
  name: 'AIContentGenerator',
  setup() {
    const formRef = ref(null);
    const submitting = ref(false);
    const searching = ref(false);
    
    // 表单数据
    const generationForm = reactive({
      taskName: '',
      taskType: '',
      crawlerRuleId: null,
      searchKeywords: '',
      hotspotSource: '',
      aiModel: '',
      prompt: '',
      articleCategoryId: null,
      sourceData: ''
    });
    
    // 数据源
    const crawlerRules = ref([]);
    const crawlerData = ref([]);
    const searchResults = ref([]);
    const hotspots = ref([]);
    const articleCategories = ref([]);
    
    // 获取爬虫规则列表
    const fetchCrawlerRules = async () => {
      try {
        const response = await axios.get('/api/crawler/rules');
        crawlerRules.value = response.data;
      } catch (error) {
        console.error('获取爬虫规则失败:', error);
        ElMessage.error('获取爬虫规则失败');
      }
    };
    
    // 获取文章分类
    const fetchArticleCategories = async () => {
      try {
        const response = await axios.get('/api/article/categories');
        articleCategories.value = response.data;
      } catch (error) {
        console.error('获取文章分类失败:', error);
        ElMessage.error('获取文章分类失败');
      }
    };
    
    // 获取爬虫数据
    const fetchCrawlerData = async () => {
      if (!generationForm.crawlerRuleId) return;
      
      try {
        const response = await axios.get(`/api/crawler/data/${generationForm.crawlerRuleId}`);
        crawlerData.value = response.data.map(item => ({
          ...item,
          selected: true
        }));
        updateSelectedData();
      } catch (error) {
        console.error('获取爬虫数据失败:', error);
        ElMessage.error('获取爬虫数据失败');
      }
    };
    
    // 搜索关键词
    const searchKeywords = async () => {
      if (!generationForm.searchKeywords) {
        ElMessage.warning('请输入搜索关键词');
        return;
      }
      
      searching.value = true;
      try {
        const response = await axios.get('/api/search', {
          params: { keywords: generationForm.searchKeywords }
        });
        searchResults.value = response.data.map(item => ({
          ...item,
          selected: true
        }));
        updateSelectedData();
      } catch (error) {
        console.error('搜索失败:', error);
        ElMessage.error('搜索失败');
      } finally {
        searching.value = false;
      }
    };
    
    // 获取热点话题
    const fetchHotspots = async () => {
      if (!generationForm.hotspotSource) return;
      
      try {
        const response = await axios.get(`/api/hotspot/${generationForm.hotspotSource}`);
        hotspots.value = response.data.map(item => ({
          ...item,
          selected: true
        }));
        updateSelectedData();
      } catch (error) {
        console.error('获取热点话题失败:', error);
        ElMessage.error('获取热点话题失败');
      }
    };
    
    // 更新选中的数据
    const updateSelectedData = () => {
      let selectedData = [];
      
      if (generationForm.taskType === 'CRAWLER') {
        selectedData = crawlerData.value.filter(item => item.selected);
      } else if (generationForm.taskType === 'SEARCH') {
        selectedData = searchResults.value.filter(item => item.selected);
      } else if (generationForm.taskType === 'HOTSPOT') {
        selectedData = hotspots.value.filter(item => item.selected);
      }
      
      generationForm.sourceData = JSON.stringify(selectedData);
    };
    
    // 任务类型变更
    const handleTaskTypeChange = () => {
      // 重置相关字段
      generationForm.crawlerRuleId = null;
      generationForm.searchKeywords = '';
      generationForm.hotspotSource = '';
      generationForm.sourceData = '';
      
      crawlerData.value = [];
      searchResults.value = [];
      hotspots.value = [];
    };
    
    // 提交生成任务
    const submitGenerationTask = async () => {
      if (!formRef.value) return;
      
      await formRef.value.validate(async (valid) => {
        if (!valid) return;
        
        // 检查是否有选中的数据源
        if (!generationForm.sourceData || JSON.parse(generationForm.sourceData).length === 0) {
          ElMessage.warning('请至少选择一条数据作为生成源');
          return;
        }
        
        submitting.value = true;
        try {
          await axios.post('/api/ai/generation/task', generationForm);
          ElMessage.success('生成任务创建成功');
          
          // 重置表单
          formRef.value.resetFields();
          crawlerData.value = [];
          searchResults.value = [];
          hotspots.value = [];
        } catch (error) {
          console.error('创建生成任务失败:', error);
          ElMessage.error('创建生成任务失败');
        } finally {
          submitting.value = false;
        }
      });
    };
    
    onMounted(() => {
      fetchCrawlerRules();
      fetchArticleCategories();
    });
    
    return {
      formRef,
      generationForm,
      crawlerRules,
      crawlerData,
      searchResults,
      hotspots,
      articleCategories,
      submitting,
      searching,
      fetchCrawlerData,
      searchKeywords,
      fetchHotspots,
      handleTaskTypeChange,
      updateSelectedData,
      submitGenerationTask
    };
  }
};
</script>

<style scoped>
.ai-content-generator {
  margin-top: 20px;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}
</style> 