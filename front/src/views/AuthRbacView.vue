<template>
  <section class="panel page-panel">
    <div class="section-heading">
      <p>Auth / RBAC</p>
      <h2>后台认证与权限</h2>
    </div>

    <div class="action-bar auth-action-bar">
      <div class="filters">
        <el-input
          v-model="keyword"
          class="filter-input"
          clearable
          placeholder="搜索账号/姓名"
          @keyup.enter="reloadFromFirstPage"
        />
        <el-select v-model="statusFilter" class="filter-select" clearable placeholder="全部状态">
          <el-option
            v-for="status in statusOptions"
            :key="status"
            :label="statusLabel(status)"
            :value="status"
          />
        </el-select>
        <div class="filter-actions">
          <el-button type="primary" @click="reloadFromFirstPage">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </div>
      </div>
      <div class="action-buttons">
        <el-button type="primary" :disabled="!canManage" @click="openCreateDrawer">创建管理员</el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="rows" border stripe>
      <el-table-column prop="username" label="账号" width="180" />
      <el-table-column prop="displayName" label="姓名" width="180" />
      <el-table-column prop="email" label="邮箱" min-width="220" />
      <el-table-column label="状态" width="140">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="角色" min-width="300">
        <template #default="{ row }">
          <el-space wrap>
            <el-tag
              v-for="role in displayRolesForUser(row)"
              :key="`${row.id}-${role}`"
              :type="roleTagType(role)"
            >
              {{ roleLabel(role) }}
            </el-tag>
            <span v-if="!displayRolesForUser(row).length" class="empty-text">未授权</span>
          </el-space>
        </template>
      </el-table-column>
      <el-table-column label="最近登录" width="200">
        <template #default="{ row }">
          {{ formatDateTime(row.lastLoginAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="360" fixed="right">
        <template #default="{ row }">
          <el-space>
            <el-tooltip :disabled="!statusActionDisabledReason(row)" :content="statusActionDisabledReason(row)">
              <span>
                <el-button
                  size="small"
                  :disabled="Boolean(statusActionDisabledReason(row))"
                  @click="openStatusDrawer(row)"
                >
                  状态
                </el-button>
              </span>
            </el-tooltip>
            <el-tooltip :disabled="!roleActionDisabledReason(row)" :content="roleActionDisabledReason(row)">
              <span>
                <el-button
                  size="small"
                  type="primary"
                  plain
                  :disabled="Boolean(roleActionDisabledReason(row))"
                  @click="openRolesDrawer(row)"
                >
                  角色
                </el-button>
              </span>
            </el-tooltip>
            <el-tooltip :disabled="!resetPasswordActionDisabledReason(row)" :content="resetPasswordActionDisabledReason(row)">
              <span>
                <el-button
                  size="small"
                  type="warning"
                  plain
                  :disabled="Boolean(resetPasswordActionDisabledReason(row))"
                  @click="openResetPasswordDrawer(row)"
                >
                  重置密码
                </el-button>
              </span>
            </el-tooltip>
          </el-space>
        </template>
      </el-table-column>
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

    <el-drawer v-model="createDrawerVisible" title="创建管理员" size="420px" destroy-on-close>
      <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-position="top">
        <el-form-item label="账号" prop="username">
          <el-input
            v-model="createForm.username"
            maxlength="64"
            autocomplete="off"
            name="admin-create-username"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="createForm.password"
            type="password"
            show-password
            maxlength="128"
            autocomplete="new-password"
            name="admin-create-password"
          />
        </el-form-item>
        <el-form-item label="姓名" prop="displayName">
          <el-input v-model="createForm.displayName" maxlength="100" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="createForm.email" maxlength="255" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="drawer-footer">
          <el-button @click="createDrawerVisible = false">取消</el-button>
          <el-button type="primary" :loading="creating" @click="submitCreate">创建</el-button>
        </div>
      </template>
    </el-drawer>

    <el-drawer v-model="statusDrawerVisible" title="修改账号状态" size="420px" destroy-on-close>
      <div v-if="selectedUser" class="drawer-content">
        <div class="status-summary">
          <p><span>账号：</span>{{ selectedUser.username }}</p>
          <p><span>当前状态：</span>{{ statusLabel(selectedUser.status) }}</p>
        </div>
        <el-form label-position="top">
          <el-form-item label="目标状态">
            <el-select v-model="targetStatus" class="full-width" placeholder="请选择目标状态">
              <el-option
                v-for="status in statusTransitionOptions(selectedUser)"
                :key="status"
                :label="statusLabel(status)"
                :value="status"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="drawer-footer">
          <el-button @click="statusDrawerVisible = false">取消</el-button>
          <el-button type="primary" :loading="updatingStatus" @click="submitStatus">确认变更</el-button>
        </div>
      </template>
    </el-drawer>

    <el-drawer v-model="rolesDrawerVisible" title="角色管理" size="460px" destroy-on-close>
      <div v-if="selectedUser" class="drawer-content">
        <div class="status-summary">
          <p><span>账号：</span>{{ selectedUser.username }}</p>
          <p><span>姓名：</span>{{ selectedUser.displayName }}</p>
        </div>
        <el-form label-position="top">
          <el-form-item label="角色分配">
            <el-checkbox-group v-model="selectedRoleCodes" class="role-list">
              <el-checkbox
                v-for="role in editableRoleOptions"
                :key="role.roleCode"
                :label="role.roleCode"
                :disabled="isRoleOptionDisabled(role.roleCode)"
                @change="handleRoleCheckboxToggle(role.roleCode, $event as boolean)"
              >
                {{ toRoleDisplayName(role.roleCode, role.displayName) }}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="drawer-footer">
          <el-button @click="rolesDrawerVisible = false">取消</el-button>
          <el-button type="primary" :loading="updatingRoles" @click="submitRoles">保存角色</el-button>
        </div>
      </template>
    </el-drawer>

    <el-drawer v-model="resetPasswordDrawerVisible" title="重置密码" size="420px" destroy-on-close>
      <div v-if="selectedUser" class="drawer-content">
        <div class="status-summary">
          <p><span>账号：</span>{{ selectedUser.username }}</p>
          <p><span>姓名：</span>{{ selectedUser.displayName }}</p>
        </div>
        <el-form ref="resetPasswordFormRef" :model="resetPasswordForm" :rules="resetPasswordRules" label-position="top">
          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="resetPasswordForm.newPassword"
              type="password"
              show-password
              maxlength="128"
              autocomplete="new-password"
            />
          </el-form-item>
          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input
              v-model="resetPasswordForm.confirmPassword"
              type="password"
              show-password
              maxlength="128"
              autocomplete="new-password"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="drawer-footer">
          <el-button @click="resetPasswordDrawerVisible = false">取消</el-button>
          <el-button type="primary" :loading="resettingPassword" @click="submitResetPassword">确认重置</el-button>
        </div>
      </template>
    </el-drawer>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { resolveUiErrorMessage } from '../api/client'
import {
  createAdminUser,
  grantRole,
  listAdminUsers,
  listRoles,
  resetAdminUserPassword,
  revokeRole,
  type AdminUserCreateRequest,
  type AdminUserListItem,
  type AdminUserStatus,
  type RoleCode,
  type RoleResponse,
  updateAdminUserStatus,
} from '../api/adminUsers'
import { toRoleDisplayName, visibleRoleCodesForUI } from '../constants/roleDisplay'
import { useAdminSessionStore } from '../stores/adminSession'

const session = useAdminSessionStore()
const canManage = computed(() => session.hasAnyRole(['PRIMARY_SUPER_ADMIN', 'SUPER_ADMIN']))
const isPrimarySuperAdmin = computed(() => session.hasAnyRole(['PRIMARY_SUPER_ADMIN']))

const loading = ref(false)
const rows = ref<AdminUserListItem[]>([])
const totalElements = ref(0)
const page = ref(0)
const size = ref(20)
const keyword = ref('')
const statusFilter = ref<AdminUserStatus | ''>('')

const roleOptions = ref<RoleResponse[]>([])
const statusOptions: AdminUserStatus[] = ['pending_init', 'active', 'suspended', 'disabled']

const createDrawerVisible = ref(false)
const statusDrawerVisible = ref(false)
const rolesDrawerVisible = ref(false)
const resetPasswordDrawerVisible = ref(false)
const creating = ref(false)
const updatingStatus = ref(false)
const updatingRoles = ref(false)
const resettingPassword = ref(false)

const selectedUser = ref<AdminUserListItem | null>(null)
const targetStatus = ref<AdminUserStatus | ''>('')
const selectedRoleCodes = ref<RoleCode[]>([])
const originalRoleCodes = ref<RoleCode[]>([])

const createFormRef = ref<FormInstance>()
const resetPasswordFormRef = ref<FormInstance>()
const createForm = reactive<AdminUserCreateRequest>({
  username: '',
  password: '',
  displayName: '',
  email: '',
})
const resetPasswordForm = reactive({
  newPassword: '',
  confirmPassword: '',
})

const createRules: FormRules<AdminUserCreateRequest> = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, message: '密码至少 8 位', trigger: 'blur' },
  ],
  displayName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
}

