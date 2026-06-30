<template>
  <el-sub-menu v-if="hasChildren" :index="node.label">
    <template #title>
      <el-icon v-if="node.icon"><component :is="node.icon" /></el-icon>
      <span>{{ node.label }}</span>
    </template>
    <MenuNode v-for="child in node.children" :key="child.label" :node="child" />
  </el-sub-menu>

  <el-menu-item v-else :index="node.path" :disabled="isDisabled" @click="handleClick">
    <el-icon v-if="node.icon"><component :is="node.icon" /></el-icon>
    <template #title>
      <span>{{ node.label }}</span>
      <small v-if="isDisabled" class="menu-disabled-hint">无权限</small>
    </template>
  </el-menu-item>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAdminSessionStore, type RoleCode } from '../stores/adminSession'

export interface MenuItem {
  label: string
  path?: string
  icon?: any
  roles: RoleCode[]
  children?: MenuItem[]
}

const props = defineProps<{ node: MenuItem }>()

const router = useRouter()
const session = useAdminSessionStore()

const hasChildren = computed(() => (props.node.children?.length ?? 0) > 0)
const isDisabled = computed(() => !session.hasAnyRole(props.node.roles))

function handleClick() {
  if (isDisabled.value) {
    ElMessage.warning('无权限访问该模块')
    return
  }
  if (props.node.path) {
    router.push(props.node.path)
  }
}
</script>
