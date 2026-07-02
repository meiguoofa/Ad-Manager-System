import { apiClient, type ApiResponse, type PageResponse } from './client'

export interface AuditLogItem {
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

export interface SystemLogQuery {
  page?: number
  size?: number
  operatorId?: string
  from?: string
  to?: string
}

export async function getOperationLogs(params: SystemLogQuery) {
  const { data } = await apiClient.get<ApiResponse<PageResponse<AuditLogItem>>>('/admin/audit/logs', { params })
  return data.data
}

export async function getLoginLogs(params: SystemLogQuery) {
  const { data } = await apiClient.get<ApiResponse<PageResponse<AuditLogItem>>>('/admin/audit/logs', {
    params: { ...params, action: 'auth.login' },
  })
  return data.data
}