const resetPasswordRules: FormRules<typeof resetPasswordForm> = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, message: '密码至少 8 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (!value) {
          callback(new Error('请确认新密码'))
          return
        }
        if (value !== resetPasswordForm.newPassword) {
          callback(new Error('两次输入的新密码不一致'))
          return
        }
        callback()
      },
      trigger: 'blur',
    },
  ],
}

const roleDisplayNameMap = computed(() => {
  const map: Partial<Record<RoleCode, string>> = {}
  for (const role of roleOptions.value) {
    map[role.roleCode] = role.displayName
  }
  return map
})

const visibleRoleOptions = computed(() => visibleRoleCodesForUI(roleOptions.value))

const editableRoleOptions = computed(() => {
  if (isPrimarySuperAdmin.value) {
    return visibleRoleOptions.value
  }
  return visibleRoleOptions.value.filter((role) => !isSuperRelationRole(role.roleCode))
})

function isSelfRow(user: AdminUserListItem) {
  return session.profile?.id === user.id
}

function isSuperRelationRole(roleCode: RoleCode) {
  return roleCode === 'PRIMARY_SUPER_ADMIN' || roleCode === 'SUPER_ADMIN'
}

function isBusinessRole(roleCode: RoleCode) {
  return roleCode === 'OPERATOR' || roleCode === 'SALES' || roleCode === 'AUDITOR'
}

