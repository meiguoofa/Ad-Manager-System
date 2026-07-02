<template>
  <div class="biz-page">
    <PageHeader title="语言管理" desc="多语言配置管理" />

    <FilterBar>
      <el-form-item label="名称"><el-input v-model="name" placeholder="请输入语言名" style="width:180px" /></el-form-item>
      <el-form-item label="状态"><el-select v-model="status" placeholder="全部" style="width:140px"><el-option label="启用" value="enabled" /><el-option label="停用" value="disabled" /></el-select></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="48" :page="1" :page-size="10" @change="onPageChange">
      <template #actions><el-button type="primary" @click="openAdd">新增语言</el-button></template>
      <el-table-column prop="name" label="语言名称" />
      <el-table-column prop="code" label="语言编码" />
      <el-table-column label="翻译进度"><template #default="{ row }"><div class="biz-progress-bar"><div class="biz-progress-fill blue" :style="{ width: row.progress }"></div></div><span style="font-size:12px;color:#8c8c8c">{{ row.progress }}</span></template></el-table-column>
      <el-table-column label="默认"><template #default="{ row }"><StatusTag :type="row.default === '是' ? 'blue' : 'default'">{{ row.default }}</StatusTag></template></el-table-column>
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '启用' ? 'green' : 'default'">{{ row.status }}</StatusTag></template></el-table-column>
      <el-table-column label="操作" width="180"><template #default><el-button text type="primary" @click="openAdd">编辑</el-button><el-button text type="primary">翻译</el-button><el-button text type="danger">删除</el-button></template></el-table-column>
    </BaseTable>

    <BaseModal v-model="addVisible" title="新增语言" width="480px">
      <el-form label-position="top">
        <el-form-item label="语言名称"><el-input /></el-form-item>
        <el-form-item label="语言编码"><el-input placeholder="如 zh-CN" /></el-form-item>
        <el-form-item label="设为默认"><el-switch /></el-form-item>
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

onMounted(async () => { table.value = await systemApi.languages() })
function onPageChange() {}
function openAdd() { addVisible.value = true }
</script>
