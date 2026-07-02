<template>
  <div class="biz-page">
    <PageHeader title="操作日志" desc="用户系统操作日志（来自审计日志，已过滤登录类事件）" />

    <FilterBar>
      <el-form-item label="操作人">
        <el-input v-model="filters.operatorId" placeholder="操作人 ID" style="width:180px" clearable @keyup.enter="reloadFromFirstPage" />
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

    <BaseTable v-loading="loading" :data="filteredRows" :show-pagination="false" :total="0">
      <template #actions>
        <el-button @click="loadLogs">刷新</el-button>
      </template>
      <el-table-column label="时间" width="180">
        <template #default="{ row }">{{ formatDateTime(row.occurredAt) }}</template>
      </el-table-column>
      <el-table-column label="操作人" width="160">
        <template #default="{ row }">
          {{ row.operatorName || '—' }}
          <div class="biz-text-muted" style="font-size:12px">{{ row.operatorId || '—' }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="resourceType" label="模块" width="160" />
      <el-table-column prop="action" label="操作" width="200" />
      <el-table-column prop="resourceId" label="对象" width="180" show-overflow-tooltip />
      <el-table-column prop="ip" label="IP" width="150" />
      <el-table-column label="结果" width="110">
        <template #default="{ row }">
          <StatusTag :type="row.result === 'success' ? 'green' : 'red'">{{ row.result }}</StatusTag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button text type="primary" @click="openDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </BaseTable>

    <div class="biz-flex biz-flex-between" style="padding: 16px 24px; background:#fff;">
      <span class="biz-text-muted" style="font-size:13px">
        共 {{ totalElements }} 条审计记录{{ filteredHint }}
      </span>
      <el-pagination
        :current-page="page + 1"
        :page-size="size"
        :total="totalElements"
        layout="prev, pager, next, jumper"
        @current-change="handlePageChange"
      />
    </div>

    <BaseModal v-model="detailVisible" title="操作日志详情" width="640px">
      <div v-if="detail" class="biz-info-grid cols-2">
        <div class="biz-info-item"><span class="biz-info-label">时间</span><span class="biz-info-value">{{ formatDateTime(detail.occurredAt) }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">操作人</span><span class="biz-info-value">{{ detail.operatorName || '—' }} ({{ detail.operatorId || '—' }})</span></div>
        <div class="biz-info-item"><span class="biz-info-label">角色</span><span class="biz-info-value">{{ detail.roleCode || '—' }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">动作</span><span class="biz-info-value">{{ detail.action }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">资源类型</span><span class="biz-info-value">{{ detail.resourceType }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">资源 ID</span><span class="biz-info-value">{{ detail.resourceId || '—' }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">结果</span><span class="biz-info-value"><StatusTag :type="detail.result === 'success' ? 'green' : 'red'">{{ detail.result }}</StatusTag></span></div>
        <div class="biz-info-item"><span class="biz-info-label">IP</span><span class="biz-info-value">{{ detail.ip || '—' }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">Request ID</span><span class="biz-info-value biz-truncate" style="max-width:240px">{{ detail.requestId || '—' }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">User Agent</span><span class="biz-info-value biz-truncate" style="max-width:240px">{{ detail.userAgent || '—' }}</span></div>
      </div>
      <div v-if="detail?.errorMessage" class="biz-mt-16">
        <div class="biz-info-label biz-mb-8">错误信息</div>
        <div class="biz-alert biz-alert-error">{{ detail.errorMessage }}</div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </BaseModal>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '../../../components/PageHeader.vue'
import FilterBar from '../../../components/FilterBar.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'
import BaseModal from '../../../components/BaseModal.vue'
import { resolveUiErrorMessage } from '../../../api/client'
import { getOperationLogs, type AuditLogItem } from '../../../api/systemLogs'

const loading = ref(false)
const rows = ref<AuditLogItem[]>([])
const totalElements = ref(0)
const page = ref(0)
const size = ref(20)
const timeRange = ref<[string, string] | null>(null)
const filters = reactive({ operatorId: '' })
const detailVisible = ref(false)
const detail = ref<AuditLogItem | null>(null)

const filteredRows = computed(() => rows.value.filter((r) => !r.action.startsWith('auth.')))
const filteredHint = computed(() => `，当前页显示 ${filteredRows.value.length} 条非登录类操作`)

async function loadLogs() {
  loading.value = true
  try {
    const data = await getOperationLogs(buildQueryParams())
    rows.value = data.content || []
    totalElements.value = data.totalElements || 0
  } catch (error) {
    ElMessage.error(resolveUiErrorMessage(error, '加载操作日志失败'))
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

function openDetail(row: AuditLogItem) {
  detail.value = row
  detailVisible.value = true
}

function formatDateTime(value?: string) {
  if (!value) return '—'
  return value.replace('T', ' ').slice(0, 19)
}

onMounted(() => { void loadLogs() })
</script>
