<template>
  <div class="platform-publisher">
    <h3>发布到平台</h3>
    
    <el-form label-width="100px">
      <el-form-item label="选择平台">
        <el-checkbox-group v-model="selectedPlatforms">
          <el-checkbox v-for="platform in availablePlatforms" 
                      :key="platform.type" 
                      :label="platform.type"
                      :disabled="!platform.authorized">
            {{ platform.name }}
            <el-tag v-if="platform.authorized" type="success" size="small">已授权</el-tag>
            <el-tag v-else type="danger" size="small">未授权</el-tag>
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      
      <el-form-item v-if="selectedPlatforms.length > 0">
        <el-button type="primary" @click="publishToPlatforms" :loading="publishing">
          一键发布
        </el-button>
      </el-form-item>
    </el-form>
    
    <div v-if="selectedPlatforms.length > 0" class="platform-settings">
      <el-collapse>
        <el-collapse-item v-for="platform in selectedPlatforms" :key="platform" :title="getPlatformName(platform)">
          <div class="platform-config">
            <el-form :model="platformConfigs[platform]" label-width="120px">
              <!-- 微信公众号特有设置 -->
              <template v-if="platform === 'WECHAT'">
                <el-form-item label="原创声明">
                  <el-switch v-model="platformConfigs[platform].original" />
                </el-form-item>
                <el-form-item label="评论设置">
                  <el-radio-group v-model="platformConfigs[platform].commentType">
                    <el-radio :label="0">全部人可评论</el-radio>
                    <el-radio :label="1">仅关注人可评论</el-radio>
                    <el-radio :label="2">关闭评论</el-radio>
                  </el-radio-group>
                </el-form-item>
              </template>
              
              <!-- 知乎特有设置 -->
              <template v-if="platform === 'ZHIHU'">
                <el-form-item label="文章可见性">
                  <el-select v-model="platformConfigs[platform].visibility">
                    <el-option label="公开" value="public" />
                    <el-option label="仅自己可见" value="self" />
                  </el-select>
                </el-form-item>
              </template>
              
              <!-- 头条特有设置 -->
              <template v-if="platform === 'TOUTIAO'">
                <el-form-item label="允许转载">
                  <el-switch v-model="platformConfigs[platform].canRepost" />
                </el-form-item>
              </template>
              
              <!-- 小红书特有设置 -->
              <template v-if="platform === 'XIAOHONGSHU'">
                <el-form-item label="话题标签">
                  <el-select
                    v-model="platformConfigs[platform].tags"
                    multiple
                    filterable
                    allow-create
                    default-first-option
                    placeholder="请输入话题标签">
                  </el-select>
                </el-form-item>
              </template>
              
              <!-- 所有平台通用设置 -->
              <el-form-item label="发布时间">
                <el-radio-group v-model="platformConfigs[platform].publishTime">
                  <el-radio label="now">立即发布</el-radio>
                  <el-radio label="scheduled">定时发布</el-radio>
                </el-radio-group>
                <el-date-picker
                  v-if="platformConfigs[platform].publishTime === 'scheduled'"
                  v-model="platformConfigs[platform].scheduledTime"
                  type="datetime"
                  placeholder="选择发布时间"
                  :disabled-date="disabledDate"
                  :disabled-hours="disabledHours"
                />
              </el-form-item>
            </el-form>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
    
    <el-dialog
      title="发布结果"
      v-model="showResultDialog"
      width="500px">
      <div class="publish-results">
        <div v-for="result in publishResults" :key="result.platform" class="result-item">
          <el-alert
            :title="getPlatformName(result.platform)"
            :type="result.success ? 'success' : 'error'"
            :description="result.message"
            show-icon
          />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showResultDialog = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';

