<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant';
import instance from "../utils/request.ts";

const router = useRouter()

// 初始表单数据
const initialForm = {
  name: '', // 圈子名称
  description: '', // 描述
  maxNum: null, // 最大人数
  expireTime: '', // 过期时间
  status: 0, // 状态值
  statusText: '公开', // 状态文本
  password: '' // 密码（仅在加密状态下显示）
};

// 表单数据
const teamForm = ref({
  name: '',
  description: '',
  maxNum: null,
  expireTime: '',
  status: 0,
  statusText: '公开',
  password: ''
})

const getTeamList = () => {
  return instance.get('/team/teamsOfUsers').then(res => res.data)
}

const createTeam = (data) => {
  return instance.post('/team/createTeam', data)
}

// 验证函数
const validateMaxNum = (val) => val >= 1 && val <= 15
const validatePassword = (val) => val.length >= 6 && val.length <= 15

// 时间选择器
const showPicker = ref(false)
const minDate = new Date()
const currentDate = ref()
const onTimeConfirm = (value) => {
  teamForm.value.expireTime = formatDate(value)
  showPicker.value = false
}

// 状态选择器
const showStatusPicker = ref(false)
const statusColumns = [
  { text: '公开', value: 0 },
  { text: '私有', value: 1 },
  { text: '加密', value: 2 }
]
const onStatusConfirm = (value) => {
  teamForm.value.status = value.selectedOptions[0].value
  teamForm.value.statusText = value.selectedOptions[0].text
  showStatusPicker.value = false
}

// 提交处理
const onSubmit = async () => {
  try {
    await createTeam(teamForm.value)
    showToast('创建成功');
    teamForm.value = { ...initialForm }
    fetchTeamList()
  } catch (error) {
    showToast('创建失败');
  }
}

// 列表数据
const teamList = ref([])
const fetchTeamList = async () => {
  const res = await getTeamList()
  teamList.value = res.map(item => ({
    ...item,
    statusText: statusColumns.find(s => s.value === item.status)?.text
  }))
}

// 跳转详情
const goToDetail = (id) => {
  router.push(`/team/${id}`)
}

// 初始化
onMounted(() => {
  fetchTeamList()
})

// 工具函数
const formatDate = (date) => {
  return `${date.selectedValues[0]}-${date.selectedValues[1]}-${date.selectedValues[2]} 00:00:00`
}

const padZero = (num) => {
  return num.toString().padStart(2, '0')
}
</script>

<template>
  <div class="team-container">
    <!-- 创建表单 -->
    <van-form @submit="onSubmit" class="create-form">
      <van-cell-group inset>
        <!-- 队伍名称 -->
        <van-field
            v-model="teamForm.name"
            name="name"
            label="圈子名称"
            placeholder="请输入圈子名称"
            :rules="[{ required: true, message: '请输入圈子名称' }]"
        />

        <!-- 队伍描述 -->
        <van-field
            v-model="teamForm.description"
            rows="2"
            autosize
            label="描述"
            type="textarea"
            placeholder="请输入圈子描述"
        />

        <!-- 最大人数 -->
        <van-field
            v-model="teamForm.maxNum"
            type="digit"
            name="maxNum"
            label="最大人数"
            placeholder="1-15人"
            :rules="[
            { required: true, message: '请输入人数' },
            { validator: validateMaxNum, message: '人数需在1-15之间' }
          ]"
        />

        <!-- 过期时间 -->
        <van-field
            v-model="teamForm.expireTime"
            is-link
            readonly
            name="expireTime"
            label="过期时间"
            placeholder="请选择时间"
            @click="showPicker = true"
            :rules="[{ required: true, message: '请选择时间' }]"
        />
        <van-popup v-model:show="showPicker" position="bottom">
          <van-date-picker
              v-model="currentDate"
              type="datetime"
              title="选择时间"
              :min-date="minDate"
              @confirm="onTimeConfirm"
              @cancel="showPicker = false"
          />
        </van-popup>

        <!-- 状态选择 -->
        <van-field
            v-model="teamForm.statusText"
            is-link
            readonly
            name="status"
            label="圈子类型"
            placeholder="请选择类型"
            @click="showStatusPicker = true"
            :rules="[{ required: true, message: '请选择类型' }]"
        />
        <van-popup v-model:show="showStatusPicker" position="bottom">
          <van-picker
              :columns="statusColumns"
              @confirm="onStatusConfirm"
              @cancel="showStatusPicker = false"
          />
        </van-popup>

        <!-- 密码输入 -->
        <van-field
            v-if="teamForm.status === 2"
            v-model="teamForm.password"
            type="password"
            name="password"
            label="密码"
            placeholder="6-15位密码"
            :rules="[
            { required: true, message: '请输入密码' },
            { validator: validatePassword, message: '密码需6-15位' }
          ]"
        />
      </van-cell-group>

      <div class="submit-btn">
        <van-button round block type="primary" native-type="submit">
          创建圈子
        </van-button>
      </div>
    </van-form>

    <!-- 圈子列表 -->
    <div class="team-list">
      <van-card
          v-for="team in teamList"
          :key="team.id"
          :desc="team.description"
          :title="team.name"
          class="team-card"
      >
        <template #tags>
          <van-tag plain type="primary" class="team-tag">
            {{ team.statusText }}
          </van-tag>
          <van-tag plain type="success" class="team-tag">
            {{ team.num }}/{{ team.maxNum }}人
          </van-tag>
        </template>
        <template #footer>
          <van-button size="small" @click="goToDetail(team.id)">
            查看详情
          </van-button>
        </template>
      </van-card>
    </div>
  </div>
</template>

<style scoped>
.team-container {
  padding: 16px;
}

.create-form {
  margin-bottom: 24px;
}

.submit-btn {
  margin: 24px 16px;
}

.team-card {
  margin-bottom: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.team-tag {
  margin-right: 8px;
  margin-top: 8px;
}
.team-list {
  padding: 0 0 40px 0;
}
</style>