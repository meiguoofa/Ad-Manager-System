<template>
  <div>
    <div class="biz-tabs">
      <div
        v-for="tab in tabs"
        :key="tab.key"
        class="biz-tab-item"
        :class="{ active: tab.key === modelValue }"
        @click="$emit('update:modelValue', tab.key)"
      >
        {{ tab.label }}
        <span v-if="tab.badge" class="biz-tab-badge">{{ tab.badge }}</span>
      </div>
    </div>
    <div><slot /></div>
  </div>
</template>

<script setup lang="ts">
export interface TabItem { key: string; label: string; badge?: number | string }
defineProps<{ tabs: TabItem[]; modelValue: string }>()
defineEmits<{ (e: 'update:modelValue', v: string): void }>()
</script>
