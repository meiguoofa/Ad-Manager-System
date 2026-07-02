import { generateTableData, random, randomDate, formatMoney, randomName, pick, channels, statuses } from './utils'

export const dashboardStats = [
  { label: '今日消耗', value: '¥128,560.25', diff: 12.5, icon: '💰', iconColor: 'blue' as const, trend: [true, true, false, true, true, true, false, true] },
  { label: '今日充值', value: '¥96,320.10', diff: 8.3, icon: '💳', iconColor: 'green' as const, trend: [true, false, true, true, false, true, true, true] },
  { label: '新增用户', value: '1,256', diff: 15.2, icon: '👥', iconColor: 'orange' as const, trend: [true, true, true, false, true, true, false, true] },
  { label: '当日 ROI', value: '74.8%', diff: -2.1, icon: '📈', iconColor: 'red' as const, trend: [false, true, true, false, true, false, true, true] },
]

export const costTrend = {
  dates: ['06-25', '06-26', '06-27', '06-28', '06-29', '06-30', '07-01'],
  values: [98500, 102300, 110800, 95600, 125200, 138900, 128560],
  recharge: [78000, 85000, 92000, 71000, 98000, 105000, 96320],
}

export const channelShare = [
  { name: 'TikTok', value: 45 },
  { name: 'Facebook', value: 28 },
  { name: 'Google', value: 15 },
  { name: '其他', value: 12 },
]

export const roiTrend = {
  dates: ['06-25', '06-26', '06-27', '06-28', '06-29', '06-30', '07-01'],
  values: [68, 72, 75, 71, 78, 80, 74.8],
}

export const realtimeAlerts = [
  { type: 'error', title: '广告组 #233 消耗超预算', desc: 'TikTok · 已消耗 ¥4,500 / 预算 ¥4,000', time: '2分钟前' },
  { type: 'warning', title: 'ROI 低于阈值', desc: '广告组 #198 当前 ROI 52%', time: '15分钟前' },
  { type: 'info', title: '新短剧审核通过', desc: '《仙之大陆》第 12 集已上线', time: '1小时前' },
  { type: 'error', title: '账户余额不足', desc: '账户 #A002 余额 ¥120，请及时充值', time: '2小时前' },
  { type: 'warning', title: '素材审核未通过', desc: '素材 #M1024 违规，请修改后重新提交', time: '3小时前' },
]

export const dailyCostTable = generateTableData(10, [
  { key: 'date', generator: () => randomDate().slice(0, 10) },
  { key: 'channel', generator: () => pick(channels) },
  { key: 'adGroup', generator: (i) => `广告组-${String(i).padStart(3, '0')}` },
  { key: 'cost', generator: () => formatMoney(random(5000, 50000)) },
  { key: 'impressions', generator: () => random(10000, 200000).toLocaleString() },
  { key: 'clicks', generator: () => random(500, 8000).toLocaleString() },
  { key: 'ctr', generator: () => (random(15, 80) / 10).toFixed(2) + '%' },
  { key: 'cpc', generator: () => '¥' + (random(30, 200) / 100).toFixed(2) },
  { key: 'conversions', generator: () => random(20, 500) },
  { key: 'costPerConv', generator: () => formatMoney(random(20, 200)) },
  { key: 'status', generator: () => pick(statuses) },
])

export const h5DashboardStats = [
  { label: '访问数', value: '24,560', diff: 10.2, icon: '👁️', iconColor: 'blue' as const },
  { label: '充值人数', value: '1,256', diff: 8.5, icon: '💳', iconColor: 'green' as const },
  { label: '充值金额', value: '¥96,320', diff: 12.1, icon: '💰', iconColor: 'orange' as const },
  { label: '转化率', value: '5.12%', diff: 1.3, icon: '📈', iconColor: 'cyan' as const },
]

export const h5TopPages = [
  { name: '仙之大陆-落地页A', visits: 5230, rate: 85 },
  { name: '仙之大陆-落地页B', visits: 4120, rate: 72 },
  { name: '仙之大陆-落地页C', visits: 3560, rate: 65 },
  { name: '仙之大陆-落地页D', visits: 2890, rate: 52 },
  { name: '仙之大陆-落地页E', visits: 2340, rate: 42 },
]

export const adDashboardTable = generateTableData(10, [
  { key: 'adGroup', generator: (i) => `广告组-${String(i).padStart(3, '0')}` },
  { key: 'channel', generator: () => pick(channels) },
  { key: 'cost', generator: () => formatMoney(random(2000, 30000)) },
  { key: 'impressions', generator: () => random(10000, 150000).toLocaleString() },
  { key: 'clicks', generator: () => random(400, 6000).toLocaleString() },
  { key: 'ctr', generator: () => (random(15, 80) / 10).toFixed(2) + '%' },
  { key: 'conversions', generator: () => random(20, 400) },
  { key: 'recharge', generator: () => formatMoney(random(3000, 40000)) },
  { key: 'roi', generator: () => (random(40, 120) / 100).toFixed(2) },
  { key: 'status', generator: () => pick(statuses) },
])

export const acquisitionStats = [
  { label: '新增用户', value: '1,256', diff: 15.2, icon: '👥', iconColor: 'blue' as const },
  { label: '活跃用户', value: '8,560', diff: 8.6, icon: '⚡', iconColor: 'green' as const },
  { label: '付费用户', value: '423', diff: 5.3, icon: '💳', iconColor: 'orange' as const },
  { label: '付费率', value: '33.7%', diff: 2.1, icon: '📊', iconColor: 'purple' as const },
]

export const acquisitionByChannel = [
  { name: 'TikTok', value: 580 },
  { name: 'Facebook', value: 320 },
  { name: 'Google', value: 210 },
  { name: '其他', value: 146 },
]

