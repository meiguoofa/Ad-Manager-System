<template>
  <div class="biz-page">
    <PageHeader title="域名配置" desc="管理站点绑定的域名" />

    <FilterBar>
      <el-form-item label="站点"><el-select v-model="site" placeholder="全部" style="width:160px"><el-option v-for="i in 8" :key="i" :label="`站点-${i}`" :value="i" /></el-select></el-form-item>
      <el-form-item label="类型"><el-select v-model="type" placeholder="全部" style="width:140px"><el-option label="主域名" value="main" /><el-option label="备用域名" value="backup" /></el-select></el-form-item>
      <el-form-item label="域名"><el-input v-model="domain" placeholder="请输入域名" style="width:180px" /></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="48" :page="1" :page-size="10" @change="onPageChange">
      <template #actions>
        <el-button type="primary" @click="openAdd">绑定域名</el-button>
      </template>
      <el-table-column prop="domain" label="域名" />
      <el-table-column prop="site" label="所属站点" />
      <el-table-column prop="type" label="类型" />
      <el-table-column label="SSL"><template #default="{ row }"><StatusTag :type="row.ssl === '已配置' ? 'green' : 'red'">{{ row.ssl }}</StatusTag></template></el-table-column>
      <el-table-column label="DNS"><template #default="{ row }"><span class="biz-dot-status" :class="row.dns === '已解析' ? 'success' : row.dns === '解析中' ? 'info' : 'error'">{{ row.dns }}</span></template></el-table-column>
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '正常' ? 'green' : 'red'">{{ row.status }}</StatusTag></template></el-table-column>
      <el-table-column prop="bindTime" label="绑定时间" />
      <el-table-column label="操作" width="160"><template #default><el-button text type="primary">编辑</el-button><el-button text type="danger">解绑</el-button></template></el-table-column>
    </BaseTable>

    <BaseCard title="DNS 配置指引">
      <div class="biz-alert biz-alert-info">
        <div class="alert-content">请将域名 CNAME 解析至 <code>sites.xianzhidalu.com</code>，或 A 记录解析至 <code>47.91.221.33</code>。配置完成后系统会自动检测。</div>
      </div>
    </BaseCard>

    <BaseModal v-model="addVisible" title="绑定域名" width="480px">
      <el-form label-position="top">
        <el-form-item label="域名"><el-input placeholder="example.com" /></el-form-item>
        <el-form-item label="站点"><el-select class="biz-w-full"><el-option v-for="i in 8" :key="i" :label="`站点-${i}`" :value="i" /></el-select></el-form-item>
        <el-form-item label="类型"><el-select class="biz-w-full"><el-option label="主域名" value="main" /><el-option label="备用域名" value="backup" /></el-select></el-form-item>
        <el-form-item label="SSL"><el-switch /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="addVisible = false">绑定</el-button>
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
import BaseCard from '../../../components/BaseCard.vue'
import BaseModal from '../../../components/BaseModal.vue'
import { siteApi } from '../../../api/business'

const site = ref('')
const type = ref('')
const domain = ref('')
const table = ref<any[]>([])
const addVisible = ref(false)

onMounted(async () => { table.value = await siteApi.domains() })
function onPageChange() {}
function openAdd() { addVisible.value = true }
</script>
