<template>
  <div class="biz-page">
    <PageHeader title="剧集分析" desc="单集表现与用户行为分析" />

    <FilterBar>
      <el-form-item label="短剧"><el-select v-model="drama" placeholder="全部" style="width:180px"><el-option v-for="i in 20" :key="i" :label="`短剧-${String(i).padStart(2,'0')}`" :value="i" /></el-select></el-form-item>
      <el-form-item label="日期"><el-date-picker v-model="date" type="daterange" range-separator="至" style="width:240px" /></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <ChartPanel title="用户留存曲线" :option="retentionOption" :height="280" />

    <BaseTable :data="table" :total="80" :page="1" :page-size="10" @change="onPageChange">
      <template #actions><el-button>导出</el-button></template>
      <el-table-column prop="episode" label="集数" />
      <el-table-column prop="playCount" label="播放量" />
      <el-table-column prop="completionRate" label="完播率" />
      <el-table-column prop="payRate" label="付费率" />
      <el-table-column prop="recharge" label="充值" />
      <el-table-column prop="retention" label="留存率" />
      <el-table-column prop="conversion" label="转化率" />
    </BaseTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import PageHeader from '../../../components/PageHeader.vue'
import FilterBar from '../../../components/FilterBar.vue'
import ChartPanel from '../../../components/ChartPanel.vue'
import BaseTable from '../../../components/BaseTable.vue'
import { lineOption } from '../../../composables/useECharts'
import { dramaApi } from '../../../api/business'

const drama = ref('')
const date = ref([])
const table = ref<any[]>([])

const retentionOption = lineOption({
  x: ['第1集', '第3集', '第5集', '第7集', '第9集', '第11集', '第12集'],
  series: [{ name: '留存率', data: [100, 78, 62, 51, 43, 36, 30], color: '#1890ff' }],
  area: true,
})

onMounted(async () => { table.value = await dramaApi.episodeAnalysis() })
function onPageChange() {}
</script>
