import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAdminSessionStore, type RoleCode } from '../stores/adminSession'
import AdminLayout from '../layouts/AdminLayout.vue'
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'
import AuditView from '../views/AuditView.vue'
import AuthRbacView from '../views/AuthRbacView.vue'
import ForbiddenView from '../views/ForbiddenView.vue'
import TikTokCallbackView from '../views/TikTokCallbackView.vue'

declare module 'vue-router' {
  interface RouteMeta {
    public?: boolean
    title?: string
    roles?: RoleCode[]
    menu?: { group: string; subgroup?: string; icon?: string; hidden?: boolean; order?: number }
  }
}

const ALL_ROLES: RoleCode[] = ['PRIMARY_SUPER_ADMIN', 'SUPER_ADMIN', 'OPERATOR', 'SALES', 'AUDITOR']

const lazy = (loader: () => Promise<any>) => () => loader()

const routes: RouteRecordRaw[] = [
  { path: '/login', name: 'login', component: LoginView, meta: { public: true } },
  { path: '/403', name: 'forbidden', component: ForbiddenView, meta: { public: true } },
  { path: '/tiktok/callback', name: 'tiktok-callback', component: TikTokCallbackView, meta: { public: true } },
  {
    path: '/',
    component: AdminLayout,
    children: [
      { path: '', name: 'home', component: HomeView, meta: { title: '工作台', roles: ALL_ROLES, menu: { group: '首页', icon: '🏠', order: 1 } } },
      { path: 'auth', name: 'auth', component: AuthRbacView, meta: { title: '用户管理', roles: ['PRIMARY_SUPER_ADMIN', 'SUPER_ADMIN'], menu: { group: '系统管理', icon: '👤', order: 601 } } },
      { path: 'audit', name: 'audit', component: AuditView, meta: { title: '审计日志', roles: ['PRIMARY_SUPER_ADMIN', 'SUPER_ADMIN', 'AUDITOR'], menu: { group: '系统管理', icon: '📝', order: 608, hidden: false } } },

      // 投放系统 - 投放数据
      { path: 'delivery/daily-cost', name: 'delivery-daily-cost', component: lazy(() => import('../views/business/delivery/DailyCostView.vue')), meta: { title: '每日消耗', roles: ALL_ROLES, menu: { group: '投放系统', subgroup: '投放数据', icon: '💵', order: 201 } } },
      { path: 'delivery/h5-dashboard', name: 'delivery-h5-dashboard', component: lazy(() => import('../views/business/delivery/H5DashboardView.vue')), meta: { title: 'H5看板', roles: ALL_ROLES, menu: { group: '投放系统', subgroup: '投放数据', order: 202 } } },
      { path: 'delivery/ad-dashboard', name: 'delivery-ad-dashboard', component: lazy(() => import('../views/business/delivery/AdDashboardView.vue')), meta: { title: '广告看板', roles: ALL_ROLES, menu: { group: '投放系统', subgroup: '投放数据', order: 203 } } },
      { path: 'delivery/user-acquisition', name: 'delivery-user-acquisition', component: lazy(() => import('../views/business/delivery/UserAcquisitionView.vue')), meta: { title: '拉新数据', roles: ALL_ROLES, menu: { group: '投放系统', subgroup: '投放数据', order: 204 } } },
      { path: 'delivery/roi-data', name: 'delivery-roi-data', component: lazy(() => import('../views/business/delivery/RoiDataView.vue')), meta: { title: 'ROI数据', roles: ALL_ROLES, menu: { group: '投放系统', subgroup: '投放数据', order: 205 } } },
      { path: 'delivery/material-stats', name: 'delivery-material-stats', component: lazy(() => import('../views/business/delivery/MaterialStatsView.vue')), meta: { title: '素材统计', roles: ALL_ROLES, menu: { group: '投放系统', subgroup: '投放数据', order: 206 } } },

      // 投放系统 - 短剧数据
      { path: 'delivery/drama-stats', name: 'delivery-drama-stats', component: lazy(() => import('../views/business/delivery/DramaStatsView.vue')), meta: { title: '短剧统计', roles: ALL_ROLES, menu: { group: '投放系统', subgroup: '短剧数据', order: 211 } } },
      { path: 'delivery/episode-analysis', name: 'delivery-episode-analysis', component: lazy(() => import('../views/business/delivery/EpisodeAnalysisView.vue')), meta: { title: '剧集分析', roles: ALL_ROLES, menu: { group: '投放系统', subgroup: '短剧数据', order: 212 } } },

      // 投放系统 - 投放管理
      { path: 'delivery/recharge-template', name: 'delivery-recharge-template', component: lazy(() => import('../views/business/delivery/RechargeTemplateView.vue')), meta: { title: '充值模板', roles: ALL_ROLES, menu: { group: '投放系统', subgroup: '投放管理', order: 221 } } },
      { path: 'delivery/link-detect', name: 'delivery-link-detect', component: lazy(() => import('../views/business/delivery/LinkDetectView.vue')), meta: { title: '检测链接', roles: ALL_ROLES, menu: { group: '投放系统', subgroup: '投放管理', order: 222 } } },
      { path: 'delivery/batch-delivery', name: 'delivery-batch-delivery', component: lazy(() => import('../views/business/delivery/BatchDeliveryView.vue')), meta: { title: '批量投放', roles: ALL_ROLES, menu: { group: '投放系统', subgroup: '投放管理', order: 223 } } },
      { path: 'delivery/auto-rules', name: 'delivery-auto-rules', component: lazy(() => import('../views/business/delivery/AutoRulesView.vue')), meta: { title: '自动规则', roles: ALL_ROLES, menu: { group: '投放系统', subgroup: '投放管理', order: 224 } } },
      { path: 'delivery/rule-detail/:id', name: 'delivery-rule-detail', component: lazy(() => import('../views/business/delivery/RuleDetailView.vue')), meta: { title: '规则详情', roles: ALL_ROLES, menu: { group: '投放系统', subgroup: '投放管理', hidden: true } } },

      // 投放系统 - 充值管理
      { path: 'delivery/recharge-detail', name: 'delivery-recharge-detail', component: lazy(() => import('../views/business/delivery/RechargeDetailView.vue')), meta: { title: '充值明细', roles: ALL_ROLES, menu: { group: '投放系统', subgroup: '充值管理', order: 231 } } },
      { path: 'delivery/account-list', name: 'delivery-account-list', component: lazy(() => import('../views/business/delivery/AccountListView.vue')), meta: { title: '账户列表', roles: ALL_ROLES, menu: { group: '投放系统', subgroup: '充值管理', order: 232 } } },
      { path: 'delivery/account-detail/:id', name: 'delivery-account-detail', component: lazy(() => import('../views/business/delivery/AccountDetailView.vue')), meta: { title: '账户详情', roles: ALL_ROLES, menu: { group: '投放系统', subgroup: '充值管理', hidden: true } } },

      // 素材库
      { path: 'material/library', name: 'material-library', component: lazy(() => import('../views/business/material/MaterialLibraryView.vue')), meta: { title: '素材库', roles: ALL_ROLES, menu: { group: '短剧内容', subgroup: '素材库', icon: '🎞️', order: 313 } } },

      // 短剧内容
      { path: 'drama/management', name: 'drama-management', component: lazy(() => import('../views/business/drama/DramaManagementView.vue')), meta: { title: '短剧管理', roles: ALL_ROLES, menu: { group: '短剧内容', subgroup: '短剧管理', icon: '📽️', order: 311 } } },
      { path: 'drama/detail/:id', name: 'drama-detail', component: lazy(() => import('../views/business/drama/DramaDetailView.vue')), meta: { title: '短剧详情', roles: ALL_ROLES, menu: { group: '短剧内容', subgroup: '短剧管理', hidden: true } } },
      { path: 'drama/product-library', name: 'drama-product-library', component: lazy(() => import('../views/business/drama/ProductLibraryView.vue')), meta: { title: '商品库', roles: ALL_ROLES, menu: { group: '短剧内容', subgroup: '商品库', icon: '🛒', order: 321 } } },
      { path: 'drama/product-detail/:id', name: 'drama-product-detail', component: lazy(() => import('../views/business/drama/ProductDetailView.vue')), meta: { title: '商品详情', roles: ALL_ROLES, menu: { group: '短剧内容', subgroup: '商品库', hidden: true } } },

      // 独立站管理
      { path: 'site/list', name: 'site-list', component: lazy(() => import('../views/business/site/SiteListView.vue')), meta: { title: '站点列表', roles: ALL_ROLES, menu: { group: '独立站管理', subgroup: '站点管理', icon: '🌐', order: 411 } } },
      { path: 'site/detail/:id', name: 'site-detail', component: lazy(() => import('../views/business/site/SiteDetailView.vue')), meta: { title: '站点详情', roles: ALL_ROLES, menu: { group: '独立站管理', subgroup: '站点管理', hidden: true } } },
      { path: 'site/page-management', name: 'site-page-management', component: lazy(() => import('../views/business/site/PageManagementView.vue')), meta: { title: '落地页列表', roles: ALL_ROLES, menu: { group: '独立站管理', subgroup: '页面管理', icon: '📄', order: 421 } } },
      { path: 'site/page-editor/:id?', name: 'site-page-editor', component: lazy(() => import('../views/business/site/PageEditorView.vue')), meta: { title: '页面编辑器', roles: ALL_ROLES, menu: { group: '独立站管理', subgroup: '页面管理', hidden: true } } },
      { path: 'site/domain-config', name: 'site-domain-config', component: lazy(() => import('../views/business/site/DomainConfigView.vue')), meta: { title: '域名配置', roles: ALL_ROLES, menu: { group: '独立站管理', icon: '🔗', order: 431 } } },
      { path: 'site/template-center', name: 'site-template-center', component: lazy(() => import('../views/business/site/TemplateCenterView.vue')), meta: { title: '模板中心', roles: ALL_ROLES, menu: { group: '独立站管理', icon: '🎯', order: 441 } } },

      // 订单管理
      { path: 'order/third-party', name: 'order-third-party', component: lazy(() => import('../views/business/order/ThirdPartyOrdersView.vue')), meta: { title: '三方支付订单', roles: ALL_ROLES, menu: { group: '订单管理', subgroup: '订单管理', icon: '📦', order: 511 } } },
      { path: 'order/all', name: 'order-all', component: lazy(() => import('../views/business/order/AllOrdersView.vue')), meta: { title: '全部订单', roles: ALL_ROLES, menu: { group: '订单管理', subgroup: '订单管理', order: 512 } } },
      { path: 'order/detail/:id', name: 'order-detail', component: lazy(() => import('../views/business/order/OrderDetailView.vue')), meta: { title: '订单详情', roles: ALL_ROLES, menu: { group: '订单管理', subgroup: '订单管理', hidden: true } } },
      { path: 'order/refund', name: 'order-refund', component: lazy(() => import('../views/business/order/RefundOrdersView.vue')), meta: { title: '退款订单', roles: ALL_ROLES, menu: { group: '订单管理', subgroup: '订单管理', order: 513 } } },
      { path: 'order/refund-detail/:id', name: 'order-refund-detail', component: lazy(() => import('../views/business/order/RefundDetailView.vue')), meta: { title: '退款详情', roles: ALL_ROLES, menu: { group: '订单管理', subgroup: '订单管理', hidden: true } } },

      // 系统管理
      { path: 'system/role', name: 'system-role', component: lazy(() => import('../views/business/system/RoleManagementView.vue')), meta: { title: '角色管理', roles: ['PRIMARY_SUPER_ADMIN', 'SUPER_ADMIN'], menu: { group: '系统管理', icon: '🎭', order: 602 } } },
      { path: 'system/dept', name: 'system-dept', component: lazy(() => import('../views/business/system/DeptManagementView.vue')), meta: { title: '部门管理', roles: ['PRIMARY_SUPER_ADMIN', 'SUPER_ADMIN'], menu: { group: '系统管理', icon: '🏢', order: 603 } } },
      { path: 'system/menu', name: 'system-menu', component: lazy(() => import('../views/business/system/MenuManagementView.vue')), meta: { title: '菜单管理', roles: ['PRIMARY_SUPER_ADMIN', 'SUPER_ADMIN'], menu: { group: '系统管理', icon: '📋', order: 604 } } },
      { path: 'system/dict', name: 'system-dict', component: lazy(() => import('../views/business/system/DictManagementView.vue')), meta: { title: '字典管理', roles: ['PRIMARY_SUPER_ADMIN', 'SUPER_ADMIN'], menu: { group: '系统管理', icon: '📖', order: 605 } } },
      { path: 'system/param', name: 'system-param', component: lazy(() => import('../views/business/system/ParamSettingsView.vue')), meta: { title: '参数设置', roles: ['PRIMARY_SUPER_ADMIN', 'SUPER_ADMIN'], menu: { group: '系统管理', icon: '⚙️', order: 606 } } },
      { path: 'system/operation-log', name: 'system-operation-log', component: lazy(() => import('../views/business/system/OperationLogView.vue')), meta: { title: '操作日志', roles: ['PRIMARY_SUPER_ADMIN', 'SUPER_ADMIN', 'AUDITOR'], menu: { group: '系统管理', icon: '📝', order: 607 } } },
      { path: 'system/login-log', name: 'system-login-log', component: lazy(() => import('../views/business/system/LoginLogView.vue')), meta: { title: '登录日志', roles: ['PRIMARY_SUPER_ADMIN', 'SUPER_ADMIN', 'AUDITOR'], menu: { group: '系统管理', icon: '🔐', order: 609 } } },

      // 基础配置
      { path: 'basic/media-channel', name: 'basic-media-channel', component: lazy(() => import('../views/business/basic/MediaChannelView.vue')), meta: { title: '媒体渠道', roles: ALL_ROLES, menu: { group: '基础配置', icon: '📺', order: 701 } } },
      { path: 'basic/language', name: 'basic-language', component: lazy(() => import('../views/business/basic/LanguageManagementView.vue')), meta: { title: '语言管理', roles: ALL_ROLES, menu: { group: '基础配置', icon: '🌍', order: 702 } } },
      { path: 'basic/timezone', name: 'basic-timezone', component: lazy(() => import('../views/business/basic/TimezoneConfigView.vue')), meta: { title: '时区配置', roles: ALL_ROLES, menu: { group: '基础配置', icon: '🕐', order: 703 } } },
      { path: 'basic/ad-account', name: 'basic-ad-account', component: lazy(() => import('../views/business/basic/AdAccountView.vue')), meta: { title: '广告账户管理', roles: ALL_ROLES, menu: { group: '基础配置', icon: '💳', order: 704 } } },
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
