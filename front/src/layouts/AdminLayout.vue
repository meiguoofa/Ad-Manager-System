<template>
  <div class="app-wrapper">
    <aside class="sidebar" :class="{ 'is-collapsed': isCollapse }">
      <div class="logo-container">
        <div class="logo">仙</div>
        <span class="logo-text" v-show="!isCollapse">仙之大陆</span>
      </div>

      <div class="sidebar-scroll">
        <div v-for="group in menuTree" :key="group.label" class="menu-group">
          <div v-if="!isCollapse" class="menu-group-title">{{ group.label }}</div>
          <template v-for="item in group.items" :key="item.label">
            <template v-if="item.children && item.children.length">
              <div class="menu-item" :class="{ expanded: openKeys.has(item.label) }" @click="toggleSubmenu(item.label)">
                <span class="menu-icon">{{ item.icon }}</span>
                <span class="menu-text" v-if="!isCollapse">{{ item.label }}</span>
                <span class="menu-arrow" v-if="!isCollapse">▶</span>
              </div>
              <div class="submenu" :class="{ open: openKeys.has(item.label) || isCollapse }">
                <div
                  v-for="child in item.children"
                  :key="child.path"
                  v-show="!child.hidden"
                  class="submenu-item"
                  :class="{ active: activeMenu === child.path }"
                  @click="navigate(child.path)"
                >{{ child.label }}</div>
              </div>
            </template>
            <div
              v-else
              class="menu-item"
              :class="{ active: activeMenu === item.path }"
              @click="navigate(item.path)"
            >
              <span class="menu-icon">{{ item.icon }}</span>
              <span class="menu-text" v-if="!isCollapse">{{ item.label }}</span>
            </div>
          </template>
        </div>
      </div>
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
          <el-input v-model="changePasswordForm.oldPassword" type="password" show-password maxlength="128" autocomplete="current-password" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="changePasswordForm.newPassword" type="password" show-password maxlength="128" autocomplete="new-password" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="changePasswordForm.confirmPassword" type="password" show-password maxlength="128" autocomplete="new-password" />
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
import { ArrowDown, UserFilled, Fold, Expand } from '@element-plus/icons-vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useAdminSessionStore, type RoleCode } from '../stores/adminSession'
import { topbarRoleDisplay } from '../constants/roleDisplay'
import { changePassword } from '../api/adminAuth'

interface MenuLeaf { label: string; path?: string; icon?: string; hidden?: boolean }
interface MenuNode extends MenuLeaf { children?: MenuLeaf[] }

const route = useRoute()
const router = useRouter()
const session = useAdminSessionStore()

const isCollapse = ref(false)
const openKeys = ref<Set<string>>(new Set())
const changePasswordDialogVisible = ref(false)
const changingPassword = ref(false)
const changePasswordFormRef = ref<FormInstance>()
const changePasswordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

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
        if (!value) { callback(new Error('请确认新密码')); return }
        if (value !== changePasswordForm.newPassword) { callback(new Error('两次输入的新密码不一致')); return }
        callback()
      },
      trigger: 'blur',
    },
  ],
}

// 从路由 meta 构建菜单树
const menuTree = computed(() => {
  const groups = new Map<string, { label: string; items: MenuNode[] }>()
  const allRoutes = router.getRoutes().filter((r) => r.meta?.menu)

  for (const r of allRoutes) {
    const meta = r.meta as any
    const menu = meta.menu
    if (menu.hidden && menu.group !== undefined && !menu.subgroup) {
      // 顶级 hidden 路由不显示
      if (!menu.subgroup) continue
    }
    const hasRole = session.hasAnyRole(meta.roles as RoleCode[])
    if (!hasRole) continue

    if (!groups.has(menu.group)) groups.set(menu.group, { label: menu.group, items: [] })
    const group = groups.get(menu.group)!

    if (menu.subgroup) {
      let parent = group.items.find((i) => i.label === menu.subgroup)
      if (!parent) {
        parent = { label: menu.subgroup, icon: menu.groupIcon, children: [] }
        group.items.push(parent)
      } else if (!parent.icon && menu.groupIcon) {
        parent.icon = menu.groupIcon
      }
      if (!parent.children) parent.children = []
      if (!menu.hidden) {
        parent.children.push({ label: meta.title, path: r.path, hidden: menu.hidden })
      } else {
        parent.children.push({ label: meta.title, path: r.path, hidden: true })
      }
    } else {
      if (!menu.hidden) {
        group.items.push({ label: meta.title as string, path: r.path, icon: menu.icon || menu.groupIcon })
      }
    }
  }

  const orderMap: Record<string, number> = {
    '首页': 1, '投放系统': 2, '短剧内容': 3, '独立站管理': 4, '订单管理': 5, '系统管理': 6, '基础配置': 7,
  }
  return Array.from(groups.values()).sort((a, b) => (orderMap[a.label] || 99) - (orderMap[b.label] || 99))
})

const activeMenu = computed(() => route.path)
const userRoleDisplay = computed(() => topbarRoleDisplay(session.roles))
const currentTitle = computed(() => String(route.meta.title || '模块'))

function toggleSidebar() { isCollapse.value = !isCollapse.value }

function toggleSubmenu(label: string) {
  if (openKeys.value.has(label)) openKeys.value.delete(label)
  else openKeys.value.add(label)
}

