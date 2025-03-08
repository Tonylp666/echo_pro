import request from '@/utils/request'

// 获取任务列表
export function getTasks(params) {
  return request({
    url: '/tasks',
    method: 'get',
    params
  })
}

// 获取任务详情
export function getTask(id) {
  return request({
    url: `/tasks/${id}`,
    method: 'get'
  })
}

// 创建任务
export function createTask(data) {
  return request({
    url: '/tasks',
    method: 'post',
    data
  })
}

// 更新任务
export function updateTask(id, data) {
  return request({
    url: `/tasks/${id}`,
    method: 'put',
    data
  })
}

// 删除任务
export function deleteTask(id) {
  return request({
    url: `/tasks/${id}`,
    method: 'delete'
  })
}

// 更新任务状态
export function updateTaskStatus(id, status) {
  return request({
    url: `/tasks/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 获取用户的任务
export function getTasksByUser(userId, params) {
  return request({
    url: `/tasks/user/${userId}`,
    method: 'get',
    params
  })
} 