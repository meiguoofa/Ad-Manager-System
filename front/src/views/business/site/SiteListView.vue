<template>
  <div class="biz-page">
    <PageHeader title="站点列表" desc="管理独立站站点" />

    <FilterBar>
      <el-form-item label="状态"><el-select v-model="status" placeholder="全部" style="width:140px"><el-option label="运行中" value="running" /><el-option label="已停用" value="stopped" /></el-select></el-form-item>
      <el-form-item label="名称"><el-input v-model="name" placeholder="请输入站点" style="width:180px" /></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <div class="biz-flex-between biz-mb-16">
      <div></div>
      <el-button type="primary" @click="openAdd">新建站点</el-button>
    </div>

    <div class="biz-site-grid">
      <div v-for="s in list" :key="s.id" class="biz-site-card" @click="goDetail(s)">
        <div class="biz-site-header">
          <div class="biz-site-icon">🌐</div>
          <div>
            <div class="biz-site-name">{{ s.name }}</div>
            <div class="biz-site-domain">{{ s.domain }}</div>
          </div>
          <div style="margin-left:auto"><StatusTag :type="s.status === '运行中' ? 'green' : s.status === '维护中' ? 'orange' : 'default'">{{ s.status }}</StatusTag></div>
        </div>
        <div class="biz-site-stats">
          <div class="biz-site-stat"><div class="biz-site-stat-value">{{ s.visits.toLocaleString() }}</div><div class="biz-site-stat-label">访问数</div></div>
          <div class="biz-site-stat"><div class="biz-site-stat-value">{{ s.recharge }}</div><div class="biz-site-stat-label">充值</div></div>
          <div class="biz-site-stat"><div class="biz-site-stat-value">{{ s.pages }}</div><div class="biz-site-stat-label">页面数</div></div>
        </div>
      </div>
    </div>

    <BaseModal v-model="addVisible" title="新建站点" width="560px">
      <el-form label-position="top">
        <el-form-item label="站点名称"><el-input /></el-form-item>
        <el-form-item label="主域名"><el-input placeholder="example.com" /></el-form-item>
        <el-form-item label="描述"><el-input type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="addVisible = false">创建</el-button>
      </template>
    </BaseModal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '../../../components/PageHeader.vue'
import FilterBar from '../../../components/FilterBar.vue'
import StatusTag from '../../../components/StatusTag.vue'
import BaseModal from '../../../components/BaseModal.vue'
import { siteApi } from '../../../api/business'

const router = useRouter()
const status = ref('')
const name = ref('')
const list = ref<any[]>([])
const addVisible = ref(false)

onMounted(async () => { list.value = await siteApi.list() })
function openAdd() { addVisible.value = true }
function goDetail(s: any) { router.push(`/site/detail/${s.id}`) }
</script>
