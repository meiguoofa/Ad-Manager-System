import * as delivery from '../../mock/delivery'
import * as drama from '../../mock/drama'
import * as material from '../../mock/material'
import * as site from '../../mock/site'
import * as order from '../../mock/order'
import * as system from '../../mock/system'
import { delay } from '../../mock/utils'

export const deliveryApi = {
  dashboardStats: () => delay(delivery.dashboardStats),
  costTrend: () => delay(delivery.costTrend),
  channelShare: () => delay(delivery.channelShare),
  roiTrend: () => delay(delivery.roiTrend),
  realtimeAlerts: () => delay(delivery.realtimeAlerts),
  dailyCost: () => delay(delivery.dailyCostTable),
  h5Stats: () => delay(delivery.h5DashboardStats),
  h5TopPages: () => delay(delivery.h5TopPages),
  adDashboard: () => delay(delivery.adDashboardTable),
  acquisitionStats: () => delay(delivery.acquisitionStats),
  acquisitionByChannel: () => delay(delivery.acquisitionByChannel),
  acquisitionTrend: () => delay(delivery.acquisitionTrend),
  roi: () => delay(delivery.roiTable),
  materialStats: () => delay(delivery.materialStatsTable),
  rechargeTemplate: () => delay(delivery.rechargeTemplateTable),
  linkDetect: () => delay(delivery.linkDetectTable),
  batchDelivery: () => delay(delivery.batchDeliveryTable),
  autoRules: () => delay(delivery.autoRulesTable),
  rechargeDetail: () => delay(delivery.rechargeDetailTable),
  accountList: () => delay(delivery.accountListTable),
}

export const dramaApi = {
  stats: () => delay(drama.dramaStatsOverview),
  playTrend: () => delay(drama.dramaPlayTrend),
  rechargeTrend: () => delay(drama.dramaRechargeTrend),
  channelShare: () => delay(drama.dramaChannelShare),
  rank: () => delay(drama.dramaRankTable),
  episodeAnalysis: () => delay(drama.episodeAnalysisTable),
  retentionCurve: () => delay(drama.episodeRetentionCurve),
  management: () => delay(drama.dramaManagementTable),
  detailBase: () => delay(drama.dramaDetailBase),
  detailScript: () => delay(drama.dramaDetailScriptTable),
  detailPrice: () => delay(drama.dramaPriceTable),
  detailUpdate: () => delay(drama.dramaUpdateTable),
  productLibrary: () => delay(drama.productLibraryTable),
}

export const materialApi = {
  library: () => delay(material.materialLibraryTable),
  batch: () => delay(material.materialBatchTable),
  cards: () => delay(material.materialCards),
}

export const siteApi = {
  list: () => delay(site.siteList),
  visitTrend: () => delay(site.siteVisitTrend),
  revenueTrend: () => delay(site.siteRevenueTrend),
  pages: () => delay(site.pageManagementTable),
  editorComponents: () => delay(site.pageEditorComponents),
  editorCanvas: () => delay(site.pageEditorCanvas),
  domains: () => delay(site.domainConfigTable),
  templates: () => delay(site.templateCenterCards),
}

export const orderApi = {
  stats: () => delay(order.orderStats),
  thirdParty: () => delay(order.thirdPartyOrdersTable),
  all: () => delay(order.allOrdersTable),
  detail: () => delay(order.orderDetail),
  refunds: () => delay(order.refundOrdersTable),
  refundDetail: () => delay(order.refundDetail),
}

export const systemApi = {
  roles: () => delay(system.roleTable),
  deptTree: () => delay(system.deptTree),
  menus: () => delay(system.menuManagementTable),
  dicts: () => delay(system.dictManagementTable),
  params: () => delay(system.paramSettingsTable),
  operationLogs: () => delay(system.operationLogTable),
  loginLogs: () => delay(system.loginLogTable),
  mediaChannels: () => delay(system.mediaChannelTable),
  languages: () => delay(system.languageTable),
  timezones: () => delay(system.timezoneTable),
  adAccounts: () => delay(system.adAccountTable),
}
