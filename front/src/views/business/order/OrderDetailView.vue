<template>
  <div class="biz-page">
    <PageHeader title="订单详情" back />

    <BaseCard title="订单信息">
      <div class="biz-info-grid cols-2">
        <div class="biz-info-item"><span class="biz-info-label">订单号</span><span class="biz-info-value">{{ d.orderNo }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">用户</span><span class="biz-info-value">{{ d.user }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">类型</span><span class="biz-info-value">{{ d.type }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">金额</span><span class="biz-info-value">{{ d.amount }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">支付方式</span><span class="biz-info-value">{{ d.payMethod }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">状态</span><span class="biz-info-value"><StatusTag :type="d.status === '已完成' ? 'green' : 'orange'">{{ d.status }}</StatusTag></span></div>
        <div class="biz-info-item"><span class="biz-info-label">下单时间</span><span class="biz-info-value">{{ d.time }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">支付时间</span><span class="biz-info-value">{{ d.payTime }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">渠道</span><span class="biz-info-value">{{ d.channel }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">交易号</span><span class="biz-info-value">{{ d.tradeNo }}</span></div>
      </div>
    </BaseCard>

    <BaseCard title="商品信息">
      <div class="biz-info-grid cols-2">
        <div class="biz-info-item"><span class="biz-info-label">短剧</span><span class="biz-info-value">{{ d.drama }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">集数</span><span class="biz-info-value">{{ d.episode }}</span></div>
      </div>
    </BaseCard>

    <BaseCard title="设备信息">
      <div class="biz-info-grid cols-2">
        <div class="biz-info-item"><span class="biz-info-label">IP</span><span class="biz-info-value">{{ d.ip }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">设备</span><span class="biz-info-value">{{ d.device }}</span></div>
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
onMounted(async () => { d.value = await orderApi.detail() })
</script>
