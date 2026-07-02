import { generateTableData, random, randomDate, pick } from './utils'

export const materialLibraryTable = generateTableData(16, [
  { key: 'name', generator: (i) => `素材-${String(i).padStart(4, '0')}` },
  { key: 'type', generator: () => pick(['视频', '图片', '文案', '音频']) },
  { key: 'channel', generator: () => pick(['TikTok', 'Facebook', 'Google', 'Twitter']) },
  { key: 'size', generator: () => random(1, 50) + 'MB' },
  { key: 'duration', generator: () => random(5, 60) + 's' },
  { key: 'tags', generator: () => pick(['都市', '玄幻', '甜宠', '复仇', '穿越']) },
  { key: 'status', generator: () => pick(['使用中', '已停用', '审核中']) },
  { key: 'uploadTime', generator: () => randomDate() },
])

export const materialBatchTable = generateTableData(6, [
  { key: 'batch', generator: (i) => `批次-${i}` },
  { key: 'count', generator: () => random(5, 30) + '个素材' },
  { key: 'channel', generator: () => pick(['TikTok', 'Facebook', 'Google']) },
  { key: 'drama', generator: (i) => `短剧-${i}` },
  { key: 'status', generator: () => pick(['投放中', '已结束', '待投放']) },
  { key: 'createTime', generator: () => randomDate() },
])

export const materialCards = Array.from({ length: 12 }, (_, i) => ({
  id: i + 1,
  name: `素材-${String(i + 1).padStart(4, '0')}`,
  type: pick(['视频', '图片']),
  stats: random(1000, 50000).toLocaleString(),
  ctr: (random(15, 80) / 10).toFixed(2) + '%',
}))
