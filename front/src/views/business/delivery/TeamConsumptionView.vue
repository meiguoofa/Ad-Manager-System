<template>
  <div class="biz-page">
    <PageHeader title="组员消耗" desc="团队成员消耗明细与绩效分析" back />

    <FilterBar>
      <el-form-item label="时间"><el-date-picker v-model="range" type="daterange" range-separator="至" style="width:240px" /></el-form-item>
      <el-form-item label="团队"><el-select v-model="team" style="width:160px"><el-option label="全部团队" value="all" /><el-option label="投放一组" value="t1" /><el-option label="投放二组" value="t2" /></el-select></el-form-item>
      <el-form-item label="成员姓名"><el-input v-model="name" placeholder="请输入" style="width:160px" /></el-form-item>
      <template #actions><el-button>重置</el-button><el-button type="primary">查询</el-button></template>
    </FilterBar>

    <BaseCard title="团队消耗分布">
      <div class="biz-chart-grid-3">
        <div v-for="t in teams" :key="t.name" class="biz-text-center biz-p-16" style="background:#fafafa;border-radius:8px">
          <div class="biz-text-muted biz-mb-8" style="font-size:14px">{{ t.name }}</div>
          <div style="font-size:24px;font-weight:600;color:var(--biz-text);margin-bottom:8px">{{ t.value }}</div>
          <div class="biz-stat-diff up" style="justify-content:center;display:flex">↑ {{ t.up }}%</div>
        </div>
      </div>
    </BaseCard>

    <BaseTable :data="table" :total="28" :page="1" :page-size="12" @change="onPageChange">
      <template #actions><el-button>导出</el-button></template>
      <el-table-column label="成员" width="180"><template #default="{ row }"><div class="biz-flex biz-gap-8"><div class="biz-avatar sm">{{ row.name.charAt(0) }}</div><span>{{ row.name }}</span></div></template></el-table-column>
      <el-table-column prop="team" label="所属团队" />
      <el-table-column label="岗位"><template #default="{ row }"><StatusTag :type="row.role === '高级投手' ? 'purple' : row.role === '投手' ? 'blue' : 'default'">{{ row.role }}</StatusTag></template></el-table-column>
      <el-table-column prop="todayCost" label="今日消耗" />
      <el-table-column prop="weekCost" label="本周消耗" />
      <el-table-column label="本月消耗"><template #default="{ row }"><span class="biz-text-bold">{{ row.monthCost }}</span></template></el-table-column>
      <el-table-column label="本月ROI"><template #default="{ row }"><span :class="parseFloat(row.roi) >= 2.5 ? 'biz-text-success' : parseFloat(row.roi) >= 2 ? 'biz-text-warning' : 'biz-text-error'">{{ row.roi }}</span></template></el-table-column>
      <el-table-column prop="conversions" label="本月转化" />
      <el-table-column label="消耗占比"><template #default="{ row }"><div class="biz-flex biz-gap-8" style="align-items:center"><div style="flex:1;max-width:80px;height:6px;background:#f0f0f0;border-radius:3px"><div :style="{ width: row.percent, height: '100%', background: '#1890ff', borderRadius: '3px' }"></div></div><span class="biz-text-muted" style="font-size:12px">{{ row.percent }}</span></div></template></el-table-column>
      <el-table-column label="操作" width="100"><template #default><el-button text type="primary">详情</el-button></template></el-table-column>
    </BaseTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { generateTableData, randomName, formatMoney, formatNumber, random, pick } from '../../../mock/utils'
import PageHeader from '../../../components/PageHeader.vue'
import FilterBar from '../../../components/FilterBar.vue'
import BaseCard from '../../../components/BaseCard.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'

const range = ref([])
const team = ref('all')
const name = ref('')
const table = ref<any[]>([])
const teams = ref([
  { name: '投放一组', value: formatMoney(random(300000, 800000)), up: random(5, 20).toFixed(1) },
  { name: '投放二组', value: formatMoney(random(300000, 800000)), up: random(5, 20).toFixed(1) },
  { name: '投放三组', value: formatMoney(random(300000, 800000)), up: random(5, 20).toFixed(1) },
])

onMounted(() => {
  table.value = generateTableData(12, [
    { key: 'name', generator: () => randomName() },
    { key: 'team', generator: () => pick(['投放一组', '投放二组', '投放三组']) },
    { key: 'role', generator: () => pick(['高级投手', '投手', '助理投手']) },
    { key: 'todayCost', generator: () => formatMoney(random(5000, 50000)) },
    { key: 'weekCost', generator: () => formatMoney(random(30000, 300000)) },
    { key: 'monthCost', generator: () => formatMoney(random(100000, 800000)) },
    { key: 'roi', generator: () => (random(15, 35) / 10).toFixed(2) },
    { key: 'conversions', generator: () => formatNumber(random(100, 2000)) },
    { key: 'percent', generator: () => random(3, 15).toFixed(1) + '%' },
  ])
})
function onPageChange() {}
</script>
