<template>
  <div class="biz-page">
    <PageHeader title="模板中心" desc="精美的页面模板库" />

    <FilterBar>
      <el-form-item label="分类"><el-select v-model="category" placeholder="全部" style="width:160px"><el-option v-for="c in cats" :key="c" :label="c" :value="c" /></el-select></el-form-item>
      <el-form-item label="名称"><el-input v-model="name" placeholder="请输入名称" style="width:180px" /></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <div class="biz-template-grid">
      <div v-for="t in list" :key="t.id" class="biz-template-card" @click="useTemplate(t)">
        <div class="biz-template-preview"></div>
        <div class="biz-template-info">
          <div class="biz-template-name">{{ t.name }}</div>
          <div class="biz-template-desc">{{ t.desc }} · {{ t.category }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '../../../components/PageHeader.vue'
import FilterBar from '../../../components/FilterBar.vue'
import { siteApi } from '../../../api/business'

const router = useRouter()
const cats = ['短剧', '电商', '充值', '通用']
const category = ref('')
const name = ref('')
const list = ref<any[]>([])

onMounted(async () => { list.value = await siteApi.templates() })
function useTemplate(t: any) {
  router.push(`/site/page-editor/${t.id}`)
}
</script>