function navigate(path?: string) {
  if (path) router.push(path)
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
  if (changingPassword.value || !changePasswordFormRef.value) return
  const valid = await changePasswordFormRef.value.validate().catch(() => false)
  if (!valid) return

  if (changePasswordForm.oldPassword === changePasswordForm.newPassword) {
    ElMessage.warning('新密码不能与旧密码相同')
    return
  }

  changingPassword.value = true
  try {
    await changePassword({ oldPassword: changePasswordForm.oldPassword, newPassword: changePasswordForm.newPassword })
    ElMessage.success('密码修改成功')
    changePasswordDialogVisible.value = false
  } catch (error: any) {
    ElMessage.error(error?.message || '密码修改失败')
  } finally {
    changingPassword.value = false
  }
}

// 默认展开包含当前路由的子菜单
const initialOpen = new Set<string>()
for (const r of router.getRoutes()) {
  const meta = r.meta as any
  if (meta?.menu?.subgroup && r.path === route.path) initialOpen.add(meta.menu.subgroup)
}
if (route.path === '/auth' || route.path === '/audit') initialOpen.add('系统管理')
openKeys.value = initialOpen
</script>

<style scoped>
.app-wrapper { display: flex; height: 100vh; width: 100%; }
.sidebar {
  width: 240px;
  height: 100%;
  background-color: #001529;
  display: flex;
  flex-direction: column;
  z-index: 10;
  transition: width 0.3s ease;
  overflow: hidden;
}
.sidebar.is-collapsed { width: 80px; }
.logo-container {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 12px;
  background-color: #002140;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
  flex-shrink: 0;
}
.sidebar.is-collapsed .logo-container { padding: 0; justify-content: center; }
.logo {
  width: 32px; height: 32px;
  background: #1890ff;
  border-radius: 6px;
  display: flex; align-items: center; justify-content: center;
  font-weight: bold; font-size: 16px;
  flex-shrink: 0;
}
.logo-text { font-size: 16px; font-weight: 600; letter-spacing: 0.5px; }

.sidebar-scroll { flex: 1; overflow-y: auto; overflow-x: hidden; padding: 8px 0; }
.sidebar-scroll::-webkit-scrollbar { width: 6px; }
.sidebar-scroll::-webkit-scrollbar-thumb { background: rgba(255,255,255,.2); border-radius: 3px; }

.menu-group { margin-bottom: 4px; }
.menu-group-title {
  padding: 10px 24px 8px;
  font-size: 12px;
  color: rgba(255,255,255,.45);
  white-space: nowrap; overflow: hidden;
  height: 32px;
  display: flex; align-items: center;
}
.menu-item {
  padding: 10px 24px;
  cursor: pointer;
  display: flex;
  align-items: center;
  color: rgba(255,255,255,.65);
  transition: all .2s;
  white-space: nowrap; overflow: hidden;
  height: 40px;
  position: relative;
}
.menu-item:hover { color: #fff; background: rgba(255,255,255,.08); }
.menu-item.active { color: #fff; background: #1890ff; }
.menu-item .menu-icon { width: 16px; height: 16px; margin-right: 12px; display: inline-flex; align-items: center; justify-content: center; font-size: 16px; flex-shrink: 0; }
.sidebar.is-collapsed .menu-item { justify-content: center; padding: 10px; }
.sidebar.is-collapsed .menu-item .menu-icon { margin-right: 0; }
.sidebar.is-collapsed .menu-item .menu-text { display: none; }
.menu-arrow { margin-left: auto; font-size: 10px; transition: transform .2s; }
.menu-item.expanded .menu-arrow { transform: rotate(90deg); }

.submenu { max-height: 0; overflow: hidden; transition: max-height .3s; background: #000c17; }
.submenu.open { max-height: 1000px; }
.sidebar.is-collapsed .submenu { display: none; }
.submenu-item {
  padding: 10px 24px 10px 52px;
  cursor: pointer;
  color: rgba(255,255,255,.65);
  white-space: nowrap; overflow: hidden;
  height: 38px;
  display: flex; align-items: center;
  transition: all .2s;
  font-size: 13px;
}
.submenu-item:hover { color: #fff; }
.submenu-item.active { color: #fff; background: rgba(24,144,255,.2); }

.main-container {
  flex: 1; display: flex; flex-direction: column;
  min-width: 0; background-color: var(--bg-color);
}
.topbar {
  height: 64px;
  background-color: #fff;
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 24px 0 16px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 9;
}
.topbar-left { display: flex; align-items: center; gap: 16px; }
.hamburger-btn {
  display: flex; align-items: center; justify-content: center;
  width: 32px; height: 32px;
  cursor: pointer; border-radius: 4px;
  color: #606266;
  transition: background-color 0.2s, color 0.2s;
}
.hamburger-btn:hover { background-color: #f5f7fa; color: #1890ff; }
.topbar-right { display: flex; align-items: center; gap: 16px; }
.user-dropdown {
  display: flex; align-items: center; gap: 8px;
  cursor: pointer; padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
}
.user-dropdown:hover { background-color: #f5f7fa; }
.username { font-size: 14px; color: #303133; font-weight: 500; }
.app-main { flex: 1; padding: 24px; overflow-y: auto; }
</style>
