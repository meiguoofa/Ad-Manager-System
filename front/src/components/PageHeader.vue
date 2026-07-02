<template>
  <div class="biz-page">
    <div class="biz-page-title">
      <span v-if="back" class="biz-back-btn" @click="$router.back()">‹ 返回</span>
      <span>{{ title }}</span>
      <slot name="extra" />
    </div>
    <p v-if="desc" class="biz-page-desc">{{ desc }}</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{ title: string; desc?: string; back?: boolean }>()
</script>
