<script setup lang="ts">
import { ref, computed } from 'vue';
import {useRoute, useRouter} from 'vue-router';
import { showToast } from 'vant';
import instance from '../utils/request';

const router = useRouter();
const route = useRoute();

const userId = ref(route.query.userId as string);
const label = ref(route.query.label as string);
const field = ref(route.query.type as string);
const originalValue = ref(field.value !== 'tags' ? route.query.value as string : undefined);
const originalValueTag = ref(field.value === 'tags' ? JSON.parse(route.query.value as string) : []);
const newValue = ref(originalValue.value);
const tags = ref<string[]>(originalValueTag.value);

const fieldType = computed(() => {
  switch(field.value) {
    case 'userName':
    case 'userCount':
    case 'userPhone':
    case 'userEmail':
      return 'input';
    case 'sex':
      return 'radio';
    case 'tags':
      return 'tags';
    case 'userDescription':
      return 'textarea';
    default:
      return 'input';
  }
});

const options = computed(() => {
  if (field.value === 'sex') {
    return [
      { text: '男', value: '0' },
      { text: '女', value: '1' }
    ];
  }
  return [];
});

const newTagInput = '';

// 添加标签
const addTag = (value: string) => {
  if (!value.trim() || tags.value.includes(value)) return; // 空值或重复标签不添加
  if (tags.value.length >= 4) {
    showToast('最多只能添加 4 个标签');
    return;
  }
  tags.value.push(value.trim());
};

// 删除标签
const deleteTag = (index: number) => {
  tags.value.splice(index, 1);
};

// 输入框回车事件
const handleEnter = (event: KeyboardEvent) => {
  const input = event.target as HTMLInputElement;
  const value = input.value.trim();
  addTag(value);
  input.value = ''; // 清空输入框
};

const onSave = async () => {
  try {
    const data: Record<string, any> = {};
    if (newValue.value == undefined) {
      data[field.value] = JSON.stringify(tags.value); // 保存标签数组
    } else {
      data[field.value] = newValue.value;
    }
    data['userId'] = userId.value;

    await instance.post('/user/updateUserProfile', data);
    showToast('修改成功');
    router.back();
  } catch (error) {
    showToast('修改失败');
  }
};
</script>

<template>
  <div class="edit-container">
    <van-nav-bar
        :title="`${label}`"
        left-arrow
        @click-left="router.back()"
    />

    <div v-if="fieldType === 'input'">
      <van-field
          v-model="newValue"
          :label="label"
          :placeholder="`请输入${label}`"
      />
    </div>

    <div v-if="fieldType === 'radio'" class="radio-container">
      <van-radio-group v-model="newValue">
        <van-radio
            v-for="option in options"
            :key="option.value"
            :name="option.value"
            class="radio-item"
        >
          {{ option.text }}
        </van-radio>
      </van-radio-group>
    </div>

    <div v-if="fieldType === 'textarea'">
      <van-field
          v-model="newValue"
          rows="4"
          autosize
          type="textarea"
          maxlength="30"
          show-word-limit
          :placeholder="`请输入${label}`"
      />
    </div>

    <!-- 标签编辑 -->
    <div v-if="fieldType === 'tags'">
      <van-row>
        <van-col v-for="(tag, index) in tags" :key="index">
          <van-tag
              round
              type="primary"
              plain
              size="large"
              closeable
              style="margin: 8px 10px;"
              @close="() => deleteTag(index)"
          >
            {{ tag }}
          </van-tag>
        </van-col>
      </van-row>

      <!-- 输入框 -->
      <van-field
          v-model="newTagInput"
          placeholder="请输入标签，按回车确认"
          @keyup.enter="handleEnter($event)"
      />
    </div>

    <van-button type="primary" block @click="onSave" class="save-button">保存</van-button>
  </div>
</template>

<style scoped>
.edit-container {
  padding: 20px;
}

.radio-container {
  margin-bottom: 20px; /* 为整个 radio 容器添加底部间距 */
}

.radio-item {
  display: block; /* 让每个选项独占一行 */
  margin-bottom: 10px; /* 为每个选项之间添加间距 */
}

.save-button {
  margin-top: 20px; /* 为保存按钮添加顶部间距 */
}
</style>