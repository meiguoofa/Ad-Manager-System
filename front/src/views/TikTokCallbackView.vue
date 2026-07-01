<template>
  <section class="panel page-panel tiktok-callback-page">
    <el-result
      :icon="isSuccess ? 'success' : 'error'"
      :title="isSuccess ? 'TikTok 授权成功' : 'TikTok 授权失败'"
      :sub-title="subTitle"
    >
      <template #extra>
        <el-button type="primary" @click="goHome">返回首页</el-button>
      </template>
    </el-result>

    <div v-if="isSuccess && advertiserIds" class="advertiser-block">
      <p class="advertiser-label">已授权广告账户 (advertiser_ids)</p>
      <el-tag v-for="id in advertiserIdList" :key="id" class="advertiser-tag">{{ id }}</el-tag>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const isSuccess = computed(() => route.query.status === 'success')
const message = computed(() => (route.query.message ? String(route.query.message) : ''))
const advertiserIds = computed(() => (route.query.advertiserIds ? String(route.query.advertiserIds) : ''))
const advertiserIdList = computed(() => advertiserIds.value.split(',').filter(Boolean))

const subTitle = computed(() => {
  if (isSuccess.value) {
    return '广告账户已成功授权,可以开始拉取投放数据'
  }
  return message.value || '授权未成功,请重试'
})

function goHome() {
  router.push('/')
}
</script>

<style scoped>
.tiktok-callback-page {
  display: grid;
  align-content: start;
  gap: 16px;
}

.advertiser-block {
  text-align: center;
}

.advertiser-label {
  margin-bottom: 8px;
  color: var(--text-secondary, #909399);
  font-size: 13px;
}

.advertiser-tag {
  margin: 4px;
}
</style>
