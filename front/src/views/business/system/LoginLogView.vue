<template>
  <div class="biz-page">
    <PageHeader title="登录日志" desc="用户登录系统日志（仅登录事件）" />

    <FilterBar>
      <el-form-item label="用户">
        <el-input v-model="filters.operatorId" placeholder="用户 ID" style="width:180px" clearable @keyup.enter="reloadFromFirstPage" />
      </el-form-item>
      <el-form-item label="时间范围">
        <el-date-picker
          v-model="timeRange"
          type="datetimerange"
          value-format="YYYY-MM-DDTHH:mm:ss"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
        />
      </el-form-item>
      <template #actions>
        <el-button @click="resetFilters">重置</el-button>
        <el-button type="primary" @click="reloadFromFirstPage">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable v-loading="loading" :data="rows" :show-pagination="false" :total="0">
      <template #actions>
        <el-button @click="loadLogs">刷新</el-button>
      </template>
      <el-table-column label="时间" width="180">
        <template #default="{ row }">{{ formatDateTime(row.occurredAt) }}</template>
      </el-table-column>
      <el-table-column label="用户" width="160">
        <template #default="{ row }">
          {{ row.operatorName || '—' }}
          <div class="biz-text-muted" style="font-size:12px">{{ row.operatorId || '—' }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="ip" label="IP" width="150" />
      <el-table-column label="登录地点" width="140"><template #default>—</template></el-table-column>
      <el-table-column label="浏览器" width="140"><template #default>—</template></el-table-column>
      <el-table-column label="操作系统" width="140"><template #default>—</template></el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <StatusTag :type="row.result === 'success' ? 'green' : 'red'">{{ row.result }}</StatusTag>
        </template>
      </el-table-column>
      <el-table-column prop="userAgent" label="User Agent" min-width="220" show-overflow-tooltip />
    </BaseTable>

    <div class="biz-flex biz-flex-between" style="padding: 16px 24px; background:#fff;">
      <span class="biz-text-muted" style="font-size:13px">共 {{ totalElements }} 条记录</span>
      <el-pagination
        :current-page="page + 1"
        :page-size="size"
        :total="totalElements"
        layout="prev, pager, next, jumper"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '../../../components/PageHeader.vue'
import FilterBar from '../../../components/FilterBar.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'
import { resolveUiErrorMessage } from '../../../api/client'
import { getLoginLogs, type AuditLogItem } from '../../../api/systemLogs'

const loading = ref(false)
const rows = ref<AuditLogItem[]>([])
const totalElements = ref(0)
const page = ref(0)
const size = ref(20)
const timeRange = ref<[string, string] | null>(null)
const filters = reactive({ operatorId: '' })

async function loadLogs() {
  loading.value = true
  try {
    const data = await getLoginLogs(buildQueryParams())
    rows.value = data.content || []
    totalElements.value = data.totalElements || 0
  } catch (error) {
    ElMessage.error(resolveUiErrorMessage(error, '加载登录日志失败'))
  } finally {
    loading.value = false
  }
}

function buildQueryParams() {
  const params: Record<string, string | number> = { page: page.value, size: size.value }
  const operatorId = filters.operatorId.trim()
  if (operatorId) params.operatorId = operatorId
  if (timeRange.value && timeRange.value.length === 2) {
    params.from = timeRange.value[0]
    params.to = timeRange.value[1]
  }
  return params
}

function handlePageChange(currentPage: number) {
  page.value = currentPage - 1
  void loadLogs()
}

async function reloadFromFirstPage() {
  page.value = 0
  await loadLogs()
}

async function resetFilters() {
  filters.operatorId = ''
  timeRange.value = null
  await reloadFromFirstPage()
}

function formatDateTime(value?: string) {
  if (!value) return '—'
  return value.replace('T', ' ').slice(0, 19)
}

onMounted(() => { void loadLogs() })
</script>
