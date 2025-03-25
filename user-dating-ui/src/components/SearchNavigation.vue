<script setup lang="ts">
import {ref} from "vue";

const value = ref('');

// 点击查询触发
const onSearch = (val:string) => {
  console.log(val);
};

// 点击取消触发
const onCancel = () => {

};
</script>

<template>
  <form action="/">
    <van-search
        v-model="value"
        show-action
        placeholder="请输入搜索关键词"
        @search="onSearch"
        @cancel="onCancel"
    />
  </form>
</template>

<style scoped>

</style>