import { generateTableData, random, randomDate, pick, randomName } from './utils'

export const roleTable = generateTableData(8, [
  { key: 'name', generator: (i) => pick(['超级管理员', '运营', '销售', '审计', '客服', '财务']) + (i > 5 ? `-${i}` : '') },
  { key: 'code', generator: (i) => 'ROLE_' + i },
  { key: 'desc', generator: () => pick(['负责系统全部管理', '负责投放运营', '负责销售跟进', '负责审计日志', '负责客户服务']) },
  { key: 'userCount', generator: () => random(1, 30) },
  { key: 'status', generator: () => pick(['启用', '停用']) },
  { key: 'createTime', generator: () => randomDate() },
])

export const deptTree = [
  {
    id: 1, name: '仙之大陆', icon: '🏢',
    children: [
      { id: 11, name: '运营部', icon: '📊', children: [
        { id: 111, name: '投放组', icon: '📈' },
        { id: 112, name: '内容组', icon: '🎬' },
      ]},
      { id: 12, name: '技术部', icon: '💻', children: [
        { id: 121, name: '前端组', icon: '🌐' },
        { id: 122, name: '后端组', icon: '⚙️' },
      ]},
      { id: 13, name: '财务部', icon: '💰' },
      { id: 14, name: '客服部', icon: '🎧' },
    ],
  },
]

export const menuManagementTable = [
  { id: 1, name: '首页', type: '目录', icon: '🏠', path: '/', sort: 1, status: '启用' },
  { id: 2, name: '工作台', type: '菜单', icon: '📊', path: '/dashboard', sort: 1, status: '启用', parent: '首页' },
  { id: 3, name: '投放系统', type: '目录', icon: '📈', path: '/delivery', sort: 2, status: '启用' },
  { id: 4, name: '每日消耗', type: '菜单', icon: '💵', path: '/delivery/daily-cost', sort: 1, status: '启用', parent: '投放系统' },
  { id: 5, name: '系统管理', type: '目录', icon: '⚙️', path: '/system', sort: 6, status: '启用' },
  { id: 6, name: '基础配置', type: '目录', icon: '🔧', path: '/basic', sort: 7, status: '启用' },
]

export const dictManagementTable = generateTableData(8, [
  { key: 'name', generator: (i) => pick(['订单状态', '支付渠道', '广告渠道', '短剧分类', '用户等级', '素材类型']) + (i > 5 ? `-${i}` : '') },
  { key: 'code', generator: (i) => 'dict_' + i },
  { key: 'desc', generator: () => pick(['订单状态字典', '支付渠道字典', '广告渠道字典', '短剧分类字典']) },
  { key: 'itemCount', generator: () => random(3, 15) },
  { key: 'status', generator: () => pick(['启用', '停用']) },
  { key: 'updateTime', generator: () => randomDate() },
])

export const paramSettingsTable = generateTableData(8, [
  { key: 'name', generator: (i) => pick(['系统名称', '系统Logo', '默认密码', '会话超时', '上传限制', '缓存时间']) + (i > 5 ? `-${i}` : '') },
  { key: 'key', generator: (i) => 'sys.param.' + i },
  { key: 'value', generator: () => pick(['仙之大陆', '30', '1024', '3600', '86400']) },
  { key: 'desc', generator: () => pick(['系统基础配置', '会话相关', '上传相关']) },
  { key: 'updateTime', generator: () => randomDate() },
])

export const operationLogTable = generateTableData(12, [
  { key: 'operator', generator: () => randomName() },
  { key: 'module', generator: () => pick(['用户管理', '投放管理', '短剧管理', '订单管理', '系统管理']) },
  { key: 'action', generator: () => pick(['新增', '修改', '删除', '导出', '审核']) },
  { key: 'target', generator: (i) => `对象-${i}` },
  { key: 'ip', generator: () => `192.168.${random(1, 254)}.${random(1, 254)}` },
  { key: 'status', generator: () => pick(['成功', '失败']) },
  { key: 'time', generator: () => randomDate() },
])

export const loginLogTable = generateTableData(12, [
  { key: 'user', generator: () => randomName() },
  { key: 'ip', generator: () => `192.168.${random(1, 254)}.${random(1, 254)}` },
  { key: 'location', generator: () => pick(['北京', '上海', '广州', '深圳', '杭州', '成都']) },
  { key: 'browser', generator: () => pick(['Chrome', 'Firefox', 'Safari', 'Edge']) },
  { key: 'os', generator: () => pick(['Windows 10', 'macOS', 'iOS', 'Android']) },
  { key: 'status', generator: () => pick(['成功', '失败']) },
  { key: 'time', generator: () => randomDate() },
])

export const mediaChannelTable = generateTableData(8, [
  { key: 'name', generator: () => pick(['TikTok', 'Facebook', 'Google', 'Twitter', 'Unity', 'AppLovin']) },
  { key: 'code', generator: (i) => 'CH_' + i },
  { key: 'region', generator: () => pick(['全球', '北美', '欧洲', '东南亚', '中东']) },
  { key: 'currency', generator: () => pick(['USD', 'EUR', 'CNY']) },
  { key: 'accountCount', generator: () => random(1, 20) },
  { key: 'status', generator: () => pick(['启用', '停用']) },
  { key: 'createTime', generator: () => randomDate() },
])

export const languageTable = generateTableData(8, [
  { key: 'name', generator: () => pick(['简体中文', '繁体中文', '英语', '日语', '韩语', '泰语', '越南语', '印尼语']) },
  { key: 'code', generator: (i) => pick(['zh-CN', 'zh-TW', 'en', 'ja', 'ko', 'th', 'vi', 'id'])[i - 1] || 'lang' + i },
  { key: 'progress', generator: () => random(60, 100) + '%' },
  { key: 'default', generator: () => pick(['是', '否']) },
  { key: 'status', generator: () => pick(['启用', '停用']) },
])

export const timezoneTable = generateTableData(8, [
  { key: 'name', generator: () => pick(['UTC+8 北京', 'UTC+0 伦敦', 'UTC-5 纽约', 'UTC-8 洛杉矶', 'UTC+9 东京', 'UTC+1 巴黎', 'UTC+7 曼谷', 'UTC+10 悉尼']) },
  { key: 'code', generator: (i) => 'TZ' + i },
  { key: 'offset', generator: () => 'UTC' + pick(['+8', '+0', '-5', '-8', '+9', '+1', '+7', '+10']) },
  { key: 'default', generator: () => pick(['是', '否']) },
  { key: 'status', generator: () => pick(['启用', '停用']) },
])

export const adAccountTable = generateTableData(10, [
  { key: 'account', generator: (i) => 'A' + String(i).padStart(4, '0') },
  { key: 'name', generator: (i) => `广告账户-${i}` },
  { key: 'channel', generator: () => pick(['TikTok', 'Facebook', 'Google', 'Twitter']) },
  { key: 'balance', generator: () => '¥' + random(1000, 100000).toLocaleString() },
  { key: 'todayCost', generator: () => '¥' + random(500, 10000).toLocaleString() },
  { key: 'todayRecharge', generator: () => '¥' + random(0, 20000).toLocaleString() },
  { key: 'status', generator: () => pick(['正常', '冻结', '审核中']) },
  { key: 'owner', generator: () => randomName() },
  { key: 'createTime', generator: () => randomDate() },
])
