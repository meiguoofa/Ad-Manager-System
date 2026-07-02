<template>
  <div class="biz-page">
    <PageHeader title="充值模板" desc="管理短剧充值模板" />

    <FilterBar>
      <el-form-item label="短剧"><el-select v-model="drama" placeholder="全部" style="width:180px"><el-option v-for="i in 20" :key="i" :label="`短剧-${String(i).padStart(2,'0')}`" :value="i" /></el-select></el-form-item>
      <el-form-item label="渠道"><el-select v-model="channel" placeholder="全部" style="width:140px"><el-option v-for="c in channels" :key="c" :label="c" :value="c" /></el-select></el-form-item>
      <el-form-item label="状态"><el-select v-model="status" placeholder="全部" style="width:120px"><el-option label="启用" value="enabled" /><el-option label="停用" value="disabled" /></el-select></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="48" :page="1" :page-size="10" @change="onPageChange">
      <template #actions>
        <el-button>导出</el-button>
        <el-button type="primary" @click="openAdd">新建模板</el-button>
      </template>
      <el-table-column prop="name" label="模板名称" />
      <el-table-column prop="amount" label="金额" />
      <el-table-column prop="bonus" label="赠品" />
      <el-table-column prop="drama" label="关联短剧" />
      <el-table-column prop="channel" label="渠道" />
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '启用' ? 'green' : 'default'">{{ row.status }}</StatusTag></template></el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" width="160"><template #default="{ row }"><el-button text type="primary" @click="openEdit(row)">编辑</el-button><el-button text type="danger">删除</el-button></template></el-table-column>
    </BaseTable>

    <BaseModal v-model="editVisible" :title="editForm.name ? '编辑模板' : '新建模板'" width="560px">
      <el-form label-position="top" :model="editForm">
        <el-form-item label="模板名称"><el-input v-model="editForm.name" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="金额"><el-input v-model="editForm.amount" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="赠品"><el-input v-model="editForm.bonus" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="短剧"><el-select v-model="editForm.drama" class="biz-w-full"><el-option v-for="i in 20" :key="i" :label="`短剧-${String(i).padStart(2,'0')}`" :value="i" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="渠道"><el-select v-model="editForm.channel" class="biz-w-full"><el-option v-for="c in channels" :key="c" :label="c" :value="c" /></el-select></el-form-item></el-col>
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
import StatusTag from '../../../components/StatusTag.vue'
import BaseModal from '../../../components/BaseModal.vue'
import { deliveryApi } from '../../../api/business'

const channels = ['TikTok', 'Facebook', 'Google', 'Twitter']
const drama = ref('')
const channel = ref('')
const status = ref('')
const table = ref<any[]>([])
const editVisible = ref(false)
const editForm = ref<any>({ name: '', amount: '', bonus: '', drama: '', channel: '' })

onMounted(async () => { table.value = await deliveryApi.rechargeTemplate() })
function onPageChange() {}
function openAdd() { editForm.value = { name: '', amount: '', bonus: '', drama: '', channel: '' }; editVisible.value = true }
function openEdit(row: any) { editForm.value = { ...row }; editVisible.value = true }
</script>
