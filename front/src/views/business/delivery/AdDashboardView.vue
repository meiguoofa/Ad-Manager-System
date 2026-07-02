<template>
  <div class="biz-page">
    <PageHeader title="广告看板" desc="广告投放效果实时数据" />

    <div class="biz-stats-grid">
      <StatCard label="广告组数" value="186" :diff="3.2" diff-label="较昨日" icon="📈" icon-color="blue" />
      <StatCard label="今日消耗" value="¥128,560.25" :diff="12.5" diff-label="较昨日" icon="💰" icon-color="green" />
      <StatCard label="今日充值" value="¥96,320.10" :diff="8.3" diff-label="较昨日" icon="💳" icon-color="orange" />
      <StatCard label="平均ROI" value="74.8%" :diff="-2.1" diff-label="较昨日" icon="📊" icon-color="red" />
    </div>

    <FilterBar>
      <el-form-item label="渠道"><el-select v-model="channel" placeholder="全部" style="width:140px"><el-option v-for="c in channels" :key="c" :label="c" :value="c" /></el-select></el-form-item>
      <el-form-item label="广告组"><el-input v-model="adGroup" placeholder="请输入广告组" style="width:160px" /></el-form-item>
      <el-form-item label="状态"><el-select v-model="status" placeholder="全部" style="width:140px"><el-option label="运行中" value="running" /><el-option label="已暂停" value="paused" /></el-select></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="186" :page="1" :page-size="10" @change="onPageChange">
      <template #actions>
        <el-button>导出</el-button>
        <el-button type="primary">新建广告组</el-button>
      </template>
      <el-table-column prop="adGroup" label="广告组" />
      <el-table-column label="渠道"><template #default="{ row }"><StatusTag type="blue">{{ row.channel }}</StatusTag></template></el-table-column>
      <el-table-column prop="cost" label="消耗" />
      <el-table-column prop="impressions" label="曝光量" />
      <el-table-column prop="clicks" label="点击量" />
      <el-table-column prop="ctr" label="CTR" />
      <el-table-column prop="conversions" label="转化数" />
      <el-table-column prop="recharge" label="充值" />
      <el-table-column label="ROI"><template #default="{ row }"><span :class="parseFloat(row.roi) >= 1 ? 'biz-text-success' : 'biz-text-error'">{{ row.roi }}</span></template></el-table-column>
      <el-table-column label="状态"><template #default="{ row }"><span class="biz-dot-status" :class="row.status === '活跃' ? 'success' : row.status === '暂停' ? 'warning' : 'default'">{{ row.status }}</span></template></el-table-column>
      <el-table-column label="操作" width="120"><template #default><el-button text type="primary">详情</el-button></template></el-table-column>
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
const channel = ref('')
const adGroup = ref('')
const status = ref('')
const table = ref<any[]>([])

onMounted(async () => { table.value = await deliveryApi.adDashboard() })
function onPageChange() {}
</script>
