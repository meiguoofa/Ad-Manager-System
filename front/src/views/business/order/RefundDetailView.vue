<template>
  <div class="biz-page">
    <PageHeader title="退款详情" back />

    <BaseCard title="退款信息">
      <div class="biz-info-grid cols-2">
        <div class="biz-info-item"><span class="biz-info-label">退款单号</span><span class="biz-info-value">{{ d.refundNo }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">原订单号</span><span class="biz-info-value">{{ d.orderNo }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">用户</span><span class="biz-info-value">{{ d.user }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">金额</span><span class="biz-info-value">{{ d.amount }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">原因</span><span class="biz-info-value">{{ d.reason }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">状态</span><span class="biz-info-value"><StatusTag type="orange">{{ d.status }}</StatusTag></span></div>
        <div class="biz-info-item"><span class="biz-info-label">申请时间</span><span class="biz-info-value">{{ d.applyTime }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">处理人</span><span class="biz-info-value">{{ d.operator }}</span></div>
      </div>
      <div class="biz-divider" />
      <div class="biz-info-item"><span class="biz-info-label">说明</span><p style="margin-top:6px; color: var(--biz-text-regular); line-height:1.8;">{{ d.desc }}</p></div>
    </BaseCard>

    <BaseCard title="处理记录">
      <div v-for="(r, i) in d.processRecords" :key="i" class="biz-list-item">
        <div class="biz-list-item-icon" style="background:#e6f7ff;color:#1890ff">{{ Number(i) + 1 }}</div>
        <div class="biz-list-item-content">
          <div class="biz-list-item-title">{{ r.action }}</div>
          <div class="biz-list-item-desc">{{ r.time }}</div>
        </div>
        <div class="biz-list-item-meta">{{ r.operator }}</div>
      </div>
    </BaseCard>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import PageHeader from '../../../components/PageHeader.vue'
import BaseCard from '../../../components/BaseCard.vue'
import StatusTag from '../../../components/StatusTag.vue'
import { orderApi } from '../../../api/business'

const d = ref<any>({})
onMounted(async () => { d.value = await orderApi.refundDetail() })
</script>
