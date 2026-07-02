<template>
  <div class="biz-page">
    <PageHeader title="每日消耗" desc="查看每日广告消耗数据，支持多维度分析" />
    <BaseTabs v-model="tab" :tabs="[{ key: 'language', label: '语种维度' }, { key: 'hourly', label: '分时维度' }, { key: 'adGroup', label: '广告组维度' }]" />

    <div class="biz-stats-grid">
      <StatCard label="总消耗" value="¥456,789.50" :diff="12.5" diff-label="较昨日" icon="💰" icon-color="blue" />
      <StatCard label="曝光量" value="258.6万" :diff="8.3" diff-label="较昨日" icon="👁️" icon-color="purple" />
      <StatCard label="点击量" value="36,520" :diff="15.2" diff-label="较昨日" icon="👆" icon-color="cyan" />
      <StatCard label="转化数" value="2,856" :diff="6.8" diff-label="较昨日" icon="✅" icon-color="green" />
    </div>

    <FilterBar>
      <el-form-item label="投放链路"><el-select v-model="filter.link" placeholder="全部" style="width:140px"><el-option label="W2A" value="w2a" /></el-select></el-form-item>
      <el-form-item label="媒体"><el-select v-model="filter.media" placeholder="全部" style="width:140px"><el-option label="Meta" value="meta" /><el-option label="TikTok" value="tiktok" /></el-select></el-form-item>
      <el-form-item label="广告状态"><el-select v-model="filter.status" placeholder="全部" style="width:140px"><el-option label="暂停" value="paused" /><el-option label="投放中" value="running" /><el-option label="为止" value="unknown" /></el-select></el-form-item>
      <el-form-item label="业务类型"><el-select v-model="filter.biz" placeholder="全部" style="width:140px"><el-option label="IAP" value="iap" /><el-option label="IAA" value="iaa" /></el-select></el-form-item>
      <el-form-item label="渠道号"><el-input v-model="filter.channel" placeholder="请输入渠道号" style="width:160px" /></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable :data="table" :total="86" :page="1" :page-size="10" @change="onPageChange">
      <template #actions>
        <el-button>导出</el-button>
        <el-button type="primary" @click="openAdd">新增</el-button>
      </template>
      <el-table-column prop="date" label="日期" />
      <el-table-column label="渠道"><template #default="{ row }"><StatusTag type="blue">{{ row.channel }}</StatusTag></template></el-table-column>
      <el-table-column prop="adGroup" label="广告组" />
      <el-table-column label="消耗"><template #default="{ row }"><span class="biz-text-bold">{{ row.cost }}</span></template></el-table-column>
      <el-table-column prop="impressions" label="曝光量" />
      <el-table-column prop="clicks" label="点击量" />
      <el-table-column prop="conversions" label="转化数" />
      <el-table-column label="ROI"><template #default="{ row }"><span :class="parseFloat(row.roi) >= 2.5 ? 'biz-text-success' : parseFloat(row.roi) >= 2 ? 'biz-text-warning' : 'biz-text-error'">{{ row.roi }}</span></template></el-table-column>
      <el-table-column label="状态"><template #default="{ row }"><span class="biz-dot-status" :class="row.status === '运行中' ? 'success' : row.status === '已暂停' ? 'warning' : 'default'">{{ row.status }}</span></template></el-table-column>
      <el-table-column label="操作" width="140"><template #default="{ row }"><el-button text type="primary" @click.stop="goAd">查看</el-button><el-button text type="primary" @click.stop="openEdit(row)">编辑</el-button></template></el-table-column>
    </BaseTable>

    <BaseModal v-model="editVisible" :title="editForm.id ? '编辑消耗记录' : '新增消耗记录'" width="560px">
      <el-form label-position="top" :model="editForm">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="日期"><el-input v-model="editForm.date" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="渠道"><el-select v-model="editForm.channel" class="biz-w-full"><el-option v-for="c in channels" :key="c" :label="c" :value="c" /></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="广告组"><el-input v-model="editForm.adGroup" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="消耗"><el-input v-model="editForm.cost" /></el-form-item></el-col>
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
import { useRouter } from 'vue-router'
import PageHeader from '../../../components/PageHeader.vue'
import BaseTabs from '../../../components/BaseTabs.vue'
import StatCard from '../../../components/StatCard.vue'
import FilterBar from '../../../components/FilterBar.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'
import BaseModal from '../../../components/BaseModal.vue'
import { deliveryApi } from '../../../api/business'

const router = useRouter()
const tab = ref('language')
const table = ref<any[]>([])
const channels = ['TikTok', 'Facebook', 'Google', 'Twitter']
const filter = ref({ link: '', media: '', status: '', biz: '', channel: '' })
const editVisible = ref(false)
const editForm = ref<any>({ id: 0, date: '', channel: '', adGroup: '', cost: '' })

onMounted(async () => { table.value = await deliveryApi.dailyCost() })
function onPageChange() {}
function goAd() { router.push('/delivery/ad-dashboard') }
function openAdd() { editForm.value = { id: 0, date: '', channel: '', adGroup: '', cost: '' }; editVisible.value = true }
function openEdit(row: any) { editForm.value = { ...row }; editVisible.value = true }
</script>
