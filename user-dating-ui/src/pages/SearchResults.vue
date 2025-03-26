<script setup lang="ts">
import { computed, ref } from "vue";
import router from "../router.ts";
import instance from "../utils/request.ts";


// 初始数据解析
const initialData = computed(() => {
  try {
    return {
      searchData: JSON.parse(router.options.history.state?.data as string) as PaginationData<UserInformation>,
      tags: JSON.parse(router.options.history.state?.tags as string) as string[]
    };
  } catch {
    return {
      searchData: { current: 1, size: 10, pages: 0, records: [], total: 0 },
      tags: []
    };
  }
});

// 分页状态
const currentPage = ref(initialData.value.searchData.current);
const pageSize = ref(initialData.value.searchData.size);
const totalPages = ref(initialData.value.searchData.pages);
const totalItems = ref(initialData.value.searchData.total);
const records = ref<UserInformation[]>(initialData.value.searchData.records);
const isLoading = ref(false);
const avatarUrl = ref('https://minio.creativityhq.club/api/v1/buckets/user-avatars/objects/download?preview=true&prefix=defaultAvatar.png&version_id=null');

// 数据获取方法
const fetchData = async () => {
  try {
    isLoading.value = true;
    const response = await instance.post('/user/searchUserByTags', {
      current: currentPage.value,
      pageSize: pageSize.value,
      tags: initialData.value.tags
    });

    records.value = response.data.records;
    totalPages.value = response.data.pages;
    totalItems.value = response.data.total;
  } catch (error) {
    console.error('Error fetching data:', error);
    // 可添加错误提示组件
  } finally {
    isLoading.value = false;
  }
};

// 分页变化处理
const handlePageChange = () => {
  fetchData();
};

const goHome = () => {
  router.push('/');
}
</script>

<template>
  <div class="content">
    <!-- 加载状态 -->
    <van-loading v-if="isLoading" size="24px" vertical>加载中...</van-loading>

    <!-- 用户卡片列表 -->
    <div v-else-if="records.length > 0">
      <div v-for="record in records" :key="record.userId" class="card-item">
        <van-card
            :desc="record.userDescription"
            :title="record.userName"
            :thumb="record.imageUrl || avatarUrl"
        >
          <!-- 自定义标题 -->
          <template #title>
            <div class="title-container">
              <span class="card-title">{{ record.userName }}</span>
            </div>
          </template>

          <!-- 自定义描述 -->
          <template #desc>
            <div class="desc-container">
              {{ record.userDescription }}
            </div>
          </template>

          <!-- 标签区域 -->
          <template #footer>
            <van-tag
                v-for="(tag, index) in JSON.parse(record.tags || '[]')"
                :key="index"
                plain
                type="primary"
                style="margin: 0 3px 0 0;"
            >
              {{ tag }}
            </van-tag>
          </template>
        </van-card>
      </div>
    </div>
    <!-- 无数据状态 -->
    <div v-else class="empty-container">
      <van-empty
          image="search"
          description="暂无符合条件的数据"
      >
        <van-button round type="primary" @click="goHome">返回首页</van-button>
      </van-empty>
    </div>
  </div>
  <!-- 分页器 -->
  <div v-if="totalItems > pageSize" class="pagination-wrapper">
    <van-pagination
        v-model="currentPage"
        :total-items="totalItems"
        :items-per-page="pageSize"
        :show-page-size="3"
        force-ellipses
        @change="handlePageChange"
    />
  </div>
</template>

<style scoped>
:deep(.van-loading) {
  margin: 20px 0;
  display: flex;
  justify-content: center;
}

.content {
  padding: 0 16px;
  margin-bottom: 25%; /* 与分页器高度匹配 */
}

.pagination-wrapper {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  margin: 45px 0;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 999;
}

.card-item {
  margin-bottom: 10px;
}
.card-title {
  font-size: 16px;
  font-weight: 500;
}

.desc-container {
  margin-top: 8px;
  color: #666;
  line-height: 1.4;
}

.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  flex-direction: column;
}

:deep(.van-empty__image) {
  width: 90px;
  height: 90px;
}

:deep(.van-empty__description) {
  margin-top: 16px;
  color: #999;
  font-size: 14px;
}

:deep(.van-button) {
  margin-top: 24px;
  padding: 0 40px;
}
</style>