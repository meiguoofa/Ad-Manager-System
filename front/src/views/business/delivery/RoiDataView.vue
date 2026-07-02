<template>
  <div class="biz-page">
    <PageHeader title="ROI数据" desc="ROI数据分析" />

    <div class="biz-stats-grid">
      <StatCard label="综合ROI" value="74.8%" :diff="-2.1" diff-label="较昨日" icon="📊" icon-color="blue" />
      <StatCard label="回本周期" value="14.5天" :diff="-1.2" diff-label="较上周" icon="⏱️" icon-color="green" />
      <StatCard label="总利润" value="¥356,800" :diff="18.6" diff-label="较上周" icon="💰" icon-color="orange" />
      <StatCard label="盈利短剧数" value="92" :diff="5.2" diff-label="较上周" icon="🎬" icon-color="purple" />
    </div>

    <FilterBar>
      <el-form-item label="日期"><el-date-picker v-model="date" type="daterange" range-separator="至" style="width:240px" /></el-form-item>
      <el-form-item label="短剧"><el-input v-model="drama" placeholder="请输入短剧" style="width:160px" /></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <ChartPanel title="ROI趋势（近7天）" :option="trendOption" :height="280" />

    <BaseTable :data="table" :total="86" :page="1" :page-size="10" @change="onPageChange">
      <template #actions><el-button>导出</el-button></template>
      <el-table-column prop="date" label="日期" />
      <el-table-column prop="drama" label="短剧" />
      <el-table-column prop="cost" label="消耗" />
      <el-table-column prop="recharge" label="充值" />
      <el-table-column label="ROI"><template #default="{ row }"><span :class="parseFloat(row.roi) >= 1 ? 'biz-text-success' : 'biz-text-error'">{{ row.roi }}</span></template></el-table-column>
      <el-table-column prop="payback" label="回本周期" />
      <el-table-column prop="profit" label="利润" />
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '盈利' ? 'green' : row.status === '亏损' ? 'red' : 'default'">{{ row.status }}</StatusTag></template></el-table-column>
    </BaseTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import PageHeader from '../../../components/PageHeader.vue'
import StatCard from '../../../components/StatCard.vue'
import FilterBar from '../../../components/FilterBar.vue'
import ChartPanel from '../../../components/ChartPanel.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'
import { lineOption } from '../../../composables/useECharts'
import { deliveryApi } from '../../../api/business'

const date = ref([])
const drama = ref('')
const table = ref<any[]>([])

const trendOption = lineOption({
  x: ['06-25', '06-26', '06-27', '06-28', '06-29', '06-30', '07-01'],
  series: [{ name: 'ROI', data: [68, 72, 75, 71, 78, 80, 74.8], color: '#1890ff' }],
  area: true,
})

onMounted(async () => { table.value = await deliveryApi.roi() })
function onPageChange() {}
</script>
