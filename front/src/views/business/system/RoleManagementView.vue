<template>
  <div class="biz-page">
    <PageHeader title="角色管理" desc="系统角色和权限配置" />

    <FilterBar>
      <el-form-item label="名称"><el-input v-model="name" placeholder="请输入角色名" style="width:180px" /></el-form-item>
      <el-form-item label="状态"><el-select v-model="status" placeholder="全部" style="width:140px"><el-option label="启用" value="enabled" /><el-option label="停用" value="disabled" /></el-select></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="48" :page="1" :page-size="10" @change="onPageChange">
      <template #actions><el-button type="primary" @click="openAdd">新建角色</el-button></template>
      <el-table-column prop="name" label="角色名" />
      <el-table-column prop="code" label="角色编码" />
      <el-table-column prop="desc" label="描述" />
      <el-table-column prop="userCount" label="用户数" />
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '启用' ? 'green' : 'default'">{{ row.status }}</StatusTag></template></el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" width="200"><template #default><el-button text type="primary" @click="openPermission">权限配置</el-button><el-button text type="primary" @click="openAdd">编辑</el-button><el-button text type="danger">删除</el-button></template></el-table-column>
    </BaseTable>

    <BaseModal v-model="addVisible" title="新建角色" width="480px">
      <el-form label-position="top">
        <el-form-item label="角色名"><el-input /></el-form-item>
        <el-form-item label="角色编码"><el-input /></el-form-item>
        <el-form-item label="描述"><el-input type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="addVisible = false">保存</el-button>
      </template>
    </BaseModal>

    <BaseModal v-model="permVisible" title="权限配置" width="640px">
      <el-tree :data="permTree" show-checkbox default-expand-all node-key="id" :props="{ label: 'name' }" />
      <template #footer>
        <el-button @click="permVisible = false">取消</el-button>
        <el-button type="primary" @click="permVisible = false">保存</el-button>
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
const permVisible = ref(false)
const permTree = [
  { id: 1, name: '首页', children: [{ id: 11, name: '工作台' }] },
  { id: 2, name: '投放系统', children: [
    { id: 21, name: '每日消耗' }, { id: 22, name: 'H5看板' }, { id: 23, name: '广告看板' },
    { id: 24, name: '拉新数据' }, { id: 25, name: 'ROI数据' }, { id: 26, name: '素材统计' },
  ]},
  { id: 3, name: '短剧内容', children: [{ id: 31, name: '短剧管理' }, { id: 32, name: '商品库' }, { id: 33, name: '素材库' }] },
  { id: 4, name: '独立站管理', children: [{ id: 41, name: '站点列表' }, { id: 42, name: '落地页' }] },
  { id: 5, name: '订单管理', children: [{ id: 51, name: '三方支付' }, { id: 52, name: '全部订单' }, { id: 53, name: '退款订单' }] },
  { id: 6, name: '系统管理', children: [{ id: 61, name: '用户管理' }, { id: 62, name: '角色管理' }, { id: 63, name: '审计日志' }] },
]

onMounted(async () => { table.value = await systemApi.roles() })
function onPageChange() {}
function openAdd() { addVisible.value = true }
function openPermission() { permVisible.value = true }
</script>
