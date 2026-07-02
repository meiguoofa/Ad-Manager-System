<template>
  <div class="biz-page">
    <PageHeader title="落地页列表" desc="管理站点的落地页和推广页" />

    <FilterBar>
      <el-form-item label="站点"><el-select v-model="site" placeholder="全部" style="width:160px"><el-option v-for="i in 8" :key="i" :label="`站点-${i}`" :value="i" /></el-select></el-form-item>
      <el-form-item label="状态"><el-select v-model="status" placeholder="全部" style="width:140px"><el-option label="已发布" value="published" /><el-option label="草稿" value="draft" /></el-select></el-form-item>
      <el-form-item label="名称"><el-input v-model="name" placeholder="请输入名称" style="width:160px" /></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="86" :page="1" :page-size="10" @change="onPageChange">
      <template #actions>
        <el-button>导出</el-button>
        <el-button type="primary" @click="$router.push('/site/page-editor')">新建落地页</el-button>
      </template>
      <el-table-column prop="name" label="页面名" />
      <el-table-column prop="site" label="所属站点" />
      <el-table-column prop="url" label="URL" show-overflow-tooltip />
      <el-table-column prop="visits" label="访问数" />
      <el-table-column prop="conv" label="转化率" />
      <el-table-column prop="recharge" label="充值" />
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '已发布' ? 'green' : row.status === '草稿' ? 'orange' : 'default'">{{ row.status }}</StatusTag></template></el-table-column>
      <el-table-column prop="updateTime" label="更新时间" />
      <el-table-column label="操作" width="180"><template #default="{ row }"><el-button text type="primary" @click="goEditor(row)">编辑</el-button><el-button text type="primary">预览</el-button><el-button text type="danger">删除</el-button></template></el-table-column>
    </BaseTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '../../../components/PageHeader.vue'
import FilterBar from '../../../components/FilterBar.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'
import { siteApi } from '../../../api/business'

const router = useRouter()
const site = ref('')
const status = ref('')
const name = ref('')
const table = ref<any[]>([])

onMounted(async () => { table.value = await siteApi.pages() })
function onPageChange() {}
function goEditor(row: any) { router.push(`/site/page-editor/${row.id}`) }
</script>
