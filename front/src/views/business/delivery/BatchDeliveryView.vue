<template>
  <div class="biz-page">
    <PageHeader title="批量投放" desc="批量创建投放任务" />

    <FilterBar>
      <el-form-item label="状态"><el-select v-model="status" placeholder="全部" style="width:140px"><el-option label="进行中" value="running" /><el-option label="已完成" value="done" /></el-select></el-form-item>
      <el-form-item label="短剧"><el-input v-model="drama" placeholder="请输入短剧" style="width:160px" /></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="48" :page="1" :page-size="10" @change="onPageChange">
      <template #actions>
        <el-button>导入</el-button>
        <el-button type="primary" @click="openAdd">新建批次</el-button>
      </template>
      <el-table-column prop="batch" label="批次号" />
      <el-table-column prop="drama" label="短剧" />
      <el-table-column prop="channel" label="渠道" />
      <el-table-column prop="count" label="广告数" />
      <el-table-column prop="cost" label="消耗" />
      <el-table-column label="状态"><template #default="{ row }"><span class="biz-dot-status" :class="row.status === '进行中' ? 'info' : row.status === '已完成' ? 'success' : 'warning'">{{ row.status }}</span></template></el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" width="160"><template #default><el-button text type="primary">详情</el-button><el-button text type="danger">停止</el-button></template></el-table-column>
    </BaseTable>

    <BaseModal v-model="addVisible" title="新建批次" width="640px">
      <el-form label-position="top">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="短剧"><el-select class="biz-w-full"><el-option v-for="i in 20" :key="i" :label="`短剧-${String(i).padStart(2,'0')}`" :value="i" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="渠道"><el-select class="biz-w-full"><el-option v-for="c in channels" :key="c" :label="c" :value="c" /></el-select></el-form-item></el-col>
        </el-row>
        <el-form-item label="广告组数量"><el-input-number :min="1" :max="100" /></el-form-item>
        <el-form-item label="预算"><el-input placeholder="¥0.00" /></el-form-item>
        <el-form-item label="备注"><el-input type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="addVisible = false">立即创建</el-button>
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
const status = ref('')
const drama = ref('')
const table = ref<any[]>([])
const addVisible = ref(false)

onMounted(async () => { table.value = await deliveryApi.batchDelivery() })
function onPageChange() {}
function openAdd() { addVisible.value = true }
</script>
