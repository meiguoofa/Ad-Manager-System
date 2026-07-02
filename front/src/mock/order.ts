import { generateTableData, random, randomDate, formatMoney, pick, randomName } from './utils'

export const orderStats = [
  { label: '今日订单', value: '1,256', diff: 8.5, icon: '📦', iconColor: 'blue' as const },
  { label: '今日金额', value: '¥96,320', diff: 12.3, icon: '💰', iconColor: 'green' as const },
  { label: '待处理', value: '32', diff: -5.2, icon: '⏳', iconColor: 'orange' as const },
  { label: '退款中', value: '8', diff: 1.8, icon: '↩️', iconColor: 'red' as const },
]

export const thirdPartyOrdersTable = generateTableData(10, [
  { key: 'orderNo', generator: (i) => 'TPO' + String(i).padStart(8, '0') },
  { key: 'user', generator: () => randomName() },
  { key: 'channel', generator: () => pick(['支付宝', '微信', 'PayPal', 'Stripe']) },
  { key: 'amount', generator: () => formatMoney(random(9, 298)) },
  { key: 'drama', generator: (i) => `短剧-${String(random(1, 20)).padStart(2, '0')}` },
  { key: 'episode', generator: () => `第${random(1, 80)}集` },
  { key: 'status', generator: () => pick(['成功', '处理中', '失败']) },
  { key: 'time', generator: () => randomDate() },
])

export const allOrdersTable = generateTableData(10, [
  { key: 'orderNo', generator: (i) => 'ORD' + String(i).padStart(8, '0') },
  { key: 'user', generator: () => randomName() },
  { key: 'type', generator: () => pick(['充值', '解锁', '会员', '打赏']) },
  { key: 'amount', generator: () => formatMoney(random(9, 298)) },
  { key: 'payMethod', generator: () => pick(['支付宝', '微信', '余额', 'PayPal']) },
  { key: 'drama', generator: (i) => `短剧-${String(random(1, 20)).padStart(2, '0')}` },
  { key: 'status', generator: () => pick(['已完成', '已取消', '已退款', '处理中']) },
  { key: 'time', generator: () => randomDate() },
])

export const orderDetail = {
  orderNo: 'ORD00001234',
  user: '张三',
  type: '充值',
  amount: '¥128.00',
  payMethod: '支付宝',
  status: '已完成',
  drama: '短剧-12',
  episode: '第15集',
  time: '2026-07-01 10:30:00',
  payTime: '2026-07-01 10:30:12',
  channel: '支付宝',
  tradeNo: '2026070110301234',
  ip: '192.168.1.100',
  device: 'iOS · iPhone 14',
}

export const refundOrdersTable = generateTableData(10, [
  { key: 'refundNo', generator: (i) => 'RFD' + String(i).padStart(8, '0') },
  { key: 'orderNo', generator: (i) => 'ORD' + String(i + 100).padStart(8, '0') },
  { key: 'user', generator: () => randomName() },
  { key: 'amount', generator: () => formatMoney(random(9, 298)) },
  { key: 'reason', generator: () => pick(['未观看', '误购', '内容问题', '其他']) },
  { key: 'status', generator: () => pick(['待审核', '已通过', '已拒绝', '已退款']) },
  { key: 'applyTime', generator: () => randomDate() },
])

export const refundDetail = {
  refundNo: 'RFD00001234',
  orderNo: 'ORD00000123',
  user: '李四',
  amount: '¥39.90',
  reason: '误购',
  desc: '用户表示误操作购买，希望退款。',
  status: '待审核',
  applyTime: '2026-07-01 09:15:00',
  operator: '王五',
  processRecords: [
    { time: '2026-07-01 09:15:00', action: '提交退款申请', operator: '李四' },
    { time: '2026-07-01 10:00:00', action: '客服初审通过', operator: '王五' },
    { time: '2026-07-01 10:30:00', action: '等待财务复核', operator: '系统' },
  ],
}
