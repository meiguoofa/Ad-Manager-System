<template>
  <div class="biz-page">
    <PageHeader title="检测链接" desc="管理投放检测链接" />

    <FilterBar>
      <el-form-item label="渠道"><el-select v-model="channel" placeholder="全部" style="width:140px"><el-option v-for="c in channels" :key="c" :label="c" :value="c" /></el-select></el-form-item>
      <el-form-item label="状态"><el-select v-model="status" placeholder="全部" style="width:120px"><el-option label="正常" value="ok" /><el-option label="异常" value="err" /></el-select></el-form-item>
      <el-form-item label="名称"><el-input v-model="name" placeholder="请输入名称" style="width:160px" /></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="48" :page="1" :page-size="10" @change="onPageChange">
      <template #actions>
        <el-button>批量检测</el-button>
        <el-button type="primary" @click="openAdd">新建链接</el-button>
      </template>
      <el-table-column prop="name" label="链接名称" />
      <el-table-column prop="url" label="URL" show-overflow-tooltip />
      <el-table-column prop="channel" label="渠道" />
      <el-table-column prop="adGroup" label="广告组" />
      <el-table-column label="状态"><template #default="{ row }"><span class="biz-dot-status" :class="row.status === '正常' ? 'success' : row.status === '异常' ? 'error' : 'info'">{{ row.status }}</span></template></el-table-column>
      <el-table-column prop="responseTime" label="响应时间" />
      <el-table-column prop="lastCheck" label="最后检测" />
      <el-table-column label="操作" width="160"><template #default="{ row }"><el-button text type="primary" @click="openEdit(row)">编辑</el-button><el-button text type="primary">检测</el-button></template></el-table-column>
    </BaseTable>

    <BaseModal v-model="editVisible" :title="editForm.id ? '编辑链接' : '新建链接'" width="560px">
      <el-form label-position="top" :model="editForm">
        <el-form-item label="名称"><el-input v-model="editForm.name" /></el-form-item>
        <el-form-item label="URL"><el-input v-model="editForm.url" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="渠道"><el-select v-model="editForm.channel" class="biz-w-full"><el-option v-for="c in channels" :key="c" :label="c" :value="c" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="广告组"><el-input v-model="editForm.adGroup" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="editVisible = false">确定</el-button>
      </template>
    </BaseModal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import PageHeader from '../../../components/PageHeader.vue'
import FilterBar from '../../../components/FilterBar.vue'
import BaseTable from '../../../components/BaseTable.vue'
import BaseModal from '../../../components/BaseModal.vue'
import { deliveryApi } from '../../../api/business'

const channels = ['TikTok', 'Facebook', 'Google', 'Twitter']
const channel = ref('')
const status = ref('')
const name = ref('')
const table = ref<any[]>([])
const editVisible = ref(false)
const editForm = ref<any>({ id: 0, name: '', url: '', channel: '', adGroup: '' })

onMounted(async () => { table.value = await deliveryApi.linkDetect() })
function onPageChange() {}
function openAdd() { editForm.value = { id: 0, name: '', url: '', channel: '', adGroup: '' }; editVisible.value = true }
function openEdit(row: any) { editForm.value = { ...row }; editVisible.value = true }
</script>
