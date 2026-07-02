<template>
  <div class="biz-page">
    <PageHeader title="站点详情" back />
    <BaseCard title="基本信息">
      <div class="biz-info-grid cols-4">
        <div class="biz-info-item"><span class="biz-info-label">站点名称</span><span class="biz-info-value">站点-1</span></div>
        <div class="biz-info-item"><span class="biz-info-label">域名</span><span class="biz-info-value">site1.example.com</span></div>
        <div class="biz-info-item"><span class="biz-info-label">状态</span><span class="biz-info-value"><StatusTag type="green">运行中</StatusTag></span></div>
        <div class="biz-info-item"><span class="biz-info-label">创建时间</span><span class="biz-info-value">2026-05-01</span></div>
        <div class="biz-info-item"><span class="biz-info-label">今日访问</span><span class="biz-info-value">6,800</span></div>
        <div class="biz-info-item"><span class="biz-info-label">今日充值</span><span class="biz-info-value">¥19,200</span></div>
        <div class="biz-info-item"><span class="biz-info-label">页面数</span><span class="biz-info-value">12</span></div>
        <div class="biz-info-item"><span class="biz-info-label">负责人</span><span class="biz-info-value">张三</span></div>
      </div>
    </BaseCard>

    <div class="biz-chart-grid-2">
      <ChartPanel title="访问趋势" :option="visitOption" :height="260" />
      <ChartPanel title="营收趋势" :option="revenueOption" :height="260" />
    </div>

    <BaseCard title="页面列表">
      <template #actions><el-button type="primary" @click="$router.push('/site/page-editor')">新建页面</el-button></template>
      <BaseTable :data="pages" :show-pagination="false" :total="0">
        <el-table-column prop="name" label="页面名" />
        <el-table-column prop="url" label="URL" show-overflow-tooltip />
        <el-table-column prop="visits" label="访问数" />
        <el-table-column prop="conv" label="转化率" />
        <el-table-column prop="recharge" label="充值" />
        <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '已发布' ? 'green' : 'orange'">{{ row.status }}</StatusTag></template></el-table-column>
        <el-table-column label="操作" width="120"><template #default><el-button text type="primary">编辑</el-button></template></el-table-column>
      </BaseTable>
    </BaseCard>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import PageHeader from '../../../components/PageHeader.vue'
import BaseCard from '../../../components/BaseCard.vue'
import ChartPanel from '../../../components/ChartPanel.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'
import { lineOption } from '../../../composables/useECharts'
import { siteApi } from '../../../api/business'

const visitOption = lineOption({
  x: ['06-25', '06-26', '06-27', '06-28', '06-29', '06-30', '07-01'],
  series: [{ name: '访问数', data: [3200, 4100, 5300, 4800, 6200, 7500, 6800], color: '#1890ff' }],
  area: true,
})
const revenueOption = lineOption({
  x: ['06-25', '06-26', '06-27', '06-28', '06-29', '06-30', '07-01'],
  series: [{ name: '营收', data: [8500, 12000, 15600, 13200, 17800, 21500, 19200], color: '#52c41a' }],
  area: true,
})

const pages = ref<any[]>([])
onMounted(async () => { pages.value = await siteApi.pages() })
</script>
