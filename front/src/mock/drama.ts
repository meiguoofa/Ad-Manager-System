import { generateTableData, random, randomDate, formatMoney, pick } from './utils'

export const dramaStatsOverview = [
  { label: '短剧数量', value: '128', diff: 5.2, icon: '🎬', iconColor: 'blue' as const },
  { label: '总播放量', value: '156.8万', diff: 12.3, icon: '▶️', iconColor: 'green' as const },
  { label: '总充值', value: '¥2,356,800', diff: 18.6, icon: '💰', iconColor: 'orange' as const },
  { label: '平均 ROI', value: '85.6%', diff: 3.2, icon: '📈', iconColor: 'cyan' as const },
]

export const dramaPlayTrend = {
  dates: ['06-25', '06-26', '06-27', '06-28', '06-29', '06-30', '07-01'],
  values: [12500, 15200, 18900, 16800, 21300, 24500, 22800],
}

export const dramaRechargeTrend = {
  dates: ['06-25', '06-26', '06-27', '06-28', '06-29', '06-30', '07-01'],
  values: [98000, 125000, 156000, 132000, 178000, 205000, 192000],
}

export const dramaChannelShare = [
  { name: 'TikTok', value: 55 },
  { name: 'Facebook', value: 25 },
  { name: 'Google', value: 12 },
  { name: '其他', value: 8 },
]

export const dramaRankTable = generateTableData(10, [
  { key: 'rank', generator: (i) => i },
  { key: 'drama', generator: (i) => `短剧-${String(i).padStart(2, '0')}` },
  { key: 'playCount', generator: () => random(50000, 500000).toLocaleString() },
  { key: 'recharge', generator: () => formatMoney(random(20000, 200000)) },
  { key: 'cost', generator: () => formatMoney(random(10000, 150000)) },
  { key: 'roi', generator: () => (random(50, 180) / 100).toFixed(2) },
  { key: 'newUsers', generator: () => random(500, 5000) },
  { key: 'payRate', generator: () => (random(20, 60) / 100).toFixed(2) + '%' },
])

export const episodeAnalysisTable = generateTableData(8, [
  { key: 'episode', generator: (i) => `第${i}集` },
  { key: 'playCount', generator: () => random(10000, 100000).toLocaleString() },
  { key: 'completionRate', generator: () => (random(40, 95) / 100).toFixed(2) + '%' },
  { key: 'payRate', generator: () => (random(10, 50) / 100).toFixed(2) + '%' },
  { key: 'recharge', generator: () => formatMoney(random(5000, 50000)) },
  { key: 'retention', generator: () => (random(30, 90) / 100).toFixed(2) + '%' },
  { key: 'conversion', generator: () => (random(5, 30) / 100).toFixed(2) + '%' },
])

export const episodeRetentionCurve = {
  episodes: ['第1集', '第3集', '第5集', '第7集', '第9集', '第11集', '第12集'],
  values: [100, 78, 62, 51, 43, 36, 30],
}

export const dramaManagementTable = generateTableData(12, [
  { key: 'drama', generator: (i) => `短剧-${String(i).padStart(2, '0')}` },
  { key: 'name', generator: (i) => `《仙之大陆-${i}》` },
  { key: 'category', generator: () => pick(['都市', '玄幻', '甜宠', '复仇', '穿越']) },
  { key: 'episodes', generator: () => random(12, 80) + '集' },
  { key: 'status', generator: () => pick(['连载中', '已完结', '待上线']) },
  { key: 'playCount', generator: () => random(10000, 500000).toLocaleString() },
  { key: 'recharge', generator: () => formatMoney(random(10000, 200000)) },
  { key: 'createTime', generator: () => randomDate() },
])

export const dramaDetailBase = {
  name: '《仙之大陆-1》',
  category: '玄幻',
  episodes: 80,
  status: '连载中',
  playCount: '128,560',
  recharge: '¥356,800',
  cost: '¥210,000',
  roi: '1.70',
  releaseDate: '2026-05-01',
  updateTime: '2026-07-01 10:30:00',
  desc: '一名都市青年意外穿越至仙侠世界，凭借现代智慧与机缘逐步崛起，揭开身世之谜并扭转乾坤的故事。',
}

export const dramaDetailScriptTable = generateTableData(10, [
  { key: 'episode', generator: (i) => `第${i}集` },
  { key: 'title', generator: (i) => `章节标题-${i}` },
  { key: 'duration', generator: () => random(60, 180) + '秒' },
  { key: 'unlock', generator: () => random(5, 30) + '币' },
  { key: 'status', generator: () => pick(['已发布', '待发布', '审核中']) },
  { key: 'updateTime', generator: () => randomDate() },
])

export const productLibraryTable = generateTableData(12, [
  { key: 'name', generator: (i) => `商品-${i}` },
  { key: 'sku', generator: (i) => 'SKU' + String(i).padStart(5, '0') },
  { key: 'category', generator: () => pick(['虚拟', '实物', '会员', '充值包']) },
  { key: 'price', generator: () => formatMoney(random(9, 298)) },
  { key: 'stock', generator: () => random(0, 9999) },
  { key: 'sales', generator: () => random(0, 5000) },
  { key: 'status', generator: () => pick(['上架', '下架', '审核中']) },
])

export const dramaPriceTable = generateTableData(10, [
  { key: 'name', generator: (i) => `充值包-${i}` },
  { key: 'price', generator: () => formatMoney(random(9, 298)) },
  { key: 'coins', generator: () => random(10, 3000) + '币' },
  { key: 'bonus', generator: () => '赠' + random(0, 500) + '币' },
  { key: 'firstBonus', generator: () => '首充赠' + random(50, 500) + '币' },
  { key: 'status', generator: () => pick(['启用', '停用']) },
])

export const dramaUpdateTable = generateTableData(10, [
  { key: 'version', generator: (i) => 'v1.' + i },
  { key: 'episode', generator: (i) => `第${i}集` },
  { key: 'operator', generator: () => pick(['张三', '李四', '王五']) },
  { key: 'type', generator: () => pick(['上线', '修改', '下线']) },
  { key: 'note', generator: () => '更新说明' },
  { key: 'time', generator: () => randomDate() },
])