export default {
  name: 'PlatformPublisher',
  props: {
    articleId: {
      type: Number,
      required: true
    }
  },
  setup(props) {
    const selectedPlatforms = ref([]);
    const availablePlatforms = ref([]);
    const publishing = ref(false);
    const showResultDialog = ref(false);
    const publishResults = ref([]);
    
    // 平台配置
    const platformConfigs = reactive({
      WECHAT: {
        original: true,
        commentType: 0,
        publishTime: 'now',
        scheduledTime: null
      },
      ZHIHU: {
        visibility: 'public',
        publishTime: 'now',
        scheduledTime: null
      },
      TOUTIAO: {
        canRepost: true,
        publishTime: 'now',
        scheduledTime: null
      },
      XIAOHONGSHU: {
        tags: [],
        publishTime: 'now',
        scheduledTime: null
      }
    });
    
    // 获取平台名称
    const getPlatformName = (platformType) => {
      const platformMap = {
        WECHAT: '微信公众号',
        ZHIHU: '知乎',
        TOUTIAO: '今日头条',
        XIAOHONGSHU: '小红书'
      };
      return platformMap[platformType] || platformType;
    };
    
    // 获取用户已授权的平台
    const fetchAuthorizedPlatforms = async () => {
      try {
        const response = await axios.get('/api/user/platforms');
        const platforms = [
          { type: 'WECHAT', name: '微信公众号', authorized: false },
          { type: 'ZHIHU', name: '知乎', authorized: false },
          { type: 'TOUTIAO', name: '今日头条', authorized: false },
          { type: 'XIAOHONGSHU', name: '小红书', authorized: false }
        ];
        
        // 更新授权状态
        response.data.forEach(platform => {
          const index = platforms.findIndex(p => p.type === platform.platformType);
          if (index !== -1) {
            platforms[index].authorized = true;
          }
        });
        
        availablePlatforms.value = platforms;
      } catch (error) {
        console.error('获取授权平台失败:', error);
        ElMessage.error('获取授权平台失败');
      }
    };
    
    // 发布到选中的平台
    const publishToPlatforms = async () => {
      if (selectedPlatforms.value.length === 0) {
        ElMessage.warning('请至少选择一个平台');
        return;
      }
      
      publishing.value = true;
      publishResults.value = [];
      
      try {
        const requests = selectedPlatforms.value.map(platform => {
          return axios.post('/api/article/publish', {
            articleId: props.articleId,
            platformType: platform,
            config: platformConfigs[platform]
          }).then(response => {
            return {
              platform,
              success: true,
              message: '发布成功'
            };
          }).catch(error => {
            return {
              platform,
              success: false,
              message: error.response?.data?.message || '发布失败'
            };
          });
        });
        
        const results = await Promise.all(requests);
        publishResults.value = results;
        showResultDialog.value = true;
        
        // 检查是否全部成功
        const allSuccess = results.every(result => result.success);
        if (allSuccess) {
          ElMessage.success('所有平台发布成功');
        } else {
          ElMessage.warning('部分平台发布失败，请查看详情');
        }
      } catch (error) {
        console.error('发布失败:', error);
        ElMessage.error('发布失败');
      } finally {
        publishing.value = false;
      }
    };
    
    // 禁用过去的日期
    const disabledDate = (time) => {
      return time.getTime() < Date.now() - 8.64e7; // 禁用今天之前的日期
    };
    
    // 禁用部分小时
    const disabledHours = () => {
      return [0, 1, 2, 3, 4, 5]; // 禁用凌晨0-5点
    };
    
    onMounted(() => {
      fetchAuthorizedPlatforms();
    });
    
    return {
      selectedPlatforms,
      availablePlatforms,
      platformConfigs,
      publishing,
      showResultDialog,
      publishResults,
      getPlatformName,
      publishToPlatforms,
      disabledDate,
      disabledHours
    };
  }
};
</script>

<style scoped>
.platform-publisher {
  margin-top: 20px;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.platform-settings {
  margin-top: 20px;
}

.platform-config {
  padding: 10px;
}

.publish-results {
  max-height: 300px;
  overflow-y: auto;
}

.result-item {
  margin-bottom: 10px;
}
</style> 