/* HTML 原型数据生成工具移植 */

export function random(min: number, max: number): number {
  return Math.floor(Math.random() * (max - min + 1)) + min
}

export function formatMoney(num: number): string {
  return '¥' + num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

export function formatNumber(num: number): string {
  if (num >= 10000) return (num / 10000).toFixed(2) + '万'
  return num.toLocaleString('zh-CN')
}

export function randomDate(): string {
  const d = new Date()
  d.setDate(d.getDate() - random(0, 30))
  d.setHours(random(8, 20), random(0, 59), random(0, 59))
  return d.toISOString().slice(0, 19).replace('T', ' ')
}

const surnames = ['张', '王', '李', '赵', '刘', '陈', '杨', '黄', '周', '吴', '徐', '孙', '马', '朱', '胡', '郭', '何', '高', '林', '罗']
const nameChars = ['伟', '芳', '娜', '敏', '静', '丽', '强', '磊', '军', '洋', '勇', '艳', '杰', '娟', '涛', '明', '超', '秀英', '霞', '平']

export function randomName(): string {
  return surnames[random(0, surnames.length - 1)] + nameChars[random(0, nameChars.length - 1)]
}

export interface FieldDef {
  key: string
  generator?: (i: number) => any
}

export function generateTableData(count: number, fields: (string | FieldDef)[]): any[] {
  const data: any[] = []
  for (let i = 1; i <= count; i++) {
    const row: any = { id: i }
    fields.forEach((field) => {
      if (typeof field === 'string') {
        row[field] = `${field}_${i}`
      } else {
        row[field.key] = field.generator ? field.generator(i) : `${field.key}_${i}`
      }
    })
    data.push(row)
  }
  return data
}

export function delay<T>(data: T, ms = 50): Promise<T> {
  return new Promise((resolve) => setTimeout(() => resolve(data), ms))
}

export function pick<T>(arr: T[]): T {
  return arr[random(0, arr.length - 1)]
}

export const channels = ['TikTok', 'Facebook', 'Google', 'Twitter', 'Unity', 'AppLovin']
export const statuses = ['活跃', '暂停', '已结束', '审核中']
