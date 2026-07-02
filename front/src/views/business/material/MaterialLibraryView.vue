<template>
  <div class="biz-page">
    <PageHeader title="素材库" desc="管理广告素材资源" />
    <BaseTabs v-model="tab" :tabs="[{ key: 'list', label: '素材列表' }, { key: 'batch', label: '批量管理' }, { key: 'card', label: '卡片视图' }]" />

    <FilterBar v-if="tab !== 'card'">
      <el-form-item label="类型"><el-select v-model="type" placeholder="全部" style="width:140px"><el-option label="视频" value="video" /><el-option label="图片" value="image" /><el-option label="文案" value="text" /></el-select></el-form-item>
      <el-form-item label="渠道"><el-select v-model="channel" placeholder="全部" style="width:140px"><el-option v-for="c in channels" :key="c" :label="c" :value="c" /></el-select></el-form-item>
      <el-form-item label="标签"><el-select v-model="tag" placeholder="全部" style="width:140px"><el-option v-for="t in tags" :key="t" :label="t" :value="t" /></el-select></el-form-item>
      <template #actions>
        <el-button>重置</el-button>
        <el-button type="primary">查询</el-button>
      </template>
    </FilterBar>

    <BaseTable v-if="tab === 'list'" :data="table" :total="156" :page="1" :page-size="10" @change="onPageChange">
      <template #actions>
        <el-button>导入</el-button>
        <el-button>批量删除</el-button>
        <el-button type="primary" @click="openAdd">上传素材</el-button>
      </template>
      <el-table-column type="selection" width="48" />
      <el-table-column prop="name" label="素材名" />
      <el-table-column prop="type" label="类型" />
      <el-table-column prop="channel" label="渠道" />
      <el-table-column prop="size" label="大小" />
      <el-table-column prop="duration" label="时长" />
      <el-table-column prop="tags" label="标签" />
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '使用中' ? 'green' : row.status === '审核中' ? 'orange' : 'default'">{{ row.status }}</StatusTag></template></el-table-column>
      <el-table-column prop="uploadTime" label="上传时间" />
      <el-table-column label="操作" width="160"><template #default><el-button text type="primary">预览</el-button><el-button text type="danger">删除</el-button></template></el-table-column>
    </BaseTable>

    <BaseTable v-else-if="tab === 'batch'" :data="batchTable" :total="48" :page="1" :page-size="10" @change="onPageChange">
      <template #actions><el-button type="primary">新建批次</el-button></template>
      <el-table-column prop="batch" label="批次号" />
      <el-table-column prop="count" label="素材数" />
      <el-table-column prop="channel" label="渠道" />
      <el-table-column prop="drama" label="短剧" />
      <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '投放中' ? 'green' : row.status === '已结束' ? 'default' : 'orange'">{{ row.status }}</StatusTag></template></el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
    </BaseTable>

    <div v-else>
      <div class="biz-flex-between biz-mb-16">
        <div></div>
        <el-button type="primary" @click="openAdd">上传素材</el-button>
      </div>
      <div class="biz-material-grid">
        <div v-for="c in cards" :key="c.id" class="biz-material-card" @click="goDetail(c)">
          <div class="biz-material-cover">{{ c.type === '视频' ? '🎬' : '🖼️' }}</div>
          <div class="biz-material-info">
            <div class="biz-material-title">{{ c.name }}</div>
            <div class="biz-material-stats"><span>曝光 {{ c.stats }}</span><span>CTR {{ c.ctr }}</span></div>
          </div>
        </div>
      </div>
    </div>

    <BaseModal v-model="addVisible" title="上传素材" width="480px">
      <el-form label-position="top">
        <el-form-item label="素材类型"><el-select class="biz-w-full"><el-option label="视频" value="video" /><el-option label="图片" value="image" /></el-select></el-form-item>
        <el-form-item label="渠道"><el-select class="biz-w-full"><el-option v-for="c in channels" :key="c" :label="c" :value="c" /></el-select></el-form-item>
        <el-form-item label="文件"><el-upload drag><el-icon class="el-icon--upload"><upload-filled /></el-icon><div class="el-upload__text">拖拽文件到此处，或<em>点击上传</em></div></el-upload></el-form-item>
        <el-form-item label="标签"><el-input placeholder="多个标签用逗号分隔" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="addVisible = false">上传</el-button>
      </template>
    </BaseModal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { UploadFilled } from '@element-plus/icons-vue'
import PageHeader from '../../../components/PageHeader.vue'
import BaseTabs from '../../../components/BaseTabs.vue'
import FilterBar from '../../../components/FilterBar.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'
import BaseModal from '../../../components/BaseModal.vue'
import { materialApi } from '../../../api/business'

const channels = ['TikTok', 'Facebook', 'Google', 'Twitter']
const tags = ['都市', '玄幻', '甜宠', '复仇', '穿越']
const router = useRouter()
const tab = ref('list')
const type = ref('')
const channel = ref('')
const tag = ref('')
const table = ref<any[]>([])
const batchTable = ref<any[]>([])
const cards = ref<any[]>([])
const addVisible = ref(false)

onMounted(async () => {
  table.value = await materialApi.library()
  batchTable.value = await materialApi.batch()
  cards.value = await materialApi.cards()
})
function onPageChange() {}
function openAdd() { addVisible.value = true }
function goDetail(c: any) { router.push(`/material/detail/${c.id}`) }
</script>
