<template>
  <div class="biz-page">
    <PageHeader title="媒体渠道" desc="广告投放媒体渠道配置" />

    <FilterBar>
      <el-form-item label="名称"><el-input v-model="name" placeholder="请输入渠道名" style="width:180px" /></el-form-item>
      <el-form-item label="状态"><el-select v-model="status" placeholder="全部" style="width:140px"><el-option label="启用" value="enabled" /><el-option label="停用" value="disabled" /></el-select></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="48" :page="1" :page-size="10" @change="onPageChange">
      <template #actions><el-button type="primary" @click="openAdd">新增渠道</el-button></template>
      <el-table-column prop="name" label="渠道名" />
      <el-table-column prop="code" label="编码" />
      <el-table-column prop="region" label="支持区域" />
      <el-table-column prop="currency" label="币种" />
      <el-table-column prop="accountCount" label="账户数" />
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '启用' ? 'green' : 'default'">{{ row.status }}</StatusTag></template></el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" width="160"><template #default="{ row }"><el-button text type="primary" @click="openEdit(row)">编辑</el-button><el-button text type="danger">删除</el-button></template></el-table-column>
    </BaseTable>

    <BaseModal v-model="addVisible" :title="editForm.id ? '编辑渠道' : '新增渠道'" width="480px">
      <el-form label-position="top" :model="editForm">
        <el-form-item label="渠道名"><el-input v-model="editForm.name" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="区域"><el-select v-model="editForm.region" class="biz-w-full"><el-option label="全球" value="global" /><el-option label="北美" value="na" /><el-option label="东南亚" value="sea" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="币种"><el-select v-model="editForm.currency" class="biz-w-full"><el-option label="USD" value="usd" /><el-option label="CNY" value="cny" /></el-select></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="addVisible = false">保存</el-button>
      </template>
    </BaseModal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import PageHeader from '../../../components/PageHeader.vue'
import FilterBar from '../../../components/FilterBar.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'
import BaseModal from '../../../components/BaseModal.vue'
import { systemApi } from '../../../api/business'

const name = ref('')
const status = ref('')
const table = ref<any[]>([])
const addVisible = ref(false)
const editForm = ref<any>({ id: 0, name: '', region: '', currency: '' })

onMounted(async () => { table.value = await systemApi.mediaChannels() })
function onPageChange() {}
function openAdd() { editForm.value = { id: 0, name: '', region: '', currency: '' }; addVisible.value = true }
function openEdit(row: any) { editForm.value = { ...row }; addVisible.value = true }
</script>
