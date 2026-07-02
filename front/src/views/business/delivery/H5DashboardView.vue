<template>
  <div class="biz-page">
    <PageHeader title="H5看板" desc="H5落地页投放效果数据" />

    <div class="biz-stats-grid">
      <StatCard v-for="s in stats" :key="s.label" :label="s.label" :value="s.value" :diff="s.diff" icon="📊" :icon-color="s.color" />
    </div>

    <FilterBar>
      <el-form-item label="日期"><el-date-picker v-model="date" type="daterange" range-separator="至" style="width:240px" /></el-form-item>
      <el-form-item label="渠道"><el-select v-model="channel" placeholder="全部" style="width:140px"><el-option v-for="c in channels" :key="c" :label="c" :value="c" /></el-select></el-form-item>
      <el-form-item label="站点"><el-input v-model="site" placeholder="请输入站点" style="width:160px" /></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <div class="biz-charts-row">
      <ChartPanel title="访问趋势" :option="visitOption" :height="260" />
      <BaseCard title="Top 落地页">
        <div v-for="(p, i) in topPages" :key="i" class="biz-progress-item">
          <div class="biz-progress-header">
            <span class="biz-progress-label">{{ p.name }}</span>
            <span class="biz-progress-value">{{ p.visits.toLocaleString() }} ({{ p.rate }}%)</span>
          </div>
          <div class="biz-progress-bar"><div class="biz-progress-fill" :class="progressColor(i)" :style="{ width: p.rate + '%' }" /></div>
        </div>
      </BaseCard>
    </div>

    <BaseTable :data="table" :total="48" :page="1" :page-size="10" @change="onPageChange">
      <template #actions>
        <el-button>导出</el-button>
      </template>
      <el-table-column prop="name" label="落地页" />
      <el-table-column prop="site" label="所属站点" />
      <el-table-column prop="visits" label="访问数" />
      <el-table-column prop="conv" label="转化率" />
      <el-table-column prop="recharge" label="充值金额" />
      <el-table-column prop="roi" label="ROI" />
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '已发布' ? 'green' : 'orange'">{{ row.status }}</StatusTag></template></el-table-column>
    </BaseTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import PageHeader from '../../../components/PageHeader.vue'
import StatCard from '../../../components/StatCard.vue'
import FilterBar from '../../../components/FilterBar.vue'
import ChartPanel from '../../../components/ChartPanel.vue'
import BaseCard from '../../../components/BaseCard.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'
import { lineOption } from '../../../composables/useECharts'
import { deliveryApi, siteApi } from '../../../api/business'

const channels = ['TikTok', 'Facebook', 'Google', 'Twitter']
const date = ref([])
const channel = ref('')
const site = ref('')
const stats = ref<any[]>([])
const topPages = ref<any[]>([])
const table = ref<any[]>([])

const visitOption = lineOption({
  x: ['06-25', '06-26', '06-27', '06-28', '06-29', '06-30', '07-01'],
  series: [{ name: '访问数', data: [3200, 4100, 5300, 4800, 6200, 7500, 6800], color: '#1890ff' }],
  area: true,
})

function progressColor(i: number) {
  return (['blue', 'green', 'orange', 'red', 'purple'] as const)[i % 5]
}

onMounted(async () => {
  stats.value = (await deliveryApi.h5Stats()).map((s: any) => ({ ...s, color: s.iconColor }))
  topPages.value = await deliveryApi.h5TopPages()
  table.value = await siteApi.pages()
})
function onPageChange() {}
</script>