function rowHasSuperRelation(user: AdminUserListItem) {
  return user.roles?.some((role) => isSuperRelationRole(role)) ?? false
}

function rowHasPrimarySuperAdmin(user: AdminUserListItem) {
  return user.roles?.includes('PRIMARY_SUPER_ADMIN') ?? false
}

function isRoleVisibleInUI(roleCode: RoleCode) {
  return roleCode !== 'PRIMARY_SUPER_ADMIN'
}

function normalizeRoleForDisplay(roleCode: RoleCode): RoleCode {
  if (roleCode === 'PRIMARY_SUPER_ADMIN') {
    return 'SUPER_ADMIN'
  }
  return roleCode
}

function displayRolesForUser(user: AdminUserListItem) {
  const normalizedRoles = (user.roles || []).map((role) => normalizeRoleForDisplay(role))
  const uniqueRoles: RoleCode[] = []
  for (const role of normalizedRoles) {
    if (!uniqueRoles.includes(role)) {
      uniqueRoles.push(role)
    }
  }
  return uniqueRoles
}

function roleLabel(role: RoleCode) {
  return toRoleDisplayName(role, roleDisplayNameMap.value[role])
}

function roleTagType(role: RoleCode) {
  if (role === 'PRIMARY_SUPER_ADMIN') return 'danger'
  if (role === 'SUPER_ADMIN') return 'warning'
  return 'info'
}

function statusLabel(status: AdminUserStatus) {
  if (status === 'pending_init') return '待初始化'
  if (status === 'active') return '启用'
  if (status === 'suspended') return '冻结'
  if (status === 'disabled') return '禁用'
  return status
}

function statusTagType(status: AdminUserStatus) {
  if (status === 'active') return 'success'
  if (status === 'pending_init') return 'warning'
  if (status === 'suspended') return 'danger'
  if (status === 'disabled') return 'info'
  return ''
}

function statusTransitionOptions(user: AdminUserListItem): AdminUserStatus[] {
  if (user.status === 'pending_init') return ['active']
  if (user.status === 'active') {
    const base: AdminUserStatus[] = ['suspended', 'disabled']
    if (rowHasPrimarySuperAdmin(user)) {
      return []
    }
    return base
  }
  if (user.status === 'suspended') {
    const base: AdminUserStatus[] = ['active', 'disabled']
    if (rowHasPrimarySuperAdmin(user)) {
      return ['active']
    }
    return base
  }
  return []
}

