<script setup lang="ts">
import { onMounted, ref, watch } from "vue";
import { useRouter } from "vue-router";
import instance from "../utils/request.ts";

const router = useRouter();

// 分页参数
const currentPage = ref(1);
const pageSize = ref(10);
const totalPages = ref(0);
const total = ref(0);
const records = ref<UserInformation[]>([]);
const isLoading = ref(false);
const avatarUrl = ref('https://minio.creativityhq.club/api/v1/buckets/user-avatars/objects/download?preview=true&prefix=defaultAvatar.png&version_id=null');

// 获取推荐数据
const fetchData = async () => {
  try {
    isLoading.value = true;
    const response = await instance.post('/user/getReferralData', {
      current: currentPage.value,
      pageSize: pageSize.value,
    });

    const userPage = response.data as PaginationData<UserInformation>;
    totalPages.value = userPage.pages;
    total.value = userPage.total;
    records.value = userPage.records;
  } catch (error) {
    // @ts-ignore
    if (error.response?.status === 401) {
      router.push({
        path: '/login',
        query: { redirect: router.currentRoute.value.fullPath }
      });
    }
    console.error('数据加载失败:', error);
  } finally {
    isLoading.value = false;
  }
};

// 分页变化处理
const handlePageChange = (page: number) => {
  if (page !== currentPage.value && page <= totalPages.value) {
    currentPage.value = page;
  }
};

// 监听页码变化
watch(currentPage, () => {
  fetchData();
});

// 初始化数据
onMounted(() => {
  fetchData();
});
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
          <template #title>
            <div class="title-container">
              <span class="card-title">{{ record.userName }}</span>
            </div>
          </template>

          <template #desc>
            <div class="desc-container">
              {{ record.userDescription }}
            </div>
          </template>

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
          description="暂无数据"
      >
        <van-button round type="primary" @click="router.push('/')">返回首页</van-button>
      </van-empty>
    </div>
  </div>

  <!-- 分页器 -->
  <div v-if="total > pageSize" class="pagination-wrapper">
    <van-pagination
        v-model="currentPage"
        :total-items="total"
        :items-per-page="pageSize"
        :show-page-size="5"
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