<script setup lang="ts">
// @ts-ignore
import {useRoute, useRouter} from 'vue-router'
import {ref, watchEffect} from "vue";

const router = useRouter()
const route = useRoute()

// 动态标题响应式变量
const navTitle = ref('默认标题')

// 监听路由变化更新标题
watchEffect(() => {
  const title = route.meta.title as string | undefined
  navTitle.value = title || '社交App'
  document.title = navTitle.value // 同步页面标题
})

// 点击搜索触发方法
const onClickRight = () => {
  if (route.path === '/' || route.path === '/results') {
    router.push('/search')
  } else if (route.path === '/friends' || route.path.startsWith('/team')) {
    router.push('/listOfCircles')
  }
}

const goToHome = () => {
  router.push('/')
}
</script>

<template>
  <van-nav-bar
      :title="navTitle"
      @click-right="onClickRight"
  >
    <template #left>
      <img
          src="../public/logo.png"
          alt="Logo"
          @click="goToHome"
          class="nav-logo"
      />
    </template>
    <template #right>
      <van-icon name="search" size="18" />
    </template>
  </van-nav-bar>

  <router-view />

  <van-tabbar route>
    <van-tabbar-item replace to="/" icon="home-o">主页</van-tabbar-item>
    <van-tabbar-item replace to="/friends" icon="friends-o">圈子</van-tabbar-item>
    <van-tabbar-item replace to="/individual" icon="contact-o">个人</van-tabbar-item>
  </van-tabbar>
</template>

<style scoped>
.nav-logo {
  width: 60px;
  height: 60px;
  vertical-align: middle;
}
</style>
