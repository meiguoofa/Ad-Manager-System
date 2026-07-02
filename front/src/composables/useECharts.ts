import * as echarts from 'echarts'

export const PRIMARY = '#1890ff'
export const SUCCESS = '#52c41a'
export const WARNING = '#faad14'
export const DANGER = '#f5222d'
export const PURPLE = '#722ed1'
export const CYAN = '#13c2c2'

export const chartPalette = [PRIMARY, SUCCESS, WARNING, DANGER, PURPLE, CYAN]

export function lineOption(opts: {
  x: string[]
  series: { name: string; data: number[]; color?: string }[]
  area?: boolean
}): echarts.EChartsOption {
  return {
    tooltip: { trigger: 'axis' },
    legend: { data: opts.series.map((s) => s.name), top: 0, right: 0, textStyle: { color: '#595959' } },
    grid: { left: 40, right: 16, top: 36, bottom: 28 },
    xAxis: { type: 'category', data: opts.x, boundaryGap: false, axisLine: { lineStyle: { color: '#e4e7ed' } }, axisLabel: { color: '#8c8c8c' } },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: '#f0f0f0' } }, axisLabel: { color: '#8c8c8c' } },
    series: opts.series.map((s) => ({
      name: s.name,
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      data: s.data,
      itemStyle: { color: s.color || PRIMARY },
      lineStyle: { width: 2, color: s.color || PRIMARY },
      areaStyle: opts.area
        ? {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: (s.color || PRIMARY) + '66' },
              { offset: 1, color: (s.color || PRIMARY) + '05' },
            ]),
          }
        : undefined,
    })),
  }
}

export function barOption(opts: {
  x: string[]
  series: { name: string; data: number[]; color?: string }[]
  horizontal?: boolean
}): echarts.EChartsOption {
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { data: opts.series.map((s) => s.name), top: 0, right: 0, textStyle: { color: '#595959' } },
    grid: { left: 48, right: 16, top: 36, bottom: 28 },
    xAxis: opts.horizontal
      ? { type: 'value', splitLine: { lineStyle: { color: '#f0f0f0' } }, axisLabel: { color: '#8c8c8c' } }
      : { type: 'category', data: opts.x, axisLine: { lineStyle: { color: '#e4e7ed' } }, axisLabel: { color: '#8c8c8c' } },
    yAxis: opts.horizontal
      ? { type: 'category', data: opts.x, axisLine: { lineStyle: { color: '#e4e7ed' } }, axisLabel: { color: '#8c8c8c' } }
      : { type: 'value', splitLine: { lineStyle: { color: '#f0f0f0' } }, axisLabel: { color: '#8c8c8c' } },
    series: opts.series.map((s) => ({
      name: s.name,
      type: 'bar',
      data: s.data,
      barWidth: '40%',
      itemStyle: {
        borderRadius: opts.horizontal ? [0, 4, 4, 0] : [4, 4, 0, 0],
        color: s.color
          ? new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: s.color },
              { offset: 1, color: s.color + 'AA' },
            ])
          : new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#40a9ff' },
              { offset: 1, color: '#1890ff' },
            ]),
      },
    })),
  }
}

export function pieOption(opts: { data: { name: string; value: number }[]; doughnut?: boolean }): echarts.EChartsOption {
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', right: 0, top: 'middle', textStyle: { color: '#595959' } },
    series: [
      {
        type: 'pie',
        radius: opts.doughnut ? ['55%', '75%'] : '70%',
        center: ['38%', '50%'],
        data: opts.data,
        itemStyle: { borderColor: '#fff', borderWidth: 2 },
        label: { show: !opts.doughnut, color: '#595959' },
        color: chartPalette,
      },
    ],
  }
}

export { echarts }
