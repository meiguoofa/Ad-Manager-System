import { apiClient, type ApiResponse } from './client'

export interface ChangePasswordRequest {
  oldPassword: string
  newPassword: string
}

export async function changePassword(payload: ChangePasswordRequest) {
  await apiClient.post<ApiResponse<void>>('/admin/auth/password/change', payload)
}
