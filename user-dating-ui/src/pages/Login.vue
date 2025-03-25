<script setup lang="ts">
import { ref } from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {Field, Button} from 'vant';
import instance from "../utils/request.ts";

const router = useRouter();
const route = useRoute();

const isLoading = ref(false);

const form = ref({
  userCount: '',
  userPassword: ''
});

const handleLogin = async () => {
  try {
    isLoading.value = true;
    const result = await instance.post('/user/login', form.value) as ResponseResult<UserInformation>;
    if (result.code !== 200) {
      console.log('登录失败');
    }
    // 获取跳转地址
    const redirect = route.query.redirect?.toString() || '/';
    console.log('登录成功');
    await router.replace(redirect);
  } catch (error) {
    console.error('登录异常:', error);
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div v-if="isLoading" class="loading-container">
    <van-loading size="24px" vertical>加载中...</van-loading>
  </div>

  <div class="login-container">
    <h2>用户登录</h2>
    <Field
        v-model="form.userCount"
        label="用户名"
        placeholder="请输入用户名"
    />
    <Field
        v-model="form.userPassword"
        type="password"
        label="密码"
        placeholder="请输入密码"
    />
    <Button
        type="primary"
        block
        @click="handleLogin"
        style="margin-top: 20px"
    >
      登录
    </Button>
  </div>
</template>

<style scoped>
.loading-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7); /* 半透明遮罩 */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999; /* 确保在最上层 */
}

.login-container {
  padding: 20px;
  max-width: 400px;
  margin: 100px auto;
}
</style>