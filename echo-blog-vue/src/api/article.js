import request from '@/utils/request'

// 获取文章列表
export function getArticles(params) {
  return request({
    url: '/articles',
    method: 'get',
    params
  })
}

// 获取文章详情
export function getArticle(id) {
  return request({
    url: `/articles/${id}`,
    method: 'get'
  })
}

// 创建文章
export function createArticle(data) {
  return request({
    url: '/articles',
    method: 'post',
    data
  })
}

// 更新文章
export function updateArticle(id, data) {
  return request({
    url: `/articles/${id}`,
    method: 'put',
    data
  })
}

// 删除文章
export function deleteArticle(id) {
  return request({
    url: `/articles/${id}`,
    method: 'delete'
  })
}

// 更新文章状态
export function updateArticleStatus(id, status) {
  return request({
    url: `/articles/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 增加文章浏览次数
export function incrementViewCount(id) {
  return request({
    url: `/articles/${id}/view`,
    method: 'put'
  })
}

// 增加文章点赞次数
export function incrementLikeCount(id) {
  return request({
    url: `/articles/${id}/like`,
    method: 'put'
  })
}

// 搜索文章
export function searchArticles(keyword, params) {
  return request({
    url: '/articles/search',
    method: 'get',
    params: { keyword, ...params }
  })
}

// 根据分类获取文章
export function getArticlesByCategory(categoryId, params) {
  return request({
    url: `/articles/category/${categoryId}`,
    method: 'get',
    params
  })
}

// 获取用户的文章
export function getArticlesByUser(userId, params) {
  return request({
    url: `/articles/user/${userId}`,
    method: 'get',
    params
  })
} 