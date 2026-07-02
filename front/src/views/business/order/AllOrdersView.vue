<template>
  <div class="biz-page">
    <PageHeader title="全部订单" desc="所有订单管理" />

    <div class="biz-stats-grid">
      <StatCard v-for="s in stats" :key="s.label" :label="s.label" :value="s.value" :diff="s.diff" :icon="s.icon" :icon-color="s.iconColor" />
    </div>

    <FilterBar>
      <el-form-item label="类型"><el-select v-model="type" placeholder="全部" style="width:140px"><el-option label="充值" value="recharge" /><el-option label="解锁" value="unlock" /><el-option label="会员" value="vip" /></el-select></el-form-item>
      <el-form-item label="状态"><el-select v-model="status" placeholder="全部" style="width:140px"><el-option label="已完成" value="done" /><el-option label="已退款" value="refund" /></el-select></el-form-item>
      <el-form-item label="订单号"><el-input v-model="orderNo" placeholder="请输入订单号" style="width:180px" /></el-form-item>
      <el-form-item label="日期"><el-date-picker v-model="date" type="daterange" range-separator="至" style="width:240px" /></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="86" :page="1" :page-size="10" @change="onPageChange">
      <template #actions>
        <el-button>导出</el-button>
      </template>
      <el-table-column prop="orderNo" label="订单号" />
      <el-table-column prop="user" label="用户" />
      <el-table-column prop="type" label="类型" />
      <el-table-column prop="amount" label="金额" />
      <el-table-column prop="payMethod" label="支付方式" />
      <el-table-column prop="drama" label="短剧" />
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '已完成' ? 'green' : row.status === '已退款' ? 'orange' : row.status === '已取消' ? 'default' : 'orange'">{{ row.status }}</StatusTag></template></el-table-column>
      <el-table-column prop="time" label="时间" />
      <el-table-column label="操作" width="140"><template #default="{ row }"><el-button text type="primary" @click="goDetail(row)">详情</el-button><el-button text type="primary">退款</el-button></template></el-table-column>
    </BaseTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '../../../components/PageHeader.vue'
import StatCard from '../../../components/StatCard.vue'
import FilterBar from '../../../components/FilterBar.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'
import { orderApi } from '../../../api/business'

const router = useRouter()
const type = ref('')
const status = ref('')
const orderNo = ref('')
const date = ref([])
const stats = ref<any[]>([])
const table = ref<any[]>([])

onMounted(async () => {
  stats.value = await orderApi.stats()
  table.value = await orderApi.all()
})
function onPageChange() {}
function goDetail(row: any) { router.push(`/order/detail/${row.id}`) }
</script>
