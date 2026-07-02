<template>
  <div class="biz-page">
    <PageHeader title="短剧管理" desc="短剧列表管理" />

    <FilterBar>
      <el-form-item label="分类"><el-select v-model="category" placeholder="全部" style="width:140px"><el-option v-for="c in cats" :key="c" :label="c" :value="c" /></el-select></el-form-item>
      <el-form-item label="状态"><el-select v-model="status" placeholder="全部" style="width:140px"><el-option label="连载中" value="serial" /><el-option label="已完结" value="done" /></el-select></el-form-item>
      <el-form-item label="名称"><el-input v-model="name" placeholder="请输入名称" style="width:160px" /></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="86" :page="1" :page-size="10" @change="onPageChange">
      <template #actions>
        <el-button>导出</el-button>
        <el-button type="primary" @click="openAdd">新建短剧</el-button>
      </template>
      <el-table-column prop="drama" label="编号" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="category" label="分类" />
      <el-table-column prop="episodes" label="集数" />
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '连载中' ? 'blue' : row.status === '已完结' ? 'green' : 'orange'">{{ row.status }}</StatusTag></template></el-table-column>
      <el-table-column prop="playCount" label="播放量" />
      <el-table-column prop="recharge" label="充值" />
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" width="180"><template #default="{ row }"><el-button text type="primary" @click="goDetail(row)">详情</el-button><el-button text type="primary">编辑</el-button><el-button text type="danger">删除</el-button></template></el-table-column>
    </BaseTable>

    <BaseModal v-model="addVisible" title="新建短剧" width="640px">
      <el-form label-position="top">
        <el-form-item label="短剧名称"><el-input /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="分类"><el-select class="biz-w-full"><el-option v-for="c in cats" :key="c" :label="c" :value="c" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="集数"><el-input-number :min="1" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="简介"><el-input type="textarea" /></el-form-item>
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
import StatusTag from '../../../components/StatusTag.vue'
import BaseModal from '../../../components/BaseModal.vue'
import { dramaApi } from '../../../api/business'

const router = useRouter()
const cats = ['都市', '玄幻', '甜宠', '复仇', '穿越']
const category = ref('')
const status = ref('')
const name = ref('')
const table = ref<any[]>([])
const addVisible = ref(false)

onMounted(async () => { table.value = await dramaApi.management() })
function onPageChange() {}
function openAdd() { addVisible.value = true }
function goDetail(row: any) { router.push(`/drama/detail/${row.id}`) }
</script>
