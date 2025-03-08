import request from '@/utils/request'

// 获取爬虫源列表
export function getCrawlerSources(params) {
  return request({
    url: '/crawler/sources',
    method: 'get',
    params
  })
}

// 获取爬虫源详情
export function getCrawlerSource(id) {
  return request({
    url: `/crawler/sources/${id}`,
    method: 'get'
  })
}

// 创建爬虫源
export function createCrawlerSource(data) {
  return request({
    url: '/crawler/sources',
    method: 'post',
    data
  })
}

// 更新爬虫源
export function updateCrawlerSource(id, data) {
  return request({
    url: `/crawler/sources/${id}`,
    method: 'put',
    data
  })
}

// 删除爬虫源
export function deleteCrawlerSource(id) {
  return request({
    url: `/crawler/sources/${id}`,
    method: 'delete'
  })
}

// 获取爬虫规则列表
export function getCrawlerRules(params) {
  return request({
    url: '/crawler/rules',
    method: 'get',
    params
  })
}

// 获取爬虫规则详情
export function getCrawlerRule(id) {
  return request({
    url: `/crawler/rules/${id}`,
    method: 'get'
  })
}

// 创建爬虫规则
export function createCrawlerRule(data) {
  return request({
    url: '/crawler/rules',
    method: 'post',
    data
  })
}

// 更新爬虫规则
export function updateCrawlerRule(id, data) {
  return request({
    url: `/crawler/rules/${id}`,
    method: 'put',
    data
  })
}

// 删除爬虫规则
export function deleteCrawlerRule(id) {
  return request({
    url: `/crawler/rules/${id}`,
    method: 'delete'
  })
}

// 执行爬虫任务
export function executeCrawlerTask(data) {
  return request({
    url: '/crawler/execute',
    method: 'post',
    data
  })
} 