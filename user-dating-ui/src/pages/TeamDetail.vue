<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import instance from '../utils/request'

const route = useRoute()
const router = useRouter()
const teamId = route.params.id

// 数据模型
const teamDetail = ref({})
const memberList = ref([])
const isLeader = ref(false)
const showTransferPopup = ref(false)
const selectedMember = ref(null)
const currentUser = ref({})

const getCurrentUser = async () => {
  try {
    const res = await instance.get(`/user/currentUser`)
    currentUser.value = res.data
  } catch (error) {
    showToast('获取当前用户信息失败')
  }
}

// 获取详情
const fetchTeamDetail = async () => {
  try {
    const res = await instance.get(`/team/${teamId}`)
    teamDetail.value = res.data
    memberList.value = res.data.users || []
    isLeader.value = res.data.leaderId === currentUser.value.userId
  } catch (error) {
    showToast('加载失败')
  }
}

// 成员操作
const handleRemoveMember = async (userId) => {
  if (isLeader.value) {
    try {
      // 修改请求路径和参数传递方式
      await instance.post('/team/removeMemberByLeader', null, {
        params: {
          teamId: teamId,
          memberId: userId  // 参数名与后端 @RequestParam 一致
        }
      });
      showToast('移除成功');
      await fetchTeamDetail();
    } catch (error) {
      showToast('操作失败');
    }
  }
}

// 转让队长
const transferLeadership = async () => {
  if (!selectedMember.value) return
  try {
    await instance.post(`/team/replaceMember`, null, {
      params: {
        teamId: teamId,
        memberId: selectedMember.value.userId  // 参数名与后端 @RequestParam 一致
      }
    })
    showToast('转让成功')
    await fetchTeamDetail()
    showTransferPopup.value = false
  } catch (error) {
    showToast(error.msg)
  }
}

// 退出队伍
const exitTeam = async () => {
  if (isLeader.value) {
    showToast('请先转让队长身份')
    return
  }
  try {
    await instance.post(`/team/exitTeam/${teamId}`)
    showToast('退出成功')
    router.back()
  } catch (error) {
    showToast('退出失败')
  }
}

// 解散队伍
const disbandTeam = async () => {
  try {
    await instance.post(`/removeMemberByLeader`, null, {
      params: {
        teamId: teamId,
        memberId: currentUser.value.userId
      }
    })
    showToast('队伍已解散')
    router.back()
  } catch (error) {
    showToast('操作失败')
  }
}

// 生命周期
onMounted(() => {
  getCurrentUser();
  fetchTeamDetail();
})
</script>

<template>
  <div class="team-detail-container">
    <!-- 队伍信息 -->
    <van-card
        :title="teamDetail.name"
        :desc="teamDetail.description"
        class="team-info-card"
    >
      <template #tags>
        <van-tag type="primary" class="info-tag">
          {{ ['公开', '私有', '加密'][teamDetail.status] }}
        </van-tag>
        <van-tag type="success" class="info-tag">
          {{ teamDetail.num }}/{{ teamDetail.maxNum }}人
        </van-tag>
        <van-tag type="warning" v-if="isLeader" class="info-tag">
          队长身份
        </van-tag>
      </template>
      <template #footer>
        <div class="info-footer">
          <span>到期时间：{{ teamDetail.expireTime }}</span>
        </div>
      </template>
    </van-card>

    <!-- 成员列表 -->
    <van-cell-group inset class="member-list">
      <van-cell
          v-for="member in memberList"
          :key="member.userId"
          :title="member.userName"
          :label="member.userDescription || '暂无简介'"
          :icon="member.imageUrl || 'https://minio.creativityhq.club/api/v1/buckets/user-avatars/objects/download?preview=true&prefix=defaultAvatar.png&version_id=null'"
      >
        <template #right-icon>
          <van-button
              v-if="isLeader && member.userId !== teamDetail.leaderId"
              size="small"
              type="danger"
              @click="handleRemoveMember(member.userId)"
          >
            移除
          </van-button>
        </template>
      </van-cell>
    </van-cell-group>

    <!-- 操作区域 -->
    <div class="action-buttons">
      <!-- 队长专属操作 -->
      <van-button
          v-if="isLeader"
          block
          type="primary"
          @click="showTransferPopup = true"
      >
        转让队长
      </van-button>

      <van-button
          v-if="isLeader"
          block
          type="danger"
          @click="disbandTeam"
      >
        解散队伍
      </van-button>

      <!-- 通用操作 -->
      <van-button
          block
          type="default"
          @click="exitTeam"
      >
        退出队伍
      </van-button>
    </div>

    <!-- 转让弹窗 -->
    <van-popup
        v-model:show="showTransferPopup"
        position="bottom"
        :style="{ height: '30%' }"
    >
      <div class="transfer-popup">
        <h3>选择新队长</h3>
        <van-radio-group v-model="selectedMember">
          <van-cell
              v-for="member in memberList"
              :key="member.userId"
              :title="member.userName"
              clickable
              @click="selectedMember = member"
          >
            <template #right-icon>
              <van-radio :name="member"/>
            </template>
          </van-cell>
        </van-radio-group>
        <van-button
            block
            type="primary"
            @click="transferLeadership"
            :disabled="!selectedMember"
        >
          确认转让
        </van-button>
      </div>
    </van-popup>
  </div>
</template>

<style scoped>
.team-detail-container {
  padding: 16px;
}

.team-info-card {
  margin-bottom: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.info-tag {
  margin-right: 8px;
  margin-top: 8px;
}

.member-list {
  margin-bottom: 24px;
  border-radius: 12px;
  overflow: hidden;
}

.action-buttons {
  margin-top: 24px;
  gap: 16px;
  display: flex;
  flex-direction: column;
}

.transfer-popup {
  padding: 16px;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
</style>