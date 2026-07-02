<template>
  <div class="biz-page">
    <PageHeader title="规则详情" back />
    <BaseCard title="基本信息">
      <div class="biz-info-grid">
        <div class="biz-info-item"><span class="biz-info-label">规则名称</span><span class="biz-info-value">自动规则-1</span></div>
        <div class="biz-info-item"><span class="biz-info-label">触发条件</span><span class="biz-info-value">ROI&lt;50%</span></div>
        <div class="biz-info-item"><span class="biz-info-label">执行动作</span><span class="biz-info-value">暂停广告组</span></div>
        <div class="biz-info-item"><span class="biz-info-label">范围</span><span class="biz-info-value">TikTok</span></div>
        <div class="biz-info-item"><span class="biz-info-label">状态</span><span class="biz-info-value"><StatusTag type="green">运行中</StatusTag></span></div>
        <div class="biz-info-item"><span class="biz-info-label">触发次数</span><span class="biz-info-value">23</span></div>
        <div class="biz-info-item"><span class="biz-info-label">创建时间</span><span class="biz-info-value">2026-06-01 10:00:00</span></div>
        <div class="biz-info-item"><span class="biz-info-label">最后触发</span><span class="biz-info-value">2026-07-01 09:30:00</span></div>
      </div>
    </BaseCard>

    <BaseCard title="触发记录">
      <BaseTable :data="records" :show-pagination="false" :total="0">
        <el-table-column prop="time" label="时间" />
        <el-table-column prop="target" label="目标广告组" />
        <el-table-column prop="action" label="执行动作" />
        <el-table-column prop="result" label="结果" />
      </BaseTable>
    </BaseCard>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import PageHeader from '../../../components/PageHeader.vue'
import BaseCard from '../../../components/BaseCard.vue'
import BaseTable from '../../../components/BaseTable.vue'
import StatusTag from '../../../components/StatusTag.vue'

const records = ref([
  { time: '2026-07-01 09:30:00', target: '广告组-001', action: '暂停', result: '成功' },
  { time: '2026-07-01 08:15:00', target: '广告组-005', action: '暂停', result: '成功' },
  { time: '2026-06-30 22:10:00', target: '广告组-012', action: '暂停', result: '成功' },
  { time: '2026-06-30 18:45:00', target: '广告组-008', action: '暂停', result: '成功' },
  { time: '2026-06-30 15:20:00', target: '广告组-003', action: '暂停', result: '成功' },
])
</script>
