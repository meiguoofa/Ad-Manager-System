import { apiClient, type ApiResponse } from './client'

export interface AuditOperatorOption {
  operatorId: string
  operatorName: string
}

export interface AuditFilterOptionsResponse {
  operators: AuditOperatorOption[]
  actions: string[]
  resourceTypes: string[]
}

export interface AuditFilterOptionsParams {
  operatorKeyword?: string
  from?: string
  to?: string
  limit?: number
}

export async function getAuditFilterOptions(params: AuditFilterOptionsParams = {}) {
  const { data } = await apiClient.get<ApiResponse<AuditFilterOptionsResponse>>('/admin/audit/filter-options', {
    params,
  })
  return data.data
}
