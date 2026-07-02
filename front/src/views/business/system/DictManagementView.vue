<template>
  <div class="biz-page">
    <PageHeader title="字典管理" desc="系统数据字典" />

    <FilterBar>
      <el-form-item label="名称"><el-input v-model="name" placeholder="请输入字典名" style="width:180px" /></el-form-item>
      <el-form-item label="状态"><el-select v-model="status" placeholder="全部" style="width:140px"><el-option label="启用" value="enabled" /><el-option label="停用" value="disabled" /></el-select></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="48" :page="1" :page-size="10" @change="onPageChange">
      <template #actions><el-button type="primary" @click="openAdd">新建字典</el-button></template>
      <el-table-column prop="name" label="字典名称" />
      <el-table-column prop="code" label="字典编码" />
      <el-table-column prop="desc" label="描述" />
      <el-table-column prop="itemCount" label="字典项数" />
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '启用' ? 'green' : 'default'">{{ row.status }}</StatusTag></template></el-table-column>
      <el-table-column prop="updateTime" label="更新时间" />
      <el-table-column label="操作" width="200"><template #default><el-button text type="primary" @click="openItems">字典项</el-button><el-button text type="primary" @click="openAdd">编辑</el-button><el-button text type="danger">删除</el-button></template></el-table-column>
    </BaseTable>

    <BaseModal v-model="addVisible" title="新建字典" width="480px">
      <el-form label-position="top">
        <el-form-item label="字典名称"><el-input /></el-form-item>
        <el-form-item label="字典编码"><el-input /></el-form-item>
        <el-form-item label="描述"><el-input type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="addVisible = false">保存</el-button>
      </template>
    </BaseModal>

    <BaseModal v-model="itemsVisible" title="字典项管理" width="640px">
      <BaseTable :data="items" :show-pagination="false" :total="0">
        <template #actions><el-button type="primary" size="small">新增</el-button></template>
        <el-table-column prop="label" label="标签" />
        <el-table-column prop="value" label="值" />
        <el-table-column prop="sort" label="排序" />
        <el-table-column label="操作" width="120"><template #default><el-button text type="primary">编辑</el-button><el-button text type="danger">删除</el-button></template></el-table-column>
      </BaseTable>
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
const itemsVisible = ref(false)
const items = ref([
  { label: '待支付', value: 'pending', sort: 1 },
  { label: '已支付', value: 'paid', sort: 2 },
  { label: '已取消', value: 'cancelled', sort: 3 },
  { label: '已退款', value: 'refunded', sort: 4 },
])

onMounted(async () => { table.value = await systemApi.dicts() })
function onPageChange() {}
function openAdd() { addVisible.value = true }
function openItems() { itemsVisible.value = true }
</script>