export const acquisitionTrend = {
  dates: ['06-25', '06-26', '06-27', '06-28', '06-29', '06-30', '07-01'],
  values: [850, 1020, 1180, 960, 1320, 1450, 1256],
}

export const roiTable = generateTableData(10, [
  { key: 'date', generator: () => randomDate().slice(0, 10) },
  { key: 'drama', generator: (i) => `短剧-${String(i).padStart(2, '0')}` },
  { key: 'cost', generator: () => formatMoney(random(3000, 30000)) },
  { key: 'recharge', generator: () => formatMoney(random(2000, 40000)) },
  { key: 'roi', generator: () => (random(40, 150) / 100).toFixed(2) },
  { key: 'payback', generator: () => random(3, 30) + '天' },
  { key: 'profit', generator: () => formatMoney(random(-5000, 20000)) },
  { key: 'status', generator: () => pick(['盈利', '亏损', '持平']) },
])

export const materialStatsTable = generateTableData(10, [
  { key: 'material', generator: (i) => `素材-${String(i).padStart(4, '0')}` },
  { key: 'type', generator: () => pick(['视频', '图片', '文案']) },
  { key: 'channel', generator: () => pick(channels) },
  { key: 'cost', generator: () => formatMoney(random(1000, 20000)) },
  { key: 'impressions', generator: () => random(5000, 80000).toLocaleString() },
  { key: 'clicks', generator: () => random(200, 4000).toLocaleString() },
  { key: 'ctr', generator: () => (random(15, 80) / 10).toFixed(2) + '%' },
  { key: 'conversions', generator: () => random(10, 300) },
  { key: 'roi', generator: () => (random(40, 150) / 100).toFixed(2) },
  { key: 'status', generator: () => pick(['使用中', '已停用', '审核中']) },
])

export const rechargeTemplateTable = generateTableData(8, [
  { key: 'name', generator: (i) => `充值模板-${i}` },
  { key: 'amount', generator: () => ['¥9.9', '¥19.9', '¥39.9', '¥68', '¥128', '¥298'][random(0, 5)] },
  { key: 'bonus', generator: () => '赠送' + random(0, 50) + '币' },
  { key: 'drama', generator: (i) => `短剧-${String(random(1, 20)).padStart(2, '0')}` },
  { key: 'channel', generator: () => pick(channels) },
  { key: 'status', generator: () => pick(['启用', '停用']) },
  { key: 'createTime', generator: () => randomDate() },
])

export const linkDetectTable = generateTableData(10, [
  { key: 'name', generator: (i) => `检测链接-${i}` },
  { key: 'url', generator: (i) => `https://example.com/page-${i}` },
  { key: 'channel', generator: () => pick(channels) },
  { key: 'adGroup', generator: (i) => `广告组-${String(i).padStart(3, '0')}` },
  { key: 'status', generator: () => pick(['正常', '异常', '检测中']) },
  { key: 'lastCheck', generator: () => randomDate() },
  { key: 'responseTime', generator: () => random(50, 500) + 'ms' },
])

export const batchDeliveryTable = generateTableData(10, [
  { key: 'batch', generator: (i) => `批次-${String(i).padStart(3, '0')}` },
  { key: 'drama', generator: (i) => `短剧-${String(random(1, 20)).padStart(2, '0')}` },
  { key: 'channel', generator: () => pick(channels) },
  { key: 'count', generator: () => random(5, 50) + '条' },
  { key: 'cost', generator: () => formatMoney(random(5000, 50000)) },
  { key: 'status', generator: () => pick(['进行中', '已完成', '已停止']) },
  { key: 'createTime', generator: () => randomDate() },
])

export const autoRulesTable = generateTableData(10, [
  { key: 'name', generator: (i) => `自动规则-${i}` },
  { key: 'condition', generator: () => pick(['ROI<50%', '消耗>¥5000', 'CPC>¥1.5', '转化<10']) },
  { key: 'action', generator: () => pick(['暂停广告组', '提高预算', '降低出价', '通知']) },
  { key: 'scope', generator: () => pick(['全部', 'TikTok', 'Facebook']) },
  { key: 'status', generator: () => pick(['运行中', '已暂停']) },
  { key: 'triggerCount', generator: () => random(1, 50) },
  { key: 'lastTrigger', generator: () => randomDate() },
])

export const rechargeDetailTable = generateTableData(10, [
  { key: 'orderNo', generator: (i) => 'R' + String(i).padStart(8, '0') },
  { key: 'account', generator: () => 'A' + String(random(1, 20)).padStart(4, '0') },
  { key: 'amount', generator: () => formatMoney(random(500, 50000)) },
  { key: 'channel', generator: () => pick(['支付宝', '微信', '银行转账', 'TikTok']) },
  { key: 'type', generator: () => pick(['充值', '退款', '调整']) },
  { key: 'status', generator: () => pick(['成功', '处理中', '失败']) },
  { key: 'operator', generator: () => randomName() },
  { key: 'time', generator: () => randomDate() },
])

export const accountListTable = generateTableData(10, [
  { key: 'account', generator: (i) => 'A' + String(i).padStart(4, '0') },
  { key: 'name', generator: (i) => `广告账户-${i}` },
  { key: 'channel', generator: () => pick(channels) },
  { key: 'balance', generator: () => formatMoney(random(1000, 100000)) },
  { key: 'todayCost', generator: () => formatMoney(random(500, 10000)) },
  { key: 'todayRecharge', generator: () => formatMoney(random(0, 20000)) },
  { key: 'status', generator: () => pick(['正常', '冻结', '审核中']) },
  { key: 'owner', generator: () => randomName() },
])
