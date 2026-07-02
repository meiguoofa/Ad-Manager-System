<template>
  <div class="biz-page">
    <PageHeader title="参数设置" desc="系统全局参数配置" />

    <FilterBar>
      <el-form-item label="名称"><el-input v-model="name" placeholder="请输入参数名" style="width:180px" /></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="48" :page="1" :page-size="10" @change="onPageChange">
      <template #actions><el-button type="primary" @click="openAdd">新建参数</el-button></template>
      <el-table-column prop="name" label="参数名" />
      <el-table-column prop="key" label="参数键" />
      <el-table-column prop="value" label="参数值" />
      <el-table-column prop="desc" label="描述" />
      <el-table-column prop="updateTime" label="更新时间" />
      <el-table-column label="操作" width="160"><template #default="{ row }"><el-button text type="primary" @click="openEdit(row)">编辑</el-button><el-button text type="danger">删除</el-button></template></el-table-column>
    </BaseTable>

    <BaseModal v-model="addVisible" :title="editForm.id ? '编辑参数' : '新建参数'" width="480px">
      <el-form label-position="top" :model="editForm">
        <el-form-item label="参数名"><el-input v-model="editForm.name" /></el-form-item>
        <el-form-item label="参数键"><el-input v-model="editForm.key" /></el-form-item>
        <el-form-item label="参数值"><el-input v-model="editForm.value" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="editForm.desc" type="textarea" /></el-form-item>
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
import BaseModal from '../../../components/BaseModal.vue'
import { systemApi } from '../../../api/business'

const name = ref('')
const table = ref<any[]>([])
const addVisible = ref(false)
const editForm = ref<any>({ id: 0, name: '', key: '', value: '', desc: '' })

onMounted(async () => { table.value = await systemApi.params() })
function onPageChange() {}
function openAdd() { editForm.value = { id: 0, name: '', key: '', value: '', desc: '' }; addVisible.value = true }
function openEdit(row: any) { editForm.value = { ...row }; addVisible.value = true }
</script>
