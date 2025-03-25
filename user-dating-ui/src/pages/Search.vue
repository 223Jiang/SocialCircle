<script setup lang="ts">
import { ref } from 'vue';
// @ts-ignore
import { useRouter } from 'vue-router'
import instance from "../utils/request.ts";

const isLoading = ref(false);

const router = useRouter()

// 搜索框的值
const value = ref('');

// 标签数组
const tags = ref<string[]>([]);
const hotTags = ref<string[]>(['java', 'python', '大数据', '单身', '大一', '数字传媒']);

// 搜索事件
const onSearch = (val: string) => {
  if (val.trim()) {
    tags.value.push(val); // 将输入的值添加到标签数组中
    value.value = ''; // 清空输入框
  }
};

// 取消事件
const onCancel = () => {
    router.push('/')
};

// 删除标签
const close = (index: number) => {
  tags.value.splice(index, 1); // 根据索引删除对应的标签
};

// 添加热门标签到已选标签
const addHotTag = (tag: string) => {
  if (!tags.value.includes(tag)) {
    tags.value.push(tag); // 如果标签尚未被选中，则添加到已选标签中
  }
};

// 发送搜索请求
const handleSearch = async () => {
  try {
    if (tags.value.length === 0) {
      console.warn('没有已选标签，无法发送请求');
      return;
    }

    isLoading.value = true;

    const response = await instance.post('/user/searchUserByTags', {
      tags: tags.value
    })
    const data = response.data;

    // 跳转到结果页面或处理搜索结果
    await router.push({
      path: '/results',
      state: {
        data: JSON.stringify(data),
        tags: JSON.stringify(tags.value)
      }, // 将数据序列化为字符串
    });
  } catch (error) {
    console.error('搜索失败:', error);
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>

  <van-search
      v-model="value"
      show-action
      placeholder="请输入标签关键词"
      @search="onSearch"
      @cancel="onCancel"
  >
    <!-- 动态设置右侧按钮 -->
    <template #action>
      <div v-if="tags.length > 0" @click="handleSearch" class="handleSearch">
        搜索
      </div>
      <div v-else @click="onCancel" class="onCancel">
        取消
      </div>
    </template>
  </van-search>

  <div class="everyoneIsSearching">
    大家都在搜
  </div>
  <!-- 标签列表 -->
  <van-row>
    <van-col v-for="(tag, index) in hotTags" :key="index">
      <van-tag
          round
          type="primary"
          plain
          size="large"
          style="margin: 8px 10px;"
          @click="addHotTag(tag)"
      >
        {{ tag }}
      </van-tag>
    </van-col>
  </van-row>

  <div class="selectedTags">
    已选标签
  </div>

  <!-- 标签列表 -->
  <van-row>
    <van-col v-for="(tag, index) in tags" :key="index">
      <van-tag
          round
          type="primary"
          plain
          size="large"
          closeable
          style="margin: 8px 10px;"
          @close="() => close(index)"
      >
      {{ tag }}
      </van-tag>
    </van-col>
  </van-row>

  <van-loading v-if="isLoading" size="24px" vertical>加载中...</van-loading>

</template>

<style scoped>
.handleSearch {
  color: #5E8CFA;
  cursor: pointer;
}
.onCancel {
  color: grey;
  cursor: pointer;
}
.everyoneIsSearching{
  margin: 3% 4% 5%;
  color: grey;

}
.selectedTags {
  margin: 10% 4% 5%;
  color: grey;
}
</style>