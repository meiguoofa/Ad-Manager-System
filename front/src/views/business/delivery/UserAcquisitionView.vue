<template>
  <div class="biz-page">
    <PageHeader title="拉新数据" desc="用户拉新效果分析" />

    <div class="biz-stats-grid">
      <StatCard v-for="s in stats" :key="s.label" :label="s.label" :value="s.value" :diff="s.diff" :icon="s.icon" :icon-color="s.iconColor" />
    </div>

    <FilterBar>
      <el-form-item label="日期"><el-date-picker v-model="date" type="daterange" range-separator="至" style="width:240px" /></el-form-item>
      <el-form-item label="渠道"><el-select v-model="channel" placeholder="全部" style="width:140px"><el-option v-for="c in channels" :key="c" :label="c" :value="c" /></el-select></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <div class="biz-chart-grid-2">
      <ChartPanel title="新增用户趋势" :option="trendOption" :height="260" />
      <ChartPanel title="渠道拉新分布" :option="channelPieOption" :height="260" />
    </div>

    <BaseTable :data="byChannelTable" :total="4" :page="1" :page-size="10" :show-pagination="false">
      <el-table-column prop="name" label="渠道" />
      <el-table-column prop="value" label="新增用户" />
      <el-table-column label="占比"><template #default="{ row }">{{ (row.value / total * 100).toFixed(1) }}%</template></el-table-column>
      <el-table-column prop="payRate" label="付费率" />
      <el-table-column prop="cost" label="拉新成本" />
    </BaseTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import PageHeader from '../../../components/PageHeader.vue'
import StatCard from '../../../components/StatCard.vue'
import FilterBar from '../../../components/FilterBar.vue'
import ChartPanel from '../../../components/ChartPanel.vue'
import BaseTable from '../../../components/BaseTable.vue'
import { lineOption, pieOption } from '../../../composables/useECharts'
import { deliveryApi } from '../../../api/business'

const channels = ['TikTok', 'Facebook', 'Google', 'Twitter']
const date = ref([])
const channel = ref('')
const stats = ref<any[]>([])
const byChannelTable = ref<any[]>([])
const total = computed(() => byChannelTable.value.reduce((s, r) => s + r.value, 0))

const trendOption = lineOption({
  x: ['06-25', '06-26', '06-27', '06-28', '06-29', '06-30', '07-01'],
  series: [{ name: '新增用户', data: [850, 1020, 1180, 960, 1320, 1450, 1256], color: '#1890ff' }],
  area: true,
})

const channelPieOption = pieOption({
  data: [
    { name: 'TikTok', value: 580 },
    { name: 'Facebook', value: 320 },
    { name: 'Google', value: 210 },
    { name: '其他', value: 146 },
  ],
  doughnut: true,
})

onMounted(async () => {
  stats.value = await deliveryApi.acquisitionStats()
  const data = await deliveryApi.acquisitionByChannel()
  byChannelTable.value = data.map((d: any) => ({ ...d, payRate: (Math.random() * 0.4 + 0.1).toFixed(2) + '%', cost: '¥' + Math.floor(Math.random() * 30 + 10) }))
})
</script>
