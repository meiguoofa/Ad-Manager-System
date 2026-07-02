import { generateTableData, random, randomDate, formatMoney, pick } from './utils'

export const siteList = Array.from({ length: 8 }, (_, i) => ({
  id: i + 1,
  name: `站点-${i + 1}`,
  domain: `site${i + 1}.example.com`,
  visits: random(1000, 50000),
  recharge: formatMoney(random(5000, 80000)),
  pages: random(5, 30),
  status: pick(['运行中', '已停用', '维护中']),
}))

export const siteVisitTrend = {
  dates: ['06-25', '06-26', '06-27', '06-28', '06-29', '06-30', '07-01'],
  values: [3200, 4100, 5300, 4800, 6200, 7500, 6800],
}

export const siteRevenueTrend = {
  dates: ['06-25', '06-26', '06-27', '06-28', '06-29', '06-30', '07-01'],
  values: [8500, 12000, 15600, 13200, 17800, 21500, 19200],
}

export const pageManagementTable = generateTableData(10, [
  { key: 'name', generator: (i) => `落地页-${i}` },
  { key: 'site', generator: () => `站点-${random(1, 8)}` },
  { key: 'url', generator: (i) => `https://site${random(1, 8)}.example.com/page-${i}` },
  { key: 'visits', generator: () => random(500, 10000).toLocaleString() },
  { key: 'conv', generator: () => (random(10, 80) / 100).toFixed(2) + '%' },
  { key: 'recharge', generator: () => formatMoney(random(1000, 30000)) },
  { key: 'status', generator: () => pick(['已发布', '草稿', '已下线']) },
  { key: 'updateTime', generator: () => randomDate() },
])

export const pageEditorComponents = [
  { group: '基础组件', items: ['文字', '图片', '按钮', '分隔线', '占位'] },
  { group: '表单组件', items: ['输入框', '下拉选择', '单选', '复选', '开关'] },
  { group: '业务组件', items: ['短剧列表', '充值包', '播放器', '剧集卡片', '用户中心'] },
  { group: '布局组件', items: ['栅格', '弹性容器', '标签页', '卡片'] },
]

export const pageEditorCanvas = [
  { id: 1, type: '文字', content: '仙之大陆 · 限时免费' },
  { id: 2, type: '图片', content: '主视觉图（800x400）' },
  { id: 3, type: '短剧列表', content: '热门短剧 · 4列' },
  { id: 4, type: '充值包', content: '¥9.9 / ¥39.9 / ¥128' },
  { id: 5, type: '按钮', content: '立即观看' },
]

export const domainConfigTable = generateTableData(8, [
  { key: 'domain', generator: (i) => `domain${i}.example.com` },
  { key: 'site', generator: () => `站点-${random(1, 8)}` },
  { key: 'type', generator: () => pick(['主域名', '备用域名', '落地页域名']) },
  { key: 'ssl', generator: () => pick(['已配置', '未配置']) },
  { key: 'dns', generator: () => pick(['已解析', '解析中', '失败']) },
  { key: 'status', generator: () => pick(['正常', '异常']) },
  { key: 'bindTime', generator: () => randomDate() },
])

export const templateCenterCards = Array.from({ length: 10 }, (_, i) => ({
  id: i + 1,
  name: `模板-${i + 1}`,
  desc: pick(['限时免费', '热门短剧', '充值专享', '会员专享', '节日活动']),
  category: pick(['短剧', '电商', '充值', '通用']),
}))
