import { apiClient, type ApiResponse, type PageResponse } from './client'

export type AdminUserStatus = 'pending_init' | 'active' | 'suspended' | 'disabled'
export type RoleCode = 'PRIMARY_SUPER_ADMIN' | 'SUPER_ADMIN' | 'OPERATOR' | 'SALES' | 'AUDITOR'

export interface AdminUserListItem {
  id: string
  username: string
  displayName: string
  email?: string | null
  status: AdminUserStatus
  lastLoginAt?: string | null
  roles: RoleCode[]
}

export interface RoleResponse {
  roleCode: RoleCode
  displayName: string
  description?: string
}

export interface AdminUserCreateRequest {
  username: string
  password: string
  displayName: string
  email?: string
}

export interface ListAdminUsersParams {
  page: number
  size: number
  keyword?: string
  status?: AdminUserStatus | ''
}

export async function listAdminUsers(params: ListAdminUsersParams) {
  const { data } = await apiClient.get<ApiResponse<PageResponse<AdminUserListItem>>>('/admin/admin-users', {
    params,
  })
  return data.data
}

export async function createAdminUser(payload: AdminUserCreateRequest) {
  const { data } = await apiClient.post<ApiResponse<AdminUserListItem>>('/admin/admin-users', payload)
  return data.data
}

export async function updateAdminUserStatus(id: string, status: AdminUserStatus) {
  const { data } = await apiClient.post<ApiResponse<AdminUserListItem>>(`/admin/admin-users/${id}/status`, {
    status,
  })
  return data.data
}

export async function grantRole(id: string, roleCode: RoleCode) {
  await apiClient.post<ApiResponse<void>>(`/admin/admin-users/${id}/roles/grant`, { roleCode })
}

export async function revokeRole(id: string, roleCode: RoleCode) {
  await apiClient.post<ApiResponse<void>>(`/admin/admin-users/${id}/roles/revoke`, { roleCode })
}

export async function resetAdminUserPassword(id: string, newPassword: string) {
  await apiClient.post<ApiResponse<void>>(`/admin/admin-users/${id}/password/reset`, { newPassword })
}

export async function listRoles() {
  const { data } = await apiClient.get<ApiResponse<RoleResponse[]>>('/admin/roles')
  return data.data
}
