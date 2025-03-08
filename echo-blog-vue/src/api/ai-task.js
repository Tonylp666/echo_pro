import request from '@/utils/request'

// 获取AI生成任务列表
export function getAITasks(params) {
  return request({
    url: '/ai-tasks',
    method: 'get',
    params
  })
}

// 获取AI生成任务详情
export function getAITask(id) {
  return request({
    url: `/ai-tasks/${id}`,
    method: 'get'
  })
}

// 创建AI生成任务
export function createAITask(data) {
  return request({
    url: '/ai-tasks',
    method: 'post',
    data
  })
}

// 更新AI生成任务
export function updateAITask(id, data) {
  return request({
    url: `/ai-tasks/${id}`,
    method: 'put',
    data
  })
}

// 删除AI生成任务
export function deleteAITask(id) {
  return request({
    url: `/ai-tasks/${id}`,
    method: 'delete'
  })
}

// 重试AI生成任务
export function retryAITask(id) {
  return request({
    url: `/ai-tasks/${id}/retry`,
    method: 'post'
  })
}

// 获取AI模型列表
export function getAIModels() {
  return request({
    url: '/ai-models',
    method: 'get'
  })
} 