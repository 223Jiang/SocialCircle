<script setup lang="ts">
import { ref, onMounted, computed, onActivated } from 'vue';
import {Image as VanImage, showToast} from 'vant';
import instance from "../utils/request.ts";
import router from "../router.ts";

const profile = ref<UserInformation>({
  userId: '',
  userName: '',
  userCount: '',
  sex: undefined,
  userPhone: '',
  userEmail: '',
  userDescription: '',
  tags: '[]',
  imageUrl: ''
});

const avatarUrl = ref('https://minio.creativityhq.club/api/v1/buckets/user-avatars/objects/download?preview=true&prefix=defaultAvatar.png&version_id=null');

// 图片上传相关逻辑
const fileInput = ref<HTMLInputElement | null>(null);

const handleAvatarClick = () => {
  if (fileInput.value) {
    fileInput.value.click(); // 触发文件选择器
  }
};

const handleFileChange = async (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (!target.files || target.files.length === 0) return;

  const file = target.files[0];
  try {
    // 创建 FormData 对象，用于上传文件
    const formData = new FormData();
    formData.append('file', file);

    // 调用后端接口上传头像
    const response = await instance.post('/user/uploadAvatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    // 更新用户头像信息
    profile.value.imageUrl = response.data;

    // 提示上传成功
    showToast('头像上传成功');
  } catch (error) {
    console.error('头像上传失败:', error);
    showToast('头像上传失败');
  } finally {
    // 清空文件输入框
    if (target) {
      target.value = '';
    }
  }
};

const logout = () => {
  try {
    instance.post('/user/logout');

    // 3. 跳转登录页
    console.log('退出成功');
    const currentPath = router.currentRoute.value.fullPath;
    router.push({
      path: '/login',
      query: { redirect: currentPath }
    });
  } catch (error) {
    console.error('退出失败:', error);
  }
};

// 性别转换计算属性
const formattedSex = computed(() => {
  return profile.value.sex === 0 ? '男' : profile.value.sex === 1 ? '女' : '未知';
});

// 标签处理计算属性
const formattedTags = computed(() => {
  try {
    const tags = JSON.parse(profile.value.tags || '[]');
    return Array.isArray(tags) && tags.length > 0
        ? tags.join(' / ')
        : '暂无标签';
  } catch (error) {
    return '标签解析失败';
  }
});

onMounted(async () => {
  try {
    const response = await instance.get('/user/currentUser');

    profile.value = response.data as UserInformation;
  } catch (error) {
    console.log(error)
  }
});

// 在 Profile.vue 中添加字段映射关系
// @ts-ignore
const fieldLabels: Record<keyof UserInformation, string> = {
  userId: '用户id',
  userName: '昵称',
  userCount: '账号',
  sex: '性别',
  userPhone: '手机号',
  userEmail: '邮箱',
  userDescription: '个性签名',
  tags: '标签',
  imageUrl: '头像'
};

const goToEdit = (field: string) => {
  router.push({
    path: '/edit',
    query: {
      userId: profile.value["userId"],
      type: field,
      // @ts-ignore
      label: fieldLabels[field], // 传递中文标签
      // @ts-ignore
      value: profile.value[field]?.toString() || ''
    }
  });
};

onActivated(async () => {
  try {
    const response = await instance.get('/user/currentUser');
    profile.value = response.data as UserInformation;
  } catch (error) {
    console.error('Failed to refresh user information:', error);
  }
});
</script>

<template>
  <div class="profile-container">
    <van-cell-group>
      <!-- 头像单元格 -->
      <van-cell class="avatar-cell" @click="handleAvatarClick">
        <template #default>
          <div class="avatar-center">
            <van-image
                round
                width="60"
                height="60"
                :src="profile.imageUrl || avatarUrl"
            />
          </div>
        </template>
      </van-cell>

      <!-- 文件选择器 -->
      <input
          type="file"
          accept="image/*"
          style="display: none;"
          ref="fileInput"
          @change="handleFileChange"
      />

      <!-- 基础信息 -->
      <van-cell title="昵称" :value="profile.userName" is-link @click="goToEdit('userName')" />
      <!-- 账号信息 -->
      <van-cell title="账号" :value="profile.userCount" is-link class="non-clickable">
        <template #right-icon>
          <van-icon name="arrow" color="#ffffff" />
        </template>
      </van-cell>

      <!-- 性别显示 -->
      <van-cell title="性别" :value="formattedSex" is-link @click="goToEdit('sex')" />

      <!-- 联系方式 -->
      <van-cell title="手机号" :value="profile.userPhone" is-link @click="goToEdit('userPhone')" />
      <van-cell title="邮箱" :value="profile.userEmail" is-link @click="goToEdit('userEmail')" />

      <!-- 标签展示 -->
      <van-cell title="标签" :value="formattedTags" is-link @click="goToEdit('tags')" />

      <!-- 个性签名 -->
      <van-cell
          title="个性签名"
          :value="profile.userDescription || '这个人很懒，什么都没有写...'"
          is-link
          @click="goToEdit('userDescription')"
      />
    </van-cell-group>

    <!-- 操作按钮 -->
    <div class="button-group">
      <van-button type="danger" block @click="logout">退出登录</van-button>
    </div>
  </div>
</template>

<style scoped>
.non-clickable {
  pointer-events: none;
}

.profile-container {
  padding: 16px;
}

.button-group {
  margin-top: 24px;
}

.avatar-cell {
  display: flex;
  align-items: center;
  padding: 20px 0; /* 增加上下间距 */
}

.avatar-center {
  width: 100%;
  display: flex;
  justify-content: center;
}

/* 响应式样式 */
@media screen and (max-width: 375px) {
  .avatar-cell {
    padding: 15px 0;
  }
}
</style>