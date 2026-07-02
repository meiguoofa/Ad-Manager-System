<template>
  <div class="biz-card">
    <div v-if="title || $slots.actions" class="biz-card-header">
      <div class="biz-card-title">{{ title }}</div>
      <div v-if="$slots.actions"><slot name="actions" /></div>
    </div>
    <div class="biz-card-body">
      <div ref="el" class="biz-chart-canvas" :style="{ height: height + 'px' }" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps<{
  title?: string
  option: echarts.EChartsOption
  height?: number
}>()

const el = ref<HTMLElement>()
let chart: echarts.ECharts | null = null

function render() {
  if (!chart || !props.option) return
  chart.setOption(props.option, true)
}

function resize() { chart?.resize() }

onMounted(() => {
  if (!el.value) return
  chart = echarts.init(el.value)
  render()
  window.addEventListener('resize', resize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
  chart?.dispose()
  chart = null
})

watch(() => props.option, render, { deep: true })
</script>
