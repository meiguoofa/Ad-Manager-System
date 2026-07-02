<template>
  <div class="biz-page">
    <PageHeader title="操作日志" desc="用户系统操作日志" />

    <FilterBar>
      <el-form-item label="操作人"><el-input v-model="operator" placeholder="请输入操作人" style="width:160px" /></el-form-item>
      <el-form-item label="模块"><el-select v-model="module" placeholder="全部" style="width:140px"><el-option label="用户管理" value="user" /><el-option label="投放管理" value="delivery" /><el-option label="短剧管理" value="drama" /></el-select></el-form-item>
      <el-form-item label="状态"><el-select v-model="status" placeholder="全部" style="width:140px"><el-option label="成功" value="ok" /><el-option label="失败" value="fail" /></el-select></el-form-item>
      <el-form-item label="日期"><el-date-picker v-model="date" type="daterange" range-separator="至" style="width:240px" /></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="86" :page="1" :page-size="10" @change="onPageChange">
      <template #actions><el-button>导出</el-button></template>
      <el-table-column prop="operator" label="操作人" />
      <el-table-column prop="module" label="模块" />
      <el-table-column prop="action" label="操作" />
      <el-table-column prop="target" label="对象" />
      <el-table-column prop="ip" label="IP" />
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '成功' ? 'green' : 'red'">{{ row.status }}</StatusTag></template></el-table-column>
      <el-table-column prop="time" label="时间" />
      <el-table-column label="操作" width="100"><template #default><el-button text type="primary">详情</el-button></template></el-table-column>
    </BaseTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import PageHeader from '../../../components/PageHeader.vue'
import FilterBar from '../../../components/FilterBar.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'
import { systemApi } from '../../../api/business'

const operator = ref('')
const module = ref('')
const status = ref('')
const date = ref([])
const table = ref<any[]>([])

onMounted(async () => { table.value = await systemApi.operationLogs() })
function onPageChange() {}
</script>
