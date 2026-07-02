<template>
  <div class="biz-filter-bar">
    <slot />
    <div v-if="$slots.actions" class="biz-filter-actions">
      <slot name="actions" />
    </div>
  </div>
</template>
