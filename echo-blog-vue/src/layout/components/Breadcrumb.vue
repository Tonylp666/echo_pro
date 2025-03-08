<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index" :to="item.path">
      {{ item.title }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

export default {
  name: 'Breadcrumb',
  setup() {
    const route = useRoute()
    const breadcrumbs = ref([])
    
    const getBreadcrumbs = () => {
      const matched = route.matched.filter(item => item.meta && item.meta.title)
      
      const first = matched[0]
      if (first && first.path !== '/dashboard') {
        breadcrumbs.value = [
          { path: '/dashboard', title: '首页' },
          ...matched.map(item => {
            return {
              path: item.path,
              title: item.meta.title
            }
          })
        ]
      } else {
        breadcrumbs.value = matched.map(item => {
          return {
            path: item.path,
            title: item.meta.title
          }
        })
      }
    }
    
    getBreadcrumbs()
    
    watch(() => route.path, () => {
      getBreadcrumbs()
    })
    
    return {
      breadcrumbs
    }
  }
}
</script>

<style lang="scss" scoped>
.el-breadcrumb {
  font-size: 14px;
  line-height: 50px;
  
  .no-redirect {
    color: #97a8be;
    cursor: text;
  }
  
  a {
    color: #606266;
    cursor: pointer;
    
    &:hover {
      color: #409EFF;
    }
  }
}
</style> 