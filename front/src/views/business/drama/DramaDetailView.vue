<template>
  <div class="biz-page">
    <PageHeader title="短剧详情" back />
    <BaseTabs v-model="tab" :tabs="[{ key: 'base', label: '基本信息' }, { key: 'script', label: '剧集信息' }, { key: 'price', label: '充值配置' }, { key: 'update', label: '更新日志' }]" />

    <BaseCard v-if="tab === 'base'" title="基本信息">
      <div class="biz-info-grid cols-2">
        <div class="biz-info-item"><span class="biz-info-label">短剧名称</span><span class="biz-info-value">{{ base.name }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">分类</span><span class="biz-info-value">{{ base.category }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">集数</span><span class="biz-info-value">{{ base.episodes }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">状态</span><span class="biz-info-value"><StatusTag :type="base.status === '连载中' ? 'blue' : 'green'">{{ base.status }}</StatusTag></span></div>
        <div class="biz-info-item"><span class="biz-info-label">播放量</span><span class="biz-info-value">{{ base.playCount }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">充值</span><span class="biz-info-value">{{ base.recharge }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">消耗</span><span class="biz-info-value">{{ base.cost }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">ROI</span><span class="biz-info-value">{{ base.roi }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">上线日期</span><span class="biz-info-value">{{ base.releaseDate }}</span></div>
        <div class="biz-info-item"><span class="biz-info-label">更新时间</span><span class="biz-info-value">{{ base.updateTime }}</span></div>
      </div>
      <div class="biz-divider" />
      <div class="biz-info-item"><span class="biz-info-label">简介</span><p style="margin-top:6px; color: var(--biz-text-regular); line-height:1.8;">{{ base.desc }}</p></div>
    </BaseCard>

    <BaseCard v-else-if="tab === 'script'" title="剧集信息">
      <BaseTable :data="scriptTable" :show-pagination="false" :total="0">
        <el-table-column prop="episode" label="集数" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="duration" label="时长" />
        <el-table-column prop="unlock" label="解锁条件" />
        <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '已发布' ? 'green' : row.status === '审核中' ? 'orange' : 'default'">{{ row.status }}</StatusTag></template></el-table-column>
        <el-table-column prop="updateTime" label="更新时间" />
      </BaseTable>
    </BaseCard>

    <BaseCard v-else-if="tab === 'price'" title="充值配置">
      <template #actions><el-button type="primary">新增充值包</el-button></template>
      <BaseTable :data="priceTable" :show-pagination="false" :total="0">
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="price" label="价格" />
        <el-table-column prop="coins" label="币数" />
        <el-table-column prop="bonus" label="赠送" />
        <el-table-column prop="firstBonus" label="首充赠送" />
        <el-table-column label="状态"><template #default="{ row }"><StatusTag :type="row.status === '启用' ? 'green' : 'default'">{{ row.status }}</StatusTag></template></el-table-column>
      </BaseTable>
    </BaseCard>

    <BaseCard v-else title="更新日志">
      <BaseTable :data="updateTable" :show-pagination="false" :total="0">
        <el-table-column prop="version" label="版本" />
        <el-table-column prop="episode" label="集数" />
        <el-table-column prop="operator" label="操作人" />
        <el-table-column prop="type" label="类型" />
        <el-table-column prop="note" label="说明" />
        <el-table-column prop="time" label="时间" />
      </BaseTable>
    </BaseCard>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import PageHeader from '../../../components/PageHeader.vue'
import BaseTabs from '../../../components/BaseTabs.vue'
import BaseCard from '../../../components/BaseCard.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'
import { dramaApi } from '../../../api/business'

const tab = ref('base')
const base = ref<any>({})
const scriptTable = ref<any[]>([])
const priceTable = ref<any[]>([])
const updateTable = ref<any[]>([])

onMounted(async () => {
  base.value = await dramaApi.detailBase()
  scriptTable.value = await dramaApi.detailScript()
  priceTable.value = await dramaApi.detailPrice()
  updateTable.value = await dramaApi.detailUpdate()
})
</script>
