<template>
  <div class="biz-page">
    <PageHeader title="充值明细" desc="广告账户充值明细" />

    <div class="biz-stats-grid">
      <StatCard label="今日充值" value="¥96,320.10" :diff="8.3" icon="💰" icon-color="blue" />
      <StatCard label="本周充值" value="¥685,560.00" :diff="12.6" icon="📊" icon-color="green" />
      <StatCard label="本月充值" value="¥2,356,800.00" :diff="18.9" icon="📈" icon-color="orange" />
      <StatCard label="待审核" value="3" :diff="-2" icon="⏳" icon-color="red" />
    </div>

    <FilterBar>
      <el-form-item label="账户"><el-input v-model="account" placeholder="请输入账户" style="width:160px" /></el-form-item>
      <el-form-item label="类型"><el-select v-model="type" placeholder="全部" style="width:140px"><el-option label="充值" value="recharge" /><el-option label="退款" value="refund" /></el-select></el-form-item>
      <el-form-item label="状态"><el-select v-model="status" placeholder="全部" style="width:140px"><el-option label="成功" value="ok" /><el-option label="处理中" value="pending" /></el-select></el-form-item>
      <el-form-item label="日期"><el-date-picker v-model="date" type="daterange" range-separator="至" style="width:240px" /></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="86" :page="1" :page-size="10" @change="onPageChange">
      <template #actions>
        <el-button>导出</el-button>
        <el-button type="primary" @click="openAdd">手动充值</el-button>
      </template>
      <el-table-column prop="orderNo" label="订单号" />
      <el-table-column prop="account" label="账户" />
      <el-table-column prop="amount" label="金额" />
      <el-table-column prop="channel" label="渠道" />
      <el-table-column prop="type" label="类型" />
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '成功' ? 'green' : row.status === '失败' ? 'red' : 'orange'">{{ row.status }}</StatusTag></template></el-table-column>
      <el-table-column prop="operator" label="操作人" />
      <el-table-column prop="time" label="时间" />
    </BaseTable>

    <BaseModal v-model="addVisible" title="手动充值" width="480px">
      <el-form label-position="top">
        <el-form-item label="账户"><el-select class="biz-w-full"><el-option v-for="i in 10" :key="i" :label="`A${String(i).padStart(4,'0')}`" :value="i" /></el-select></el-form-item>
        <el-form-item label="金额"><el-input placeholder="¥0.00" /></el-form-item>
        <el-form-item label="渠道"><el-select class="biz-w-full"><el-option label="支付宝" value="alipay" /><el-option label="微信" value="wechat" /></el-select></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="addVisible = false">确定</el-button>
      </template>
    </BaseModal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import PageHeader from '../../../components/PageHeader.vue'
import StatCard from '../../../components/StatCard.vue'
import FilterBar from '../../../components/FilterBar.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'
import BaseModal from '../../../components/BaseModal.vue'
import { deliveryApi } from '../../../api/business'

const account = ref('')
const type = ref('')
const status = ref('')
const date = ref([])
const table = ref<any[]>([])
const addVisible = ref(false)

onMounted(async () => { table.value = await deliveryApi.rechargeDetail() })
function onPageChange() {}
function openAdd() { addVisible.value = true }
</script>
