<script setup lang="ts">
import { ref, computed } from 'vue';
import {
  Search,
  Field,
  CellGroup,
  Picker,
  Popup,
  List,
  Card,
  Pagination,
  Dialog,
  showLoadingToast, showSuccessToast, showFailToast, closeToast
} from 'vant';
import instance from "../utils/request.ts";

// 搜索参数
const searchForm = ref({
  nameKeyword: '',
  status: null as number | null,
  members: null as number | null,
  joinable: false,
  current: 1,
  pageSize: 10
});

// 队伍状态映射
const statusOptions = [
  { text: '全部', value: null },
  { text: '公开', value: 0 },
  { text: '加密', value: 2 }
];
const statusText = ref('全部');

// 分页数据
const teamList = ref<Team[]>([]);
const total = ref(0);
const loading = ref(false);

// 弹出选择器控制
const showStatusPicker = ref(false);

// 计算总页数
const totalPages = computed(() =>
    Math.ceil(total.value / searchForm.value.pageSize)
);

// 获取队伍列表
const getTeamList = async () => {
  try {
    loading.value = true;
    const res = await instance.post('/team/searchTeams', searchForm.value);
    teamList.value = res.data.records;
    total.value = res.data.total;
  } finally {
    loading.value = false;
  }
};

// 分页切换（添加边界控制）
const onPageChange = (page: number) => {
  // 强制限制在有效页码范围
  const newPage = Math.max(1, Math.min(page, totalPages.value));

  if (newPage !== searchForm.value.current) {
    searchForm.value.current = newPage;
    getTeamList();
  }
};

// 格式化时间
const formatDate = (dateStr: string) => {
  return dateStr ? dateStr.replace('T', ' ').slice(0, 16) : '';
};

// 初始化数据
getTeamList();

// 控制描述展开与收起
const toggleDescription = ref<{ [key: number]: boolean }>({});
const toggleDesc = (id: number) => {
  toggleDescription.value[id] = !toggleDescription.value[id];
};

// 加入队伍方法
const joinTeam = async (team: Team) => {
  // 处理加密队伍密码验证
  if (team.status === 2) {
    Dialog.confirm({
      title: '输入密码',
      message: '请输入队伍密码',
      inputType: 'password',
      showCancelButton: true,
    }).then(async (password) => {
      await handleJoin(team.id, password);
    }).catch(() => {});
  } else {
    await handleJoin(team.id);
  }
};

// 实际加入逻辑
const handleJoin = async (teamId: number, password?: string) => {
  try {
    showLoadingToast({ message: '加入中...', forbidClick: true });
    await instance.post('/team/joinTeam', null, {
      params: {
        teamId: teamId,
        password: password
      }
    });
    showSuccessToast('加入成功');
    getTeamList(); // 刷新列表
  } catch (error: any) {
    showFailToast('加入失败');
  } finally {
    closeToast();
  }
};
</script>

<template>
  <div class="team-search-container">
    <!-- 搜索栏 -->
    <Search
        v-model="searchForm.nameKeyword"
        placeholder="请输入队伍名称"
        @search="getTeamList"
        class="search-bar"
    />

    <!-- 筛选条件 -->
    <CellGroup inset class="filter-panel">
      <!-- 状态筛选 -->
      <Field
          v-model="statusText"
          is-link
          readonly
          label="队伍状态"
          placeholder="请选择状态"
          @click="showStatusPicker = true"
          :value="statusOptions.find(opt => opt.value === searchForm.status)?.text || '全部'"
          class="field"
      />
      <Popup v-model:show="showStatusPicker" position="bottom">
        <Picker
            :columns="statusOptions"
            @confirm="(val) => {
              searchForm.status = val.selectedOptions[0].value;
              statusText = statusOptions.find(opt => opt.value === searchForm.status)?.text || '全部';
              showStatusPicker = false;
          }"
            @cancel="showStatusPicker = false"
        />
      </Popup>

<!--      &lt;!&ndash; 成员数量 &ndash;&gt;
      <Field label="成员数量" class="field">
        <template #input>
          <van-stepper
              v-model="searchForm.members"
              min="1"
              max="15"
              theme="round"
              button-size="22"
              integer
              :disable-input="false"
              @change="(value) => {
                if (value < 1) searchForm.members = 1;
                if (value >= 15) searchForm.members = 15;
              }"
          />
        </template>
      </Field>-->

      <!-- 可加入状态 -->
      <Field label="仅显示可加入" class="field">
        <template #input>
          <van-switch v-model="searchForm.joinable" />
        </template>
      </Field>
    </CellGroup>

    <!-- 操作按钮 -->
    <div class="button-group">
      <van-button type="primary" size="large" @click="getTeamList">搜索</van-button>
      <van-button size="large" @click="() => {
        searchForm = {
          current: 1, pageSize: 10
        };
        getTeamList();
      }">重置</van-button>
    </div>

    <!-- 队伍列表 -->
    <List
        v-model:loading="loading"
        :finished="true"
        class="team-list"
    >
      <Card
          v-for="team in teamList"
          :key="team.id"
          :title="team.name"
          class="team-card"
      >
        <template #desc>
          <div class="team-meta">
            <span>{{ formatDate(team.createTime) }}</span>
            <span>人数：{{ team.num }}/{{ team.maxNum }}</span>
          </div>
        </template>
        <div class="team-content">
          <p v-if="toggleDescription.value[team.id]">{{ team.description || '暂无描述' }}</p>
          <van-button
              size="small"
              type="text"
              class="toggle-desc-btn"
              @click="toggleDesc(team.id)"
          >
            {{ toggleDescription.value[team.id] ? '收起' : '展开' }} 描述
          </van-button>
          <div class="status-tag">
            {{ ['公开', '私有', '加密'][team.status] }}
          </div>
        </div>
        <template #footer>
          <div class="team-footer">
            <span>队长：{{ team.leaderName }}</span>
            <span>状态：{{ team.status === 0 ? '正常' : team.status === 2 ? '加密' : '未知' }}</span>
            <van-button
                size="small"
                type="primary"
                :disabled="team.num >= team.maxNum"
            @click="joinTeam(team)"
            >
            {{ team.num >= team.maxNum ? '已满员' : '加入队伍' }}  <!-- 根据队伍人数动态改变显示文字 -->
            </van-button>
          </div>
        </template>
      </Card>
    </List>

    <!-- 分页（添加边界控制） -->
    <Pagination
        v-model:current="searchForm.current"
        :total="total"
        :page-size="searchForm.pageSize"
        @change="onPageChange"
        :force-ellipses="true"
        :show-page-size="5"
        class="pagination"
    />
  </div>
</template>

<style scoped>
.team-search-container {
  padding: 16px;
  background: #f9f9f9;
}

.search-bar {
  margin-bottom: 16px;
}

.filter-panel {
  margin: 16px 0;
}

.field {
  margin-bottom: 12px;
}

.button-group {
  margin: 16px 0;
  display: flex;
  gap: 12px;
  justify-content: center;
}

.team-card {
  margin-bottom: 16px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.team-meta {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 12px;
}

.team-content {
  position: relative;
  min-height: 60px;
  padding: 12px;
}

.status-tag {
  position: absolute;
  right: 12px;
  top: 12px;
  background: #ff976a;
  color: white;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
}

.team-footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
  padding: 12px;
  border-top: 1px solid #f1f1f1;
}

.toggle-desc-btn {
  margin-top: 8px;
  padding: 0;
  color: #007aff;
  font-size: 12px;
  text-align: left;
}

.pagination {
  padding: 0 0 40px 0;
  margin-top: 20px;
}
</style>
