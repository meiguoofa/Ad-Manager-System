<template>
  <div class="biz-page">
    <PageHeader title="退款订单" desc="退款申请和订单管理" />

    <div class="biz-stats-grid">
      <StatCard label="待审核" value="12" :diff="3" icon="⏳" icon-color="orange" />
      <StatCard label="今日退款" value="¥3,560" :diff="-5.2" icon="↩️" icon-color="red" />
      <StatCard label="本月退款" value="¥35,680" :diff="2.1" icon="💰" icon-color="blue" />
      <StatCard label="平均处理时长" value="2.5小时" :diff="-12" icon="⚡" icon-color="green" />
    </div>

    <FilterBar>
      <el-form-item label="状态"><el-select v-model="status" placeholder="全部" style="width:140px"><el-option label="待审核" value="pending" /><el-option label="已通过" value="ok" /><el-option label="已拒绝" value="rejected" /></el-select></el-form-item>
      <el-form-item label="原因"><el-select v-model="reason" placeholder="全部" style="width:140px"><el-option label="未观看" value="unwatched" /><el-option label="误购" value="mistake" /></el-select></el-form-item>
      <el-form-item label="订单号"><el-input v-model="orderNo" placeholder="请输入订单号" style="width:180px" /></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="48" :page="1" :page-size="10" @change="onPageChange">
      <template #actions><el-button>批量审核</el-button></template>
      <el-table-column prop="refundNo" label="退款单号" />
      <el-table-column prop="orderNo" label="原订单号" />
      <el-table-column prop="user" label="用户" />
      <el-table-column prop="amount" label="金额" />
      <el-table-column prop="reason" label="原因" />
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '已通过' ? 'green' : row.status === '已拒绝' ? 'red' : row.status === '已退款' ? 'blue' : 'orange'">{{ row.status }}</StatusTag></template></el-table-column>
      <el-table-column prop="applyTime" label="申请时间" />
      <el-table-column label="操作" width="180"><template #default="{ row }"><el-button text type="primary" @click="goDetail(row)">详情</el-button><el-button text type="primary" v-if="row.status === '待审核'">审核</el-button></template></el-table-column>
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
const status = ref('')
const reason = ref('')
const orderNo = ref('')
const table = ref<any[]>([])

onMounted(async () => { table.value = await orderApi.refunds() })
function onPageChange() {}
function goDetail(row: any) { router.push(`/order/refund-detail/${row.id}`) }
</script>
