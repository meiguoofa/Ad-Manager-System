<template>
  <div class="biz-stat-card">
    <div class="biz-stat-label">{{ label }}</div>
    <div class="biz-stat-value">{{ value }}</div>
    <div v-if="diff !== undefined" class="biz-stat-diff" :class="diff >= 0 ? 'up' : 'down'">
      <span>{{ diff >= 0 ? '↑' : '↓' }}</span>
      <span>{{ Math.abs(diff) }}%</span>
      <span v-if="diffLabel" class="biz-text-muted" style="margin-left: 4px">{{ diffLabel }}</span>
    </div>
    <div v-if="icon" class="biz-stat-icon" :class="iconColor || 'blue'">{{ icon }}</div>
    <div v-if="trend" class="biz-stat-trend">
      <span v-for="(t, i) in trend" :key="i" :class="{ active: t }" />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  label: string
  value: string | number
  diff?: number
  diffLabel?: string
  icon?: string
  iconColor?: 'blue' | 'green' | 'orange' | 'red' | 'purple' | 'cyan'
  trend?: boolean[]
}>()
</script>
