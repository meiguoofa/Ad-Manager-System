<template>
  <div class="biz-page">
    <h1 class="biz-page-title">工作台</h1>
    <p class="biz-page-desc">欢迎使用数据监测平台，以下是今日核心数据概览</p>

    <div class="biz-stats-grid">
      <StatCard label="今日消耗" value="¥128,560.50" :diff="12.5" diff-label="较昨日" icon="💰" icon-color="blue" :trend="[true,true,false,true,true,true,false,true]" />
      <StatCard label="今日收入" value="¥256,890.00" :diff="8.3" diff-label="较昨日" icon="📈" icon-color="green" :trend="[true,false,true,true,true,true,false,true]" />
      <StatCard label="ROI" value="1:2.35" :diff="5" diff-label="0.12 较昨日" icon="📊" icon-color="purple" :trend="[false,true,true,false,true,true,true,false]" />
      <StatCard label="达标率" value="78.5%" :diff="-3.2" diff-label="较昨日" icon="🎯" icon-color="orange" :trend="[true,false,true,false,true,false,true,true]" />
      <StatCard label="新增用户" value="3,256" :diff="15.8" diff-label="较昨日" icon="👥" icon-color="cyan" :trend="[true,true,true,false,true,true,true,false]" />
      <StatCard label="充值金额" value="¥89,650.00" :diff="5.2" diff-label="较昨日" icon="💳" icon-color="red" :trend="[true,false,true,true,false,true,true,true]" />
    </div>

    <div class="biz-charts-row">
      <ChartPanel title="消耗趋势（近7天）" :option="costOption" :height="240">
        <template #actions>
          <el-select v-model="costRange" size="small" style="width: 110px">
            <el-option label="近7天" value="7" />
            <el-option label="近15天" value="15" />
            <el-option label="近30天" value="30" />
          </el-select>
        </template>
      </ChartPanel>
      <ChartPanel title="渠道占比" :option="channelOption" :height="240" />
    </div>

    <div class="biz-charts-row">
      <ChartPanel title="ROI趋势（近7天）" :option="roiOption" :height="240" />
      <BaseCard title="异常告警">
        <template #actions>
          <el-button text type="primary">查看全部</el-button>
        </template>
        <div v-for="(a, i) in alerts" :key="i" class="biz-list-item">
          <div class="biz-list-item-icon" :style="{ background: a.bg, color: a.color }">{{ a.icon }}</div>
          <div class="biz-list-item-content">
            <div class="biz-list-item-title">{{ a.title }}</div>
            <div class="biz-list-item-desc">{{ a.desc }}</div>
          </div>
          <div class="biz-list-item-meta">{{ a.time }}</div>
        </div>
      </BaseCard>
    </div>

    <BaseCard title="快捷操作">
      <div class="biz-quick-actions">
        <div v-for="q in quickActions" :key="q.text" class="biz-quick-action" @click="q.action">
          <div class="biz-quick-action-icon">{{ q.icon }}</div>
          <div class="biz-quick-action-text">{{ q.text }}</div>
        </div>
      </div>
    </BaseCard>

    <BaseModal v-model="createVisible" title="新建广告" width="560px">
      <el-form label-position="top">
        <el-form-item label="广告名称"><el-input placeholder="请输入广告名称" /></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="广告渠道"><el-select class="biz-w-full" placeholder="请选择"><el-option v-for="c in channels" :key="c" :label="c" :value="c" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="短剧"><el-select class="biz-w-full" placeholder="请选择"><el-option v-for="i in 10" :key="i" :label="`短剧-${String(i).padStart(2,'0')}`" :value="i" /></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="日预算"><el-input placeholder="¥0.00" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="出价"><el-input placeholder="¥0.00" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="落地页"><el-input placeholder="https://" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="createVisible = false">立即创建</el-button>
      </template>
    </BaseModal>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import StatCard from '../components/StatCard.vue'
import ChartPanel from '../components/ChartPanel.vue'
import BaseCard from '../components/BaseCard.vue'
import BaseModal from '../components/BaseModal.vue'
import { lineOption, barOption, pieOption } from '../composables/useECharts'

const router = useRouter()
const costRange = ref('7')
const createVisible = ref(false)
const channels = ['TikTok', 'Facebook', 'Google', 'Twitter', 'Unity']

const costOption = lineOption({
  x: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
  series: [
    { name: '消耗', data: [98500, 102300, 110800, 95600, 125200, 138900, 128560], color: '#1890ff' },
    { name: '收入', data: [180000, 195000, 210000, 175000, 245000, 268000, 256890], color: '#52c41a' },
  ],
  area: true,
})

const channelOption = pieOption({
  data: [
    { name: 'TikTok', value: 35 },
    { name: 'Facebook', value: 25 },
    { name: 'Google', value: 20 },
    { name: 'Twitter', value: 15 },
    { name: '其他', value: 5 },
  ],
  doughnut: true,
})

const roiOption = barOption({
  x: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
  series: [{ name: 'ROI', data: [2.1, 2.4, 1.9, 2.6, 2.3, 2.8, 2.5] }],
})

const alerts = [
  { icon: '⚠️', bg: '#fff1f0', color: '#f5222d', title: 'ROI异常下降 - 抖音渠道', desc: '近2小时ROI从2.5降至1.8，触发告警', time: '10分钟前' },
  { icon: '⚡', bg: '#fff7e6', color: '#faad14', title: '消耗突增 - FB广告组15', desc: '今日消耗已超昨日50%，请关注', time: '25分钟前' },
  { icon: '🔴', bg: '#fff1f0', color: '#f5222d', title: '账户余额不足 - 账户A003', desc: '剩余金额¥2,580，预计可消耗2小时', time: '1小时前' },
  { icon: '✅', bg: '#f6ffed', color: '#52c41a', title: '控损规则已生效', desc: '规则ID: R2024008 已自动暂停3个广告组', time: '2小时前' },
  { icon: '📉', bg: '#fff7e6', color: '#faad14', title: '转化率下降 - 落地页LP-018', desc: '转化率从8.5%降至6.2%，建议优化', time: '3小时前' },
]

const quickActions = [
  { icon: '🎨', text: '素材管理', action: () => router.push('/material/library') },
  { icon: '🎬', text: '短剧管理', action: () => router.push('/drama/management') },
  { icon: '🌐', text: '站点管理', action: () => router.push('/site/list') },
  { icon: '📦', text: '订单查询', action: () => router.push('/order/all') },
  { icon: '👥', text: '用户管理', action: () => router.push('/auth') },
  { icon: '💳', text: '充值记录', action: () => router.push('/delivery/recharge-detail') },
  { icon: '🛡️', text: '自动规则', action: () => router.push('/delivery/auto-rules') },
  { icon: '➕', text: '新建广告', action: () => { createVisible.value = true } },
]
</script>