function statusActionDisabledReason(user: AdminUserListItem) {
  if (!canManage.value) return '无权限访问该操作'
  if (isSelfRow(user)) return '不可修改自身状态'
  if (rowHasSuperRelation(user) && !isPrimarySuperAdmin.value) return '仅最高权限管理员可操作超管账号'
  return ''
}

function roleActionDisabledReason(user: AdminUserListItem) {
  if (!canManage.value) return '无权限访问该操作'
  if (isSelfRow(user)) return '不可修改自身角色'
  if (rowHasSuperRelation(user) && !isPrimarySuperAdmin.value) return '仅最高权限管理员可操作超管关系'
  return ''
}

function resetPasswordActionDisabledReason(user: AdminUserListItem) {
  if (!canManage.value) return '无权限访问该操作'
  if (isSelfRow(user)) return '请使用右上角“修改密码”'
  if (rowHasSuperRelation(user) && !isPrimarySuperAdmin.value) return '仅最高权限管理员可重置超管关系账号密码'
  return ''
}

function isRoleOptionDisabled(roleCode: RoleCode) {
  if (!selectedUser.value) return true
  if (rowHasPrimarySuperAdmin(selectedUser.value) && isBusinessRole(roleCode)) return true
  if (isSuperRelationRole(roleCode) && !isPrimarySuperAdmin.value) return true
  if (roleCode === 'PRIMARY_SUPER_ADMIN' && rowHasPrimarySuperAdmin(selectedUser.value)) return true
  return false
}

function handleRoleCheckboxToggle(roleCode: RoleCode, checked: boolean | string | number) {
  if (!checked) return

  if (roleCode === 'SUPER_ADMIN') {
    const hasBusinessSelected = selectedRoleCodes.value.some((code) => isBusinessRole(code))
    if (hasBusinessSelected) {
      selectedRoleCodes.value = selectedRoleCodes.value.filter((code) => !isBusinessRole(code))
      return
    }
  }

  if (isBusinessRole(roleCode) && selectedRoleCodes.value.includes('SUPER_ADMIN')) {
    selectedRoleCodes.value = selectedRoleCodes.value.filter((code) => code !== 'SUPER_ADMIN')
  }
}

function formatDateTime(value?: string | null) {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 19)
}

async function loadUsers() {
  loading.value = true
  try {
    const payload = await listAdminUsers({
      page: page.value,
      size: size.value,
      keyword: keyword.value.trim() || undefined,
      status: statusFilter.value || undefined,
    })
    rows.value = payload.content || []
    totalElements.value = payload.totalElements || 0
  } catch (error) {
    ElMessage.error(resolveUiErrorMessage(error, '加载管理员列表失败'))
  } finally {
    loading.value = false
  }
}

async function loadRoles() {
  try {
    roleOptions.value = await listRoles()
  } catch (error) {
    ElMessage.error(resolveUiErrorMessage(error, '加载角色字典失败'))
  }
}

async function reloadFromFirstPage() {
  page.value = 0
  await loadUsers()
}

function resetFilters() {
  keyword.value = ''
  statusFilter.value = ''
  reloadFromFirstPage()
}

function handlePageChange(currentPage: number) {
  page.value = currentPage - 1
  loadUsers()
}

function openCreateDrawer() {
  createForm.username = ''
  createForm.password = ''
  createForm.displayName = ''
  createForm.email = ''
  createDrawerVisible.value = true
}

async function submitCreate() {
  if (creating.value) return
  if (!createFormRef.value) return
  const valid = await createFormRef.value.validate().catch(() => false)
  if (!valid) return

  creating.value = true
  try {
    await createAdminUser({
      username: createForm.username.trim(),
      password: createForm.password,
      displayName: createForm.displayName.trim(),
      email: createForm.email?.trim() || undefined,
    })
    ElMessage.success('管理员创建成功')
    createDrawerVisible.value = false
    await reloadFromFirstPage()
  } catch (error) {
    ElMessage.error(resolveUiErrorMessage(error, '创建管理员失败'))
  } finally {
    creating.value = false
  }
}

