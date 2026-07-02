<template>
  <span class="biz-tag" :class="`biz-tag-${type}`"><slot /></span>
</template>

<script setup lang="ts">
defineProps<{ type: 'default' | 'blue' | 'green' | 'red' | 'orange' | 'purple' | 'cyan' }>()
</script>
