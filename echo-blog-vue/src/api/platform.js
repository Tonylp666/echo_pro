import request from '@/utils/request'

// 获取平台配置列表
export function getPlatformConfigs(params) {
  return request({
    url: '/platforms',
    method: 'get',
    params
  })
}

// 获取平台配置详情
export function getPlatformConfig(id) {
  return request({
    url: `/platforms/${id}`,
    method: 'get'
  })
}

// 创建平台配置
export function createPlatformConfig(data) {
  return request({
    url: '/platforms',
    method: 'post',
    data
  })
}

// 更新平台配置
export function updatePlatformConfig(id, data) {
  return request({
    url: `/platforms/${id}`,
    method: 'put',
    data
  })
}

// 删除平台配置
export function deletePlatformConfig(id) {
  return request({
    url: `/platforms/${id}`,
    method: 'delete'
  })
}

// 获取平台类型列表
export function getPlatformTypes() {
  return request({
    url: '/platforms/types',
    method: 'get'
  })
}

// 发布文章到平台
export function publishArticle(articleId, platformId) {
  return request({
    url: '/article-publish',
    method: 'post',
    data: {
      articleId,
      platformId
    }
  })
}

// 获取文章发布状态
export function getArticlePublishStatus(articleId) {
  return request({
    url: `/article-publish/status/${articleId}`,
    method: 'get'
  })
} 