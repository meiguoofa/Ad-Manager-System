<template>
  <section class="panel page-panel">
    <div class="section-heading">
      <p>Audit / Ops Trace</p>
      <h2>审计与操作日志</h2>
    </div>

    <div class="action-bar audit-action-bar">
      <div class="filters">
        <el-select
          v-model="filters.operatorId"
          class="filter-select"
          filterable
          remote
          clearable
          reserve-keyword
          placeholder="操作人"
          :remote-method="handleOperatorSearch"
          :loading="operatorOptionsLoading"
          @visible-change="handleOperatorDropdownVisibleChange"
        >
          <el-option
            v-for="option in operatorOptions"
            :key="option.operatorId"
            :label="operatorOptionLabel(option)"
            :value="option.operatorId"
          />
        </el-select>

        <el-select v-model="filters.action" class="filter-select" clearable filterable placeholder="动作" :loading="filterOptionsLoading">
          <el-option v-for="action in actionOptions" :key="action" :label="action" :value="action" />
        </el-select>

        <el-select
          v-model="filters.resourceType"
          class="filter-select"
          clearable
          filterable
          placeholder="资源类型"
          :loading="filterOptionsLoading"
        >
          <el-option v-for="resourceType in resourceTypeOptions" :key="resourceType" :label="resourceType" :value="resourceType" />
        </el-select>

        <el-input
          v-model="filters.resourceId"
          class="filter-input"
          clearable
          placeholder="资源 ID"
          @keyup.enter="reloadFromFirstPage"
        />

        <el-date-picker
          v-model="timeRange"
          type="datetimerange"
          value-format="YYYY-MM-DDTHH:mm:ss"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
        />

        <div class="filter-actions">
          <el-button type="primary" @click="reloadFromFirstPage">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </div>
      </div>
    </div>

    <el-table v-loading="loading" :data="rows" border stripe>
      <el-table-column prop="occurredAt" label="时间" width="180">
        <template #default="{ row }">{{ formatDateTime(row.occurredAt) }}</template>
      </el-table-column>
      <el-table-column label="操作人" width="170">
        <template #default="{ row }">
          {{ row.operatorName || '-' }}
          <br />
          <small class="muted">{{ row.operatorId || '-' }}</small>
        </template>
      </el-table-column>
      <el-table-column prop="roleCode" label="角色" width="170" />
      <el-table-column prop="action" label="动作" width="220" />
      <el-table-column prop="resourceType" label="资源类型" width="160" />
      <el-table-column prop="resourceId" label="资源 ID" width="220" show-overflow-tooltip />
      <el-table-column label="结果" width="120">
        <template #default="{ row }">
          <el-tag :type="row.result === 'success' ? 'success' : 'danger'">{{ row.result }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="ip" label="IP" width="150" />
      <el-table-column prop="requestId" label="Request ID" width="210" show-overflow-tooltip />
      <el-table-column prop="userAgent" label="User Agent" min-width="200" show-overflow-tooltip />
      <el-table-column prop="errorMessage" label="错误信息" min-width="220" show-overflow-tooltip />
    </el-table>

    <div class="pagination-wrap">
      <el-pagination
        :current-page="page + 1"
        :page-size="size"
        :total="totalElements"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { apiClient, resolveUiErrorMessage, type ApiResponse, type PageResponse } from '../api/client'
import {
  getAuditFilterOptions,
  type AuditFilterOptionsParams,
  type AuditOperatorOption,
} from '../api/audit'

interface AuditLogItem {
  id: string
  operatorId?: string
  operatorName?: string
  roleCode?: string
  action: string
  resourceType: string
  resourceId?: string
  result: string
  ip?: string
  userAgent?: string
  requestId?: string
  errorMessage?: string
  occurredAt: string
}

const loading = ref(false)
const rows = ref<AuditLogItem[]>([])
const totalElements = ref(0)
const page = ref(0)
const size = ref(20)
const timeRange = ref<[string, string] | null>(null)

const filterOptionsLoading = ref(false)
const operatorOptionsLoading = ref(false)
const operatorOptions = ref<AuditOperatorOption[]>([])
const actionOptions = ref<string[]>([])
const resourceTypeOptions = ref<string[]>([])

const filters = reactive({
  operatorId: '',
  action: '',
  resourceType: '',
  resourceId: '',
})

async function loadLogs() {
  const params = buildQueryParams()
  loading.value = true
  try {
    const { data } = await apiClient.get<ApiResponse<PageResponse<AuditLogItem>>>('/admin/audit/logs', {
      params,
    })
    rows.value = data.data.content || []
    totalElements.value = data.data.totalElements || 0
  } catch (error) {
    ElMessage.error(resolveUiErrorMessage(error, '加载审计日志失败'))
  } finally {
    loading.value = false
  }
}

function buildQueryParams(): Record<string, string | number> {
  const params: Record<string, string | number> = {
    page: page.value,
    size: size.value,
  }
  const operatorId = filters.operatorId.trim()
  const action = filters.action.trim()
  const resourceType = filters.resourceType.trim()
  const resourceId = filters.resourceId.trim()

  if (operatorId) params.operatorId = operatorId
  if (action) params.action = action
  if (resourceType) params.resourceType = resourceType
  if (resourceId) params.resourceId = resourceId

  if (timeRange.value && timeRange.value.length === 2) {
    params.from = timeRange.value[0]
    params.to = timeRange.value[1]
  }

  return params
}

function buildFilterOptionParams(operatorKeyword = ''): AuditFilterOptionsParams {
  const params: AuditFilterOptionsParams = { limit: 100 }
  const keyword = operatorKeyword.trim()
  if (keyword) {
    params.operatorKeyword = keyword
  }
  if (timeRange.value && timeRange.value.length === 2) {
    params.from = timeRange.value[0]
    params.to = timeRange.value[1]
  }
  return params
}

async function reloadFilterOptions() {
  filterOptionsLoading.value = true
  try {
    const data = await getAuditFilterOptions(buildFilterOptionParams())
    operatorOptions.value = data.operators || []
    actionOptions.value = data.actions || []
    resourceTypeOptions.value = data.resourceTypes || []
  } catch (error) {
    ElMessage.error(resolveUiErrorMessage(error, '加载审计筛选选项失败'))
  } finally {
    filterOptionsLoading.value = false
  }
}

async function handleOperatorSearch(keyword: string) {
  operatorOptionsLoading.value = true
  try {
    const data = await getAuditFilterOptions(buildFilterOptionParams(keyword))
    operatorOptions.value = data.operators || []
    if (!keyword.trim()) {
      actionOptions.value = data.actions || []
      resourceTypeOptions.value = data.resourceTypes || []
    }
  } catch (error) {
    ElMessage.error(resolveUiErrorMessage(error, '加载操作人选项失败'))
  } finally {
    operatorOptionsLoading.value = false
  }
}

function handleOperatorDropdownVisibleChange(visible: boolean) {
  if (visible && operatorOptions.value.length === 0) {
    void handleOperatorSearch('')
  }
}

function operatorOptionLabel(option: AuditOperatorOption) {
  const name = option.operatorName?.trim()
  if (!name || name === option.operatorId) {
    return option.operatorId
  }
  return `${name} (${option.operatorId})`
}

async function resetFilters() {
  filters.operatorId = ''
  filters.action = ''
  filters.resourceType = ''
  filters.resourceId = ''
  timeRange.value = null
  await reloadFromFirstPage()
}

function handlePageChange(currentPage: number) {
  page.value = currentPage - 1
  void loadLogs()
}

async function reloadFromFirstPage() {
  page.value = 0
  await Promise.all([loadLogs(), reloadFilterOptions()])
}

function formatDateTime(value?: string) {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 19)
}

onMounted(() => {
  void Promise.all([loadLogs(), reloadFilterOptions()])
})
</script>

<style scoped>
.audit-action-bar {
  margin-bottom: 16px;
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
}

.filter-select,
.filter-input {
  width: 190px;
}

.filter-actions {
  display: flex;
  gap: 8px;
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.muted {
  color: #909399;
}
</style>
