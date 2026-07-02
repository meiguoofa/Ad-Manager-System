<template>
  <div class="biz-page">
    <PageHeader title="区间看板" desc="按时间区间查看投放数据趋势" back />

    <FilterBar>
      <el-form-item label="对比区间">
        <el-date-picker v-model="range" type="daterange" range-separator="至" style="width:240px" />
      </el-form-item>
      <el-form-item label="对比维度">
        <el-select v-model="dim" style="width:120px"><el-option label="按天" value="day" /><el-option label="按周" value="week" /><el-option label="按月" value="month" /></el-select>
      </el-form-item>
      <el-form-item label="渠道">
        <el-select v-model="channel" style="width:140px"><el-option label="全部渠道" value="all" /><el-option label="TikTok" value="tiktok" /><el-option label="Facebook" value="facebook" /></el-select>
      </el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <ChartPanel title="消耗对比趋势" :option="trendOption" :height="300">
      <template #actions>
        <el-checkbox v-model="showCurrent">本期</el-checkbox>
        <el-checkbox v-model="showPrev">上期</el-checkbox>
        <el-checkbox v-model="showLastYear">去年同期</el-checkbox>
      </template>
    </ChartPanel>

    <div class="biz-chart-grid-2">
      <BaseCard title="本期核心指标">
        <div class="biz-info-grid cols-2">
          <div class="biz-info-item"><span class="biz-info-label">总消耗</span><span class="biz-info-value">¥456,789</span></div>
          <div class="biz-info-item"><span class="biz-info-label">总展示</span><span class="biz-info-value">258.6万</span></div>
          <div class="biz-info-item"><span class="biz-info-label">总点击</span><span class="biz-info-value">36,520</span></div>
          <div class="biz-info-item"><span class="biz-info-label">总转化</span><span class="biz-info-value">2,856</span></div>
          <div class="biz-info-item"><span class="biz-info-label">平均ROI</span><span class="biz-info-value biz-text-success">2.35</span></div>
          <div class="biz-info-item"><span class="biz-info-label">转化率</span><span class="biz-info-value">7.82%</span></div>
        </div>
      </BaseCard>
      <BaseCard title="环比变化">
        <div v-for="m in changes" :key="m.name" class="biz-flex biz-flex-between biz-mb-16">
          <span class="biz-text-muted" style="font-size:13px">{{ m.name }}</span>
          <span class="biz-text-bold" :class="m.up ? 'biz-text-success' : 'biz-text-error'">{{ m.value }}</span>
        </div>
      </BaseCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import PageHeader from '../../../components/PageHeader.vue'
import FilterBar from '../../../components/FilterBar.vue'
import ChartPanel from '../../../components/ChartPanel.vue'
import BaseCard from '../../../components/BaseCard.vue'
import { lineOption } from '../../../composables/useECharts'

const range = ref([])
const dim = ref('day')
const channel = ref('all')
const showCurrent = ref(true)
const showPrev = ref(true)
const showLastYear = ref(false)

const trendOption = lineOption({
  x: ['06-25', '06-26', '06-27', '06-28', '06-29', '06-30', '07-01', '07-02', '07-03', '07-04', '07-05'],
  series: [
    { name: '本期', data: [220, 180, 200, 150, 170, 120, 140, 100, 130, 90, 110], color: '#1890ff' },
    { name: '上期', data: [240, 220, 230, 200, 210, 180, 190, 160, 180, 150, 170], color: '#52c41a' },
  ],
  area: true,
})

const changes = [
  { name: '消耗', value: '+12.5%', up: true },
  { name: '展示', value: '+8.3%', up: true },
  { name: '点击', value: '+15.2%', up: true },
  { name: '转化', value: '+6.8%', up: true },
  { name: 'ROI', value: '-2.1%', up: false },
  { name: '转化率', value: '-0.5%', up: false },
]
</script>
