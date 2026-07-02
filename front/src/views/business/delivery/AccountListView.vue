<template>
  <div class="biz-page">
    <PageHeader title="账户列表" desc="广告投放账户管理" />

    <div class="biz-stats-grid">
      <StatCard label="账户总数" value="56" :diff="2" icon="💳" icon-color="blue" />
      <StatCard label="正常账户" value="48" :diff="1" icon="✅" icon-color="green" />
      <StatCard label="总余额" value="¥2,356,800" :diff="8.5" icon="💰" icon-color="orange" />
      <StatCard label="今日消耗" value="¥128,560" :diff="12.5" icon="📈" icon-color="red" />
    </div>

    <FilterBar>
      <el-form-item label="渠道"><el-select v-model="channel" placeholder="全部" style="width:140px"><el-option v-for="c in channels" :key="c" :label="c" :value="c" /></el-select></el-form-item>
      <el-form-item label="状态"><el-select v-model="status" placeholder="全部" style="width:140px"><el-option label="正常" value="ok" /><el-option label="冻结" value="frozen" /></el-select></el-form-item>
      <el-form-item label="账户"><el-input v-model="account" placeholder="请输入账户" style="width:160px" /></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="56" :page="1" :page-size="10" @change="onPageChange">
      <template #actions>
        <el-button>导出</el-button>
        <el-button type="primary" @click="openAdd">新增账户</el-button>
      </template>
      <el-table-column prop="account" label="账户" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="channel" label="渠道" />
      <el-table-column prop="balance" label="余额" />
      <el-table-column prop="todayCost" label="今日消耗" />
      <el-table-column prop="todayRecharge" label="今日充值" />
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '正常' ? 'green' : row.status === '冻结' ? 'red' : 'orange'">{{ row.status }}</StatusTag></template></el-table-column>
      <el-table-column prop="owner" label="负责人" />
      <el-table-column label="操作" width="140"><template #default="{ row }"><el-button text type="primary" @click="goDetail(row)">详情</el-button><el-button text type="primary">充值</el-button></template></el-table-column>
    </BaseTable>

    <BaseModal v-model="addVisible" title="新增账户" width="480px">
      <el-form label-position="top">
        <el-form-item label="账户名"><el-input /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="渠道"><el-select class="biz-w-full"><el-option v-for="c in channels" :key="c" :label="c" :value="c" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="负责人"><el-input /></el-form-item></el-col>
        </el-row>
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
import { useRouter } from 'vue-router'
import PageHeader from '../../../components/PageHeader.vue'
import StatCard from '../../../components/StatCard.vue'
import FilterBar from '../../../components/FilterBar.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'
import BaseModal from '../../../components/BaseModal.vue'
import { deliveryApi } from '../../../api/business'

const router = useRouter()
const channels = ['TikTok', 'Facebook', 'Google', 'Twitter']
const channel = ref('')
const status = ref('')
const account = ref('')
const table = ref<any[]>([])
const addVisible = ref(false)

onMounted(async () => { table.value = await deliveryApi.accountList() })
function onPageChange() {}
function openAdd() { addVisible.value = true }
function goDetail(row: any) { router.push(`/delivery/account-detail/${row.id}`) }
</script>
