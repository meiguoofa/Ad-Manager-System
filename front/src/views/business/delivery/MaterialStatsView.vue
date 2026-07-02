<template>
  <div class="biz-page">
    <PageHeader title="素材统计" desc="广告素材效果分析" />

    <div class="biz-stats-grid">
      <StatCard label="素材总数" value="1,256" :diff="3.5" diff-label="较上周" icon="🎨" icon-color="blue" />
      <StatCard label="使用中素材" value="856" :diff="5.2" diff-label="较上周" icon="✅" icon-color="green" />
      <StatCard label="平均CTR" value="4.85%" :diff="0.6" diff-label="较上周" icon="📈" icon-color="orange" />
      <StatCard label="最佳素材ROI" value="2.85" :diff="12.3" diff-label="较上周" icon="🏆" icon-color="purple" />
    </div>

    <FilterBar>
      <el-form-item label="类型"><el-select v-model="type" placeholder="全部" style="width:140px"><el-option label="视频" value="video" /><el-option label="图片" value="image" /><el-option label="文案" value="text" /></el-select></el-form-item>
      <el-form-item label="渠道"><el-select v-model="channel" placeholder="全部" style="width:140px"><el-option v-for="c in channels" :key="c" :label="c" :value="c" /></el-select></el-form-item>
      <el-form-item label="状态"><el-select v-model="status" placeholder="全部" style="width:140px"><el-option label="使用中" value="active" /><el-option label="已停用" value="inactive" /></el-select></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="86" :page="1" :page-size="10" @change="onPageChange">
      <template #actions><el-button>导出</el-button></template>
      <el-table-column prop="material" label="素材" />
      <el-table-column prop="type" label="类型" />
      <el-table-column prop="channel" label="渠道" />
      <el-table-column prop="cost" label="消耗" />
      <el-table-column prop="impressions" label="曝光量" />
      <el-table-column prop="clicks" label="点击量" />
      <el-table-column prop="ctr" label="CTR" />
      <el-table-column prop="conversions" label="转化数" />
      <el-table-column label="ROI"><template #default="{ row }"><span :class="parseFloat(row.roi) >= 1 ? 'biz-text-success' : 'biz-text-error'">{{ row.roi }}</span></template></el-table-column>
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '使用中' ? 'green' : row.status === '审核中' ? 'orange' : 'default'">{{ row.status }}</StatusTag></template></el-table-column>
    </BaseTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import PageHeader from '../../../components/PageHeader.vue'
import StatCard from '../../../components/StatCard.vue'
import FilterBar from '../../../components/FilterBar.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'
import { deliveryApi } from '../../../api/business'

const channels = ['TikTok', 'Facebook', 'Google', 'Twitter']
const type = ref('')
const channel = ref('')
const status = ref('')
const table = ref<any[]>([])

onMounted(async () => { table.value = await deliveryApi.materialStats() })
function onPageChange() {}
</script>
