<template>
  <div class="biz-page">
    <div class="biz-detail-header">
      <span class="biz-back-btn" @click="$router.push('/material/library')">← 返回素材库</span>
      <h1 class="biz-detail-title">素材详情 - MAT-00128</h1>
      <div class="biz-detail-actions">
        <el-button>编辑</el-button>
        <el-button type="primary">复制</el-button>
        <el-button type="danger">删除</el-button>
      </div>
    </div>

    <div class="biz-charts-row">
      <BaseCard title="素材预览">
        <div style="width:100%;height:200px;background:linear-gradient(135deg,#bae7ff,#91d5ff);border-radius:8px;display:flex;align-items:center;justify-content:center;font-size:64px;margin-bottom:16px">🎬</div>
        <div class="biz-info-grid" style="grid-template-columns:1fr">
          <div class="biz-info-item"><span class="biz-info-label">素材名称</span><span class="biz-info-value">爆款短剧引流素材-01</span></div>
          <div class="biz-info-item"><span class="biz-info-label">素材类型</span><span class="biz-info-value">视频 · MP4</span></div>
          <div class="biz-info-item"><span class="biz-info-label">文件大小</span><span class="biz-info-value">15.6 MB</span></div>
          <div class="biz-info-item"><span class="biz-info-label">分辨率</span><span class="biz-info-value">1080 x 1920</span></div>
          <div class="biz-info-item"><span class="biz-info-label">时长</span><span class="biz-info-value">00:32</span></div>
          <div class="biz-info-item"><span class="biz-info-label">创建时间</span><span class="biz-info-value">2024-01-15 14:30</span></div>
          <div class="biz-info-item"><span class="biz-info-label">所属分类</span><span class="biz-info-value"><StatusTag type="blue">短剧引流</StatusTag></span></div>
        </div>
      </BaseCard>

      <BaseCard title="投放效果数据">
        <template #actions>
          <BaseTabs v-model="period" :tabs="[{ key: '7', label: '近7天' }, { key: '30', label: '近30天' }, { key: 'all', label: '累计' }]" />
        </template>
        <div class="biz-stats-grid" style="grid-template-columns:repeat(4,1fr);margin-bottom:20px">
          <StatCard label="展示量" value="258.6万" :diff="12.3" />
          <StatCard label="点击量" value="36,520" :diff="8.5" />
          <StatCard label="CTR" value="1.41%" :diff="-5" />
          <StatCard label="ROI" value="2.58" :diff="12" />
        </div>
        <ChartPanel :option="trendOption" :height="180" />
      </BaseCard>
    </div>

    <BaseCard title="关联广告">
      <BaseTable :data="ads" :show-pagination="false" :total="0">
        <el-table-column prop="id" label="广告ID" />
        <el-table-column prop="name" label="广告名称" />
        <el-table-column label="渠道"><template #default="{ row }"><StatusTag type="blue">{{ row.channel }}</StatusTag></template></el-table-column>
        <el-table-column prop="cost" label="消耗" />
        <el-table-column prop="impressions" label="展示" />
        <el-table-column prop="clicks" label="点击" />
        <el-table-column prop="conversions" label="转化" />
        <el-table-column label="ROI"><template #default="{ row }"><span :class="parseFloat(row.roi) >= 2.5 ? 'biz-text-success' : parseFloat(row.roi) >= 2 ? 'biz-text-warning' : 'biz-text-error'">{{ row.roi }}</span></template></el-table-column>
        <el-table-column label="状态"><template #default="{ row }"><span class="biz-dot-status" :class="row.status === '运行中' ? 'success' : row.status === '已暂停' ? 'warning' : 'default'">{{ row.status }}</span></template></el-table-column>
        <el-table-column label="操作" width="100"><template #default><el-button text type="primary">查看</el-button></template></el-table-column>
      </BaseTable>
    </BaseCard>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { generateTableData, formatMoney, formatNumber, random, pick } from '../../../mock/utils'
import BaseCard from '../../../components/BaseCard.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'
import StatCard from '../../../components/StatCard.vue'
import ChartPanel from '../../../components/ChartPanel.vue'
import BaseTabs from '../../../components/BaseTabs.vue'
import { lineOption } from '../../../composables/useECharts'

const period = ref('7')
const ads = ref<any[]>([])

const trendOption = lineOption({
  x: ['06-25', '06-26', '06-27', '06-28', '06-29', '06-30', '07-01', '07-02'],
  series: [{ name: '消耗', data: [120, 100, 110, 80, 90, 60, 70, 50], color: '#1890ff' }],
  area: true,
})

onMounted(() => {
  ads.value = generateTableData(6, [
    { key: 'id', generator: (i) => 'AD-' + String(i).padStart(5, '0') },
    { key: 'name', generator: (i) => `广告计划-${String(i).padStart(3, '0')}` },
    { key: 'channel', generator: () => pick(['TikTok', 'Facebook', 'Google']) },
    { key: 'cost', generator: () => formatMoney(random(5000, 50000)) },
    { key: 'impressions', generator: () => formatNumber(random(10000, 500000)) },
    { key: 'clicks', generator: () => formatNumber(random(500, 10000)) },
    { key: 'conversions', generator: () => formatNumber(random(50, 1000)) },
    { key: 'roi', generator: () => (random(15, 35) / 10).toFixed(2) },
    { key: 'status', generator: () => pick(['运行中', '已暂停', '已结束']) },
  ])
})
</script>
