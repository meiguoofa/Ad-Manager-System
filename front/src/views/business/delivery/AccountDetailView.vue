<template>
  <div class="biz-page">
    <PageHeader title="账户详情" back />
    <BaseCard title="基本信息">
      <div class="biz-info-grid">
        <div class="biz-info-item"><span class="biz-info-label">账户</span><span class="biz-info-value">A0001</span></div>
        <div class="biz-info-item"><span class="biz-info-label">名称</span><span class="biz-info-value">广告账户-1</span></div>
        <div class="biz-info-item"><span class="biz-info-label">渠道</span><span class="biz-info-value">TikTok</span></div>
        <div class="biz-info-item"><span class="biz-info-label">余额</span><span class="biz-info-value">¥85,200.00</span></div>
        <div class="biz-info-item"><span class="biz-info-label">今日消耗</span><span class="biz-info-value">¥5,230.00</span></div>
        <div class="biz-info-item"><span class="biz-info-label">今日充值</span><span class="biz-info-value">¥0.00</span></div>
        <div class="biz-info-item"><span class="biz-info-label">状态</span><span class="biz-info-value"><StatusTag type="green">正常</StatusTag></span></div>
        <div class="biz-info-item"><span class="biz-info-label">负责人</span><span class="biz-info-value">张三</span></div>
      </div>
    </BaseCard>

    <ChartPanel title="近7天消耗趋势" :option="trendOption" :height="280" />

    <BaseCard title="充值记录">
      <BaseTable :data="recharges" :show-pagination="false" :total="0">
        <el-table-column prop="time" label="时间" />
        <el-table-column prop="amount" label="金额" />
        <el-table-column prop="channel" label="渠道" />
        <el-table-column prop="operator" label="操作人" />
        <el-table-column prop="status" label="状态" />
      </BaseTable>
    </BaseCard>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import PageHeader from '../../../components/PageHeader.vue'
import BaseCard from '../../../components/BaseCard.vue'
import ChartPanel from '../../../components/ChartPanel.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'
import { lineOption } from '../../../composables/useECharts'

const trendOption = lineOption({
  x: ['06-25', '06-26', '06-27', '06-28', '06-29', '06-30', '07-01'],
  series: [{ name: '消耗', data: [4200, 5300, 6100, 4800, 7200, 8500, 5230], color: '#1890ff' }],
  area: true,
})

const recharges = ref([
  { time: '2026-06-30 10:00:00', amount: '¥20,000', channel: '支付宝', operator: '李四', status: '成功' },
  { time: '2026-06-28 14:30:00', amount: '¥50,000', channel: '银行转账', operator: '张三', status: '成功' },
  { time: '2026-06-25 09:15:00', amount: '¥30,000', channel: '微信', operator: '王五', status: '成功' },
])
</script>
