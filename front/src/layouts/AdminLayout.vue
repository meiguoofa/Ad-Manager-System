<template>
  <div class="app-wrapper">
    <aside class="sidebar" :class="{ 'is-collapsed': isCollapse }">
      <div class="logo-container">
        <div class="logo">仙</div>
        <span class="logo-text" v-show="!isCollapse">仙之大陆</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        background-color="#001529"
        text-color="#a6adb4"
        active-text-color="#ffffff"
        :collapse="isCollapse"
        :collapse-transition="false"
      >
        <MenuNode v-for="item in menuItems" :key="item.label" :node="item" />
      </el-menu>
    </aside>

    <div class="main-container">
      <header class="topbar">
        <div class="topbar-left">
          <div class="hamburger-btn" @click="toggleSidebar">
            <el-icon :size="20">
              <Expand v-if="isCollapse" />
              <Fold v-else />
            </el-icon>
          </div>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>后台管理</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="topbar-right">
          <el-dropdown trigger="click">
            <span class="user-dropdown">
              <el-icon><UserFilled /></el-icon>
              <span class="username">{{ session.profile?.username }} ({{ userRoleDisplay }})</span>
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="openChangePasswordDialog">修改密码</el-dropdown-item>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="app-main">
        <RouterView />
      </main>
    </div>

    <el-dialog v-model="changePasswordDialogVisible" title="修改密码" width="420px" destroy-on-close>
      <el-form ref="changePasswordFormRef" :model="changePasswordForm" :rules="changePasswordRules" label-position="top">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input
            v-model="changePasswordForm.oldPassword"
            type="password"
            show-password
            maxlength="128"
            autocomplete="current-password"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="changePasswordForm.newPassword"
            type="password"
            show-password
            maxlength="128"
            autocomplete="new-password"
          />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="changePasswordForm.confirmPassword"
            type="password"
            show-password
            maxlength="128"
            autocomplete="new-password"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changePasswordDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="changingPassword" @click="submitChangePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  DataAnalysis,
  DocumentChecked,
  Film,
  Key,
  Monitor,
  Operation,
  ArrowDown,
  UserFilled,
  Fold,
  Expand,
} from '@element-plus/icons-vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useAdminSessionStore, type RoleCode } from '../stores/adminSession'
import { topbarRoleDisplay } from '../constants/roleDisplay'
import { changePassword } from '../api/adminAuth'
import MenuNode, { type MenuItem } from './MenuNode.vue'

const route = useRoute()
const router = useRouter()
const session = useAdminSessionStore()

const isCollapse = ref(false)
const changePasswordDialogVisible = ref(false)
const changingPassword = ref(false)
const changePasswordFormRef = ref<FormInstance>()
const changePasswordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const changePasswordRules: FormRules<typeof changePasswordForm> = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' },
    { min: 8, message: '密码至少 8 位', trigger: 'blur' },
  ],
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
        if (value !== changePasswordForm.newPassword) {
          callback(new Error('两次输入的新密码不一致'))
          return
        }
        callback()
      },
      trigger: 'blur',
    },
  ],
}

const ALL: RoleCode[] = ['PRIMARY_SUPER_ADMIN', 'SUPER_ADMIN', 'OPERATOR', 'SALES', 'AUDITOR']

