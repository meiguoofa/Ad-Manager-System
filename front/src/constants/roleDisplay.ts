import type { RoleCode, RoleResponse } from '../api/adminUsers'

const FALLBACK_DISPLAY_NAME: Record<RoleCode, string> = {
  PRIMARY_SUPER_ADMIN: '超级管理员',
  SUPER_ADMIN: '超级管理员',
  OPERATOR: '运营人员',
  SALES: '销售人员',
  AUDITOR: '审计人员',
}

export function toRoleDisplayName(roleCode: RoleCode, backendDisplayName?: string) {
  if (roleCode === 'PRIMARY_SUPER_ADMIN') {
    return FALLBACK_DISPLAY_NAME.SUPER_ADMIN
  }
  return backendDisplayName?.trim() || FALLBACK_DISPLAY_NAME[roleCode] || roleCode
}

export function visibleRoleCodesForUI(roles: RoleResponse[]) {
  return roles.filter((role) => role.roleCode !== 'PRIMARY_SUPER_ADMIN')
}

export function topbarRoleDisplay(roles: RoleCode[]) {
  if (!roles.length) return 'N/A'
  if (roles.includes('PRIMARY_SUPER_ADMIN') || roles.includes('SUPER_ADMIN')) {
    return FALLBACK_DISPLAY_NAME.SUPER_ADMIN
  }
  const preferredOrder: RoleCode[] = ['OPERATOR', 'SALES', 'AUDITOR']
  const preferredRole = preferredOrder.find((roleCode) => roles.includes(roleCode))
  return preferredRole ? FALLBACK_DISPLAY_NAME[preferredRole] : FALLBACK_DISPLAY_NAME[roles[0]] || roles[0]
}
