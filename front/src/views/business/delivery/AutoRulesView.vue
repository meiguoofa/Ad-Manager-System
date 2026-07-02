<template>
  <div class="biz-page">
    <PageHeader title="自动规则" desc="自动化投放规则管理" />

    <FilterBar>
      <el-form-item label="状态"><el-select v-model="status" placeholder="全部" style="width:140px"><el-option label="运行中" value="running" /><el-option label="已暂停" value="paused" /></el-select></el-form-item>
      <el-form-item label="范围"><el-select v-model="scope" placeholder="全部" style="width:140px"><el-option label="全部" value="all" /><el-option label="TikTok" value="tiktok" /></el-select></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="48" :page="1" :page-size="10" @change="onPageChange">
      <template #actions>
        <el-button type="primary" @click="openAdd">新建规则</el-button>
      </template>
      <el-table-column prop="name" label="规则名称" />
      <el-table-column prop="condition" label="触发条件" />
      <el-table-column prop="action" label="执行动作" />
      <el-table-column prop="scope" label="范围" />
      <el-table-column label="状态"><template #default="{ row }"><el-switch :model-value="row.status === '运行中'" /></template></el-table-column>
      <el-table-column prop="triggerCount" label="触发次数" />
      <el-table-column prop="lastTrigger" label="最后触发" />
      <el-table-column label="操作" width="160"><template #default="{ row }"><el-button text type="primary" @click="goDetail(row)">详情</el-button><el-button text type="danger">删除</el-button></template></el-table-column>
    </BaseTable>

    <BaseModal v-model="addVisible" title="新建规则" width="640px">
      <el-form label-position="top">
        <el-form-item label="规则名称"><el-input /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="触发条件"><el-select class="biz-w-full"><el-option label="ROI<50%" value="1" /><el-option label="消耗>¥5000" value="2" /><el-option label="CPC>¥1.5" value="3" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="执行动作"><el-select class="biz-w-full"><el-option label="暂停广告组" value="1" /><el-option label="提高预算" value="2" /></el-select></el-form-item></el-col>
        </el-row>
        <el-form-item label="范围"><el-select class="biz-w-full"><el-option label="全部" value="all" /><el-option label="TikTok" value="tiktok" /></el-select></el-form-item>
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
import { useRouter } from 'vue-router'
import PageHeader from '../../../components/PageHeader.vue'
import FilterBar from '../../../components/FilterBar.vue'
import BaseTable from '../../../components/BaseTable.vue'
import BaseModal from '../../../components/BaseModal.vue'
import { deliveryApi } from '../../../api/business'

const router = useRouter()
const status = ref('')
const scope = ref('')
const table = ref<any[]>([])
const addVisible = ref(false)

onMounted(async () => { table.value = await deliveryApi.autoRules() })
function onPageChange() {}
function openAdd() { addVisible.value = true }
function goDetail(row: any) { router.push(`/delivery/rule-detail/${row.id}`) }
</script>
