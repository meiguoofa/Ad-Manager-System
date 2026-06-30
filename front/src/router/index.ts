import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAdminSessionStore, type RoleCode } from '../stores/adminSession'
import AdminLayout from '../layouts/AdminLayout.vue'
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'
import AuditView from '../views/AuditView.vue'
import AuthRbacView from '../views/AuthRbacView.vue'
import PlaceholderView from '../views/PlaceholderView.vue'
import ForbiddenView from '../views/ForbiddenView.vue'

declare module 'vue-router' {
  interface RouteMeta {
    public?: boolean
    title?: string
    roles?: RoleCode[]
  }
}

const ALL_ROLES: RoleCode[] = ['PRIMARY_SUPER_ADMIN', 'SUPER_ADMIN', 'OPERATOR', 'SALES', 'AUDITOR']

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { public: true },
  },
  {
    path: '/403',
    name: 'forbidden',
    component: ForbiddenView,
    meta: { public: true },
  },
  {
    path: '/',
    component: AdminLayout,
    children: [
      { path: '', name: 'home', component: HomeView, meta: { title: '运营总览', roles: ALL_ROLES } },

      { path: 'auth', name: 'auth', component: AuthRbacView, meta: { title: '用户管理', roles: ['PRIMARY_SUPER_ADMIN', 'SUPER_ADMIN'] } },
      { path: 'audit', name: 'audit', component: AuditView, meta: { title: '审计日志', roles: ['PRIMARY_SUPER_ADMIN', 'SUPER_ADMIN', 'AUDITOR'] } },

      { path: 'delivery/data/daily-cost', name: 'delivery-data-daily-cost', component: PlaceholderView, meta: { title: '每日消耗', roles: ALL_ROLES } },
      { path: 'delivery/data/h5-dashboard', name: 'delivery-data-h5', component: PlaceholderView, meta: { title: 'H5看板', roles: ALL_ROLES } },
      { path: 'delivery/data/ad-dashboard', name: 'delivery-data-ad', component: PlaceholderView, meta: { title: '广告看板', roles: ALL_ROLES } },
      { path: 'delivery/data/acquisition', name: 'delivery-data-acquisition', component: PlaceholderView, meta: { title: '拉新数据', roles: ALL_ROLES } },
      { path: 'delivery/data/roi', name: 'delivery-data-roi', component: PlaceholderView, meta: { title: 'ROI数据', roles: ALL_ROLES } },
      { path: 'delivery/data/material', name: 'delivery-data-material', component: PlaceholderView, meta: { title: '素材统计', roles: ALL_ROLES } },

      { path: 'delivery/drama-data/summary', name: 'delivery-drama-summary', component: PlaceholderView, meta: { title: '短剧统计', roles: ALL_ROLES } },
      { path: 'delivery/drama-data/episodes', name: 'delivery-drama-episodes', component: PlaceholderView, meta: { title: '剧集分析', roles: ALL_ROLES } },

      { path: 'delivery/management', name: 'delivery-management', component: PlaceholderView, meta: { title: '投放管理', roles: ALL_ROLES } },
      { path: 'delivery/detect-links', name: 'delivery-detect-links', component: PlaceholderView, meta: { title: '检测链接', roles: ALL_ROLES } },

      { path: 'delivery/recharge/details', name: 'delivery-recharge-details', component: PlaceholderView, meta: { title: '充值明细', roles: ALL_ROLES } },
      { path: 'delivery/recharge/accounts', name: 'delivery-recharge-accounts', component: PlaceholderView, meta: { title: '账户列表', roles: ALL_ROLES } },

      { path: 'drama-content/dramas', name: 'drama-content-dramas', component: PlaceholderView, meta: { title: '短剧管理', roles: ALL_ROLES } },
      { path: 'drama-content/products', name: 'drama-content-products', component: PlaceholderView, meta: { title: '商品库', roles: ALL_ROLES } },

      { path: 'orders/third-party-pay', name: 'orders-third-party', component: PlaceholderView, meta: { title: '三方支付订单', roles: ALL_ROLES } },
      { path: 'orders/all', name: 'orders-all', component: PlaceholderView, meta: { title: '全部订单', roles: ALL_ROLES } },
      { path: 'orders/refunds', name: 'orders-refunds', component: PlaceholderView, meta: { title: '退款订单', roles: ALL_ROLES } },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to) => {
  const session = useAdminSessionStore()

  if (to.meta.public) {
    if (to.name === 'login' && session.isAuthenticated) {
      return { name: 'home' }
    }
    return true
  }

  if (!session.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  if (!session.profile) {
    try {
      await session.fetchMe()
    } catch {
      session.clearSession()
      return { name: 'login', query: { redirect: to.fullPath } }
    }
  }

  if (!session.hasAnyRole(to.meta.roles)) {
    return { name: 'forbidden' }
  }

  return true
})

export default router
