<template>
  <section class="panel page-panel">
    <div class="section-heading">
      <p>Module</p>
      <h2>{{ title }}</h2>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const title = computed(() => String(route.meta.title || '模块'))
</script>
