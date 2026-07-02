<template>
  <div class="biz-table-container">
    <div v-if="title || $slots.actions" class="biz-table-header">
      <div class="biz-table-title">{{ title }}</div>
      <div v-if="$slots.actions" class="biz-table-actions"><slot name="actions" /></div>
    </div>
    <div class="biz-table-wrapper">
      <el-table :data="data" v-bind="$attrs" stripe>
        <slot />
      </el-table>
    </div>
    <div v-if="showPagination" class="biz-flex biz-flex-between" style="padding: 16px 24px;">
      <span class="biz-text-muted" style="font-size: 13px;">共 {{ total }} 条记录</span>
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="sizes, prev, pager, next, jumper"
        @change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = withDefaults(defineProps<{
  data: any[]
  title?: string
  showPagination?: boolean
  total?: number
  page?: number
  pageSize?: number
}>(), {
  showPagination: true,
  total: 0,
  page: 1,
  pageSize: 10,
})

const emit = defineEmits<{ (e: 'change', page: number, size: number): void }>()

const page = ref(props.page)
const size = ref(props.pageSize)

watch(() => props.page, v => { page.value = v })
watch(() => props.pageSize, v => { size.value = v })

function onPageChange() { emit('change', page.value, size.value) }
</script>
