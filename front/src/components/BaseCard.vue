<template>
  <div class="biz-card">
    <div v-if="title || $slots.header || $slots.actions" class="biz-card-header">
      <div class="biz-card-title">
        <slot name="header">{{ title }}</slot>
      </div>
      <div v-if="$slots.actions" class="biz-card-actions">
        <slot name="actions" />
      </div>
    </div>
    <div class="biz-card-body" :class="{ 'no-padding': noPadding }">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ title?: string; noPadding?: boolean }>()
</script>
