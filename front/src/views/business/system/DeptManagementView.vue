<template>
  <div class="biz-page">
    <PageHeader title="部门管理" desc="组织架构和部门信息" />

    <div class="dept-layout">
      <BaseCard title="部门架构" class="dept-tree-card">
        <template #actions><el-button type="primary" size="small" @click="openAdd">新增</el-button></template>
        <el-tree :data="tree" :props="{ label: 'name', children: 'children' }" default-expand-all node-key="id" highlight-current @node-click="onNodeClick">
          <template #default="{ node, data }">
            <span>{{ data.icon }} {{ node.label }}</span>
          </template>
        </el-tree>
      </BaseCard>

      <BaseCard :title="selected ? `${selected.name} - 部门详情` : '部门详情'" class="dept-detail-card">
        <div v-if="selected" class="biz-info-grid cols-2">
          <div class="biz-info-item"><span class="biz-info-label">部门名称</span><span class="biz-info-value">{{ selected.name }}</span></div>
          <div class="biz-info-item"><span class="biz-info-label">部门ID</span><span class="biz-info-value">{{ selected.id }}</span></div>
          <div class="biz-info-item"><span class="biz-info-label">负责人</span><span class="biz-info-value">张三</span></div>
          <div class="biz-info-item"><span class="biz-info-label">人数</span><span class="biz-info-value">{{ randomInt(5, 30) }}</span></div>
          <div class="biz-info-item"><span class="biz-info-label">创建时间</span><span class="biz-info-value">2026-01-01</span></div>
          <div class="biz-info-item"><span class="biz-info-label">状态</span><span class="biz-info-value"><StatusTag type="green">正常</StatusTag></span></div>
        </div>
        <div v-else class="biz-empty"><div class="biz-empty-icon">👈</div><div class="biz-empty-text">请选择左侧部门</div></div>

        <template v-if="selected">
          <div class="biz-divider" />
          <div class="biz-card-title biz-mb-16">子部门</div>
          <BaseTable :data="children" :show-pagination="false" :total="0">
            <el-table-column prop="name" label="部门名" />
            <el-table-column prop="leader" label="负责人" />
            <el-table-column prop="count" label="人数" />
            <el-table-column label="操作" width="160"><template #default><el-button text type="primary">编辑</el-button><el-button text type="danger">删除</el-button></template></el-table-column>
          </BaseTable>
        </template>
      </BaseCard>
    </div>

    <BaseModal v-model="addVisible" title="新增部门" width="480px">
      <el-form label-position="top">
        <el-form-item label="部门名称"><el-input /></el-form-item>
        <el-form-item label="上级部门"><el-select class="biz-w-full"><el-option label="仙之大陆" value="1" /></el-select></el-form-item>
        <el-form-item label="负责人"><el-input /></el-form-item>
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
import BaseCard from '../../../components/BaseCard.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'
import BaseModal from '../../../components/BaseModal.vue'
import { systemApi } from '../../../api/business'

const tree = ref<any[]>([])
const selected = ref<any>(null)
const children = ref<any[]>([])
const addVisible = ref(false)

onMounted(async () => { tree.value = await systemApi.deptTree() })

function onNodeClick(node: any) {
  selected.value = node
  children.value = (node.children || []).map((c: any) => ({ name: c.name, leader: '张三', count: Math.floor(Math.random() * 20 + 3) }))
}
function openAdd() { addVisible.value = true }
function randomInt(min: number, max: number) { return Math.floor(Math.random() * (max - min + 1)) + min }
</script>

<style scoped>
.dept-layout { display: grid; grid-template-columns: 360px 1fr; gap: 16px; }
.dept-tree-card :deep(.biz-card-body) { max-height: 600px; overflow-y: auto; }
</style>
