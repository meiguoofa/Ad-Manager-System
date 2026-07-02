<template>
  <div class="biz-page">
    <PageHeader title="短剧统计" desc="短剧投放效果数据分析" />
    <BaseTabs v-model="tab" :tabs="[{ key: 'overview', label: '概览' }, { key: 'script', label: '剧本维度' }, { key: 'cost', label: '消耗维度' }, { key: 'total', label: '总消耗' }]" />

    <div class="biz-stats-grid">
      <StatCard v-for="s in stats" :key="s.label" :label="s.label" :value="s.value" :diff="s.diff" :icon="s.icon" :icon-color="s.iconColor" />
    </div>

    <div class="biz-chart-grid-2">
      <ChartPanel title="播放量趋势" :option="playOption" :height="260" />
      <ChartPanel title="充值金额趋势" :option="rechargeOption" :height="260" />
    </div>

    <div class="biz-chart-grid-2">
      <ChartPanel title="渠道占比" :option="channelOption" :height="260" />
      <BaseCard title="短剧排行">
        <BaseTable :data="rank" :show-pagination="false" :total="0">
          <el-table-column prop="rank" label="排名" width="60" />
          <el-table-column prop="drama" label="短剧" />
          <el-table-column prop="playCount" label="播放量" />
          <el-table-column prop="recharge" label="充值" />
          <el-table-column prop="roi" label="ROI" />
        </BaseTable>
      </BaseCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import PageHeader from '../../../components/PageHeader.vue'
import BaseTabs from '../../../components/BaseTabs.vue'
import StatCard from '../../../components/StatCard.vue'
import ChartPanel from '../../../components/ChartPanel.vue'
import BaseCard from '../../../components/BaseCard.vue'
import BaseTable from '../../../components/BaseTable.vue'
import { lineOption, pieOption } from '../../../composables/useECharts'
import { dramaApi } from '../../../api/business'

const tab = ref('overview')
const stats = ref<any[]>([])
const rank = ref<any[]>([])

const playOption = lineOption({
  x: ['06-25', '06-26', '06-27', '06-28', '06-29', '06-30', '07-01'],
  series: [{ name: '播放量', data: [12500, 15200, 18900, 16800, 21300, 24500, 22800], color: '#1890ff' }],
  area: true,
})
const rechargeOption = lineOption({
  x: ['06-25', '06-26', '06-27', '06-28', '06-29', '06-30', '07-01'],
  series: [{ name: '充值', data: [98000, 125000, 156000, 132000, 178000, 205000, 192000], color: '#52c41a' }],
  area: true,
})
const channelOption = pieOption({
  data: [
    { name: 'TikTok', value: 55 },
    { name: 'Facebook', value: 25 },
    { name: 'Google', value: 12 },
    { name: '其他', value: 8 },
  ],
})

onMounted(async () => {
  stats.value = await dramaApi.stats()
  rank.value = await dramaApi.rank()
})
</script>