const menuItems: MenuItem[] = [
  { label: '运营总览', path: '/', icon: Monitor, roles: ALL },
  { label: '用户管理', path: '/auth', icon: Key, roles: ['PRIMARY_SUPER_ADMIN', 'SUPER_ADMIN'] },
  {
    label: '投放系统',
    icon: Operation,
    roles: ALL,
    children: [
      {
        label: '投放数据',
        roles: ALL,
        children: [
          { label: '每日消耗', path: '/delivery/data/daily-cost', roles: ALL },
          { label: 'H5看板', path: '/delivery/data/h5-dashboard', roles: ALL },
          { label: '广告看板', path: '/delivery/data/ad-dashboard', roles: ALL },
          { label: '拉新数据', path: '/delivery/data/acquisition', roles: ALL },
          { label: 'ROI数据', path: '/delivery/data/roi', roles: ALL },
          { label: '素材统计', path: '/delivery/data/material', roles: ALL },
        ],
      },
      {
        label: '短剧数据',
        roles: ALL,
        children: [
          { label: '短剧统计', path: '/delivery/drama-data/summary', roles: ALL },
          { label: '剧集分析', path: '/delivery/drama-data/episodes', roles: ALL },
        ],
      },
      { label: '投放管理', path: '/delivery/management', roles: ALL },
      { label: '检测链接', path: '/delivery/detect-links', roles: ALL },
      {
        label: '充值管理',
        roles: ALL,
        children: [
          { label: '充值明细', path: '/delivery/recharge/details', roles: ALL },
          { label: '账户列表', path: '/delivery/recharge/accounts', roles: ALL },
        ],
      },
    ],
  },
  {
    label: '短剧内容',
    icon: Film,
    roles: ALL,
    children: [
      { label: '短剧管理', path: '/drama-content/dramas', roles: ALL },
      { label: '商品库', path: '/drama-content/products', roles: ALL },
    ],
  },
  {
    label: '订单管理',
    icon: DocumentChecked,
    roles: ALL,
    children: [
      { label: '三方支付订单', path: '/orders/third-party-pay', roles: ALL },
      { label: '全部订单', path: '/orders/all', roles: ALL },
      { label: '退款订单', path: '/orders/refunds', roles: ALL },
    ],
  },
  { label: '审计日志', path: '/audit', icon: DataAnalysis, roles: ['PRIMARY_SUPER_ADMIN', 'SUPER_ADMIN', 'AUDITOR'] },
]

const activeMenu = computed(() => route.path)
const userRoleDisplay = computed(() => topbarRoleDisplay(session.roles))
const currentTitle = computed(() => String(route.meta.title || '模块'))

function toggleSidebar() {
  isCollapse.value = !isCollapse.value
}

async function logout() {
  await session.logout()
  router.push('/login')
}

function openChangePasswordDialog() {
  changePasswordForm.oldPassword = ''
  changePasswordForm.newPassword = ''
  changePasswordForm.confirmPassword = ''
  changePasswordDialogVisible.value = true
}

async function submitChangePassword() {
  if (changingPassword.value || !changePasswordFormRef.value) {
    return
  }
  const valid = await changePasswordFormRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  if (changePasswordForm.oldPassword === changePasswordForm.newPassword) {
    ElMessage.warning('新密码不能与旧密码相同')
    return
  }

  changingPassword.value = true
  try {
    await changePassword({
      oldPassword: changePasswordForm.oldPassword,
      newPassword: changePasswordForm.newPassword,
    })
    ElMessage.success('密码修改成功')
    changePasswordDialogVisible.value = false
  } catch (error: any) {
    ElMessage.error(error?.message || '密码修改失败')
  } finally {
    changingPassword.value = false
  }
}
</script>

<style scoped>
.app-wrapper {
  display: flex;
  height: 100vh;
  width: 100%;
}

.sidebar {
  width: 256px;
  height: 100%;
  background-color: #001529;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px 0 rgba(29, 35, 41, 0.05);
  z-index: 10;
  transition: width 0.3s ease;
}

.sidebar.is-collapsed {
  width: 64px;
}

.logo-container {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 12px;
  background-color: #001529;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
}

.sidebar.is-collapsed .logo-container {
  padding: 0;
  justify-content: center;
}

.logo {
  width: 32px;
  height: 32px;
  background: var(--brand-color);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 16px;
  flex-shrink: 0;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 100%;
}

:deep(.menu-disabled-hint) {
  margin-left: 8px;
  font-size: 11px;
  opacity: 0.8;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  background-color: var(--bg-color);
}

.topbar {
  height: 64px;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px 0 16px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 9;
}

.topbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.hamburger-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  cursor: pointer;
  border-radius: 4px;
  color: #606266;
  transition: background-color 0.2s, color 0.2s;
}

.hamburger-btn:hover {
  background-color: #f5f7fa;
  color: var(--brand-color);
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: var(--radius-sm);
  transition: background-color 0.2s;
}

.user-dropdown:hover {
  background-color: #f5f7fa;
}

.username {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
}

.app-main {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}
</style>