function openStatusDrawer(user: AdminUserListItem) {
  const disabledReason = statusActionDisabledReason(user)
  if (disabledReason) {
    ElMessage.warning(disabledReason)
    return
  }
  selectedUser.value = user
  const options = statusTransitionOptions(user)
  if (options.length === 0) {
    ElMessage.warning('当前状态不可继续流转')
    return
  }
  targetStatus.value = options[0] || ''
  statusDrawerVisible.value = true
}

async function submitStatus() {
  if (updatingStatus.value) return
  if (!selectedUser.value || !targetStatus.value) {
    ElMessage.warning('请选择目标状态')
    return
  }

  updatingStatus.value = true
  try {
    await updateAdminUserStatus(selectedUser.value.id, targetStatus.value)
    ElMessage.success('状态更新成功')
    statusDrawerVisible.value = false
    await loadUsers()
  } catch (error) {
    ElMessage.error(resolveUiErrorMessage(error, '状态更新失败'))
  } finally {
    updatingStatus.value = false
  }
}

function openRolesDrawer(user: AdminUserListItem) {
  const disabledReason = roleActionDisabledReason(user)
  if (disabledReason) {
    ElMessage.warning(disabledReason)
    return
  }
  const editableRoles = (user.roles || []).filter((roleCode) => isRoleVisibleInUI(roleCode))
  selectedUser.value = user
  originalRoleCodes.value = [...editableRoles]
  selectedRoleCodes.value = [...editableRoles]
  rolesDrawerVisible.value = true
}

function openResetPasswordDrawer(user: AdminUserListItem) {
  const disabledReason = resetPasswordActionDisabledReason(user)
  if (disabledReason) {
    ElMessage.warning(disabledReason)
    return
  }
  selectedUser.value = user
  resetPasswordForm.newPassword = ''
  resetPasswordForm.confirmPassword = ''
  resetPasswordDrawerVisible.value = true
}

async function submitRoles() {
  if (updatingRoles.value) return
  if (!selectedUser.value) return

  const original = new Set(originalRoleCodes.value)
  const current = new Set(selectedRoleCodes.value)
  const toGrant = [...current].filter((role) => !original.has(role))
  const toRevoke = [...original].filter((role) => !current.has(role))

  updatingRoles.value = true
  try {
    for (const roleCode of toRevoke) {
      await revokeRole(selectedUser.value.id, roleCode)
    }
    for (const roleCode of toGrant) {
      await grantRole(selectedUser.value.id, roleCode)
    }
    ElMessage.success('角色更新成功')
    rolesDrawerVisible.value = false
    await loadUsers()
  } catch (error) {
    ElMessage.error(resolveUiErrorMessage(error, '角色更新失败'))
  } finally {
    updatingRoles.value = false
  }
}

async function submitResetPassword() {
  if (resettingPassword.value || !selectedUser.value || !resetPasswordFormRef.value) return
  const valid = await resetPasswordFormRef.value.validate().catch(() => false)
  if (!valid) return

  resettingPassword.value = true
  try {
    await resetAdminUserPassword(selectedUser.value.id, resetPasswordForm.newPassword)
    ElMessage.success('重置密码成功，目标账号已被强制重新登录')
    resetPasswordDrawerVisible.value = false
  } catch (error) {
    ElMessage.error(resolveUiErrorMessage(error, '重置密码失败'))
  } finally {
    resettingPassword.value = false
  }
}

onMounted(async () => {
  await Promise.all([loadRoles(), loadUsers()])
})
</script>

<style scoped>
.auth-action-bar {
  /* Use global .action-bar margin */
}

.filters {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-input {
  width: 320px;
}

.filter-select {
  width: 160px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.drawer-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.drawer-content {
  display: grid;
  gap: 12px;
}

.status-summary {
  display: grid;
  gap: 6px;
  padding: 12px;
  border-radius: 8px;
  background: #f5f7fa;
}

.status-summary p {
  margin: 0;
  color: var(--text-regular);
}

.status-summary span {
  color: var(--text-primary);
  font-weight: 600;
}

.full-width {
  width: 100%;
}

.role-list {
  display: grid;
  gap: 10px;
}

.empty-text {
  color: var(--text-secondary);
}
</style>

