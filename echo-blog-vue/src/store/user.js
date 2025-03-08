import { defineStore } from 'pinia'
import { login, logout, getUserInfo } from '@/api/auth'
import { setToken, getToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    userInfo: null,
    roles: []
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.username || '',
    name: (state) => state.userInfo?.name || '',
    avatar: (state) => state.userInfo?.avatar || ''
  },
  
  actions: {
    // 登录
    async login(loginData) {
      try {
        const { data } = await login(loginData)
        this.token = data.token
        setToken(data.token)
        return Promise.resolve()
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    // 获取用户信息
    async getUserInfo() {
      try {
        const { data } = await getUserInfo()
        this.userInfo = data
        this.roles = data.roles || []
        return Promise.resolve(data)
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    // 登出
    async logout() {
      try {
        await logout()
        this.resetState()
        return Promise.resolve()
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    // 重置状态
    resetState() {
      this.token = ''
      this.userInfo = null
      this.roles = []
      removeToken()
    }
  }
}) 