<template>
  <div class="biz-page">
    <PageHeader title="菜单管理" desc="系统菜单和权限配置" />

    <FilterBar>
      <el-form-item label="名称"><el-input v-model="name" placeholder="请输入菜单名" style="width:180px" /></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :show-pagination="false" :total="0">
      <template #actions><el-button type="primary" @click="openAdd">新建菜单</el-button></template>
      <el-table-column prop="name" label="菜单名称" />
      <el-table-column prop="type" label="类型"><template #default="{ row }"><StatusTag :type="row.type === '目录' ? 'blue' : row.type === '菜单' ? 'green' : 'orange'">{{ row.type }}</StatusTag></template></el-table-column>
      <el-table-column prop="icon" label="图标" />
      <el-table-column prop="path" label="路由" />
      <el-table-column prop="parent" label="上级" />
      <el-table-column prop="sort" label="排序" />
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '启用' ? 'green' : 'default'">{{ row.status }}</StatusTag></template></el-table-column>
      <el-table-column label="操作" width="200"><template #default><el-button text type="primary" @click="openAdd">编辑</el-button><el-button text type="primary">新增子菜单</el-button><el-button text type="danger">删除</el-button></template></el-table-column>
    </BaseTable>

    <BaseModal v-model="addVisible" title="新建菜单" width="560px">
      <el-form label-position="top">
        <el-form-item label="类型"><el-radio-group><el-radio-button label="目录" /><el-radio-button label="菜单" /><el-radio-button label="按钮" /></el-radio-group></el-form-item>
        <el-form-item label="菜单名称"><el-input /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="图标"><el-input /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="路由"><el-input /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="上级菜单"><el-select class="biz-w-full"><el-option label="顶级" value="0" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="排序"><el-input-number :min="0" /></el-form-item></el-col>
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
const table = ref<any[]>([])
const addVisible = ref(false)

onMounted(async () => { table.value = await systemApi.menus() })
function openAdd() { addVisible.value = true }
</script>
