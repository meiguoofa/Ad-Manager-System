<template>
  <div class="biz-page">
    <PageHeader title="投手看板" desc="投手绩效与投放效果分析" back />

    <FilterBar>
      <el-form-item label="时间范围"><el-date-picker v-model="range" type="daterange" range-separator="至" style="width:240px" /></el-form-item>
      <el-form-item label="团队">
        <el-select v-model="team" style="width:160px"><el-option label="全部团队" value="all" /><el-option label="投放一组" value="t1" /><el-option label="投放二组" value="t2" /><el-option label="投放三组" value="t3" /></el-select>
      </el-form-item>
      <template #actions><el-button>重置</el-button><el-button type="primary">查询</el-button></template>
    </FilterBar>

    <div class="biz-stats-grid">
      <StatCard label="投手人数" value="28人" :diff="3" diff-label="本月新增" icon="👥" icon-color="blue" />
      <StatCard label="管理账户数" value="156个" :diff="12" diff-label="本月" icon="💳" icon-color="green" />
      <StatCard label="人均消耗" value="¥19.1万" :diff="8.5" diff-label="环比" icon="💰" icon-color="purple" />
      <StatCard label="平均ROI" value="2.28" :diff="-5" diff-label="0.05 环比" icon="📊" icon-color="orange" />
    </div>

    <BaseTable :data="table" :total="28" :page="1" :page-size="10" @change="onPageChange">
      <template #actions>
        <el-select v-model="sortBy" size="small" style="width:140px"><el-option label="按消耗排序" value="cost" /><el-option label="按ROI排序" value="roi" /><el-option label="按转化排序" value="cvr" /></el-select>
      </template>
      <el-table-column prop="rank" label="排名" width="80"><template #default="{ row }"><span class="biz-tag" :class="row.rank <= 3 ? 'biz-tag-blue' : 'biz-tag-default'">{{ row.rank }}</span></template></el-table-column>
      <el-table-column label="投手" width="180"><template #default="{ row }"><div class="biz-flex biz-gap-8"><div class="biz-avatar sm">{{ row.name.charAt(0) }}</div><span>{{ row.name }}</span></div></template></el-table-column>
      <el-table-column prop="team" label="所属团队" />
      <el-table-column prop="accounts" label="管理账户" />
      <el-table-column prop="ads" label="在投广告" />
      <el-table-column label="本月消耗"><template #default="{ row }"><span class="biz-text-bold">{{ row.cost }}</span></template></el-table-column>
      <el-table-column prop="revenue" label="本月收入" />
      <el-table-column label="ROI"><template #default="{ row }"><span :class="parseFloat(row.roi) >= 2.5 ? 'biz-text-success' : parseFloat(row.roi) >= 2 ? 'biz-text-warning' : 'biz-text-error'">{{ row.roi }}</span></template></el-table-column>
      <el-table-column prop="cvr" label="转化率" />
      <el-table-column label="操作" width="100"><template #default><el-button text type="primary">详情</el-button></template></el-table-column>
    </BaseTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { generateTableData, randomName, formatMoney, random, pick } from '../../../mock/utils'
import PageHeader from '../../../components/PageHeader.vue'
import FilterBar from '../../../components/FilterBar.vue'
import StatCard from '../../../components/StatCard.vue'
import BaseTable from '../../../components/BaseTable.vue'

const range = ref([])
const team = ref('all')
const sortBy = ref('cost')
const table = ref<any[]>([])

onMounted(() => {
  table.value = generateTableData(10, [
    { key: 'rank', generator: (i) => i },
    { key: 'name', generator: () => randomName() },
    { key: 'team', generator: () => pick(['投放一组', '投放二组', '投放三组']) },
    { key: 'accounts', generator: () => random(3, 15) + '个' },
    { key: 'ads', generator: () => random(10, 80) + '个' },
    { key: 'cost', generator: () => formatMoney(random(50000, 500000)) },
    { key: 'revenue', generator: () => formatMoney(random(100000, 1500000)) },
    { key: 'roi', generator: () => (random(15, 35) / 10).toFixed(2) },
    { key: 'cvr', generator: () => random(5, 15).toFixed(2) + '%' },
  ])
})
function onPageChange() {}
</script>
