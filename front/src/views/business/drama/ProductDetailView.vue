<template>
  <div class="biz-page">
    <PageHeader title="商品详情" back />
    <BaseCard title="基本信息">
      <div class="biz-info-grid cols-2">
        <div class="biz-info-item"><span class="biz-info-label">商品名称</span><span class="biz-info-value">商品-1</span></div>
        <div class="biz-info-item"><span class="biz-info-label">SKU</span><span class="biz-info-value">SKU00001</span></div>
        <div class="biz-info-item"><span class="biz-info-label">分类</span><span class="biz-info-value">虚拟</span></div>
        <div class="biz-info-item"><span class="biz-info-label">价格</span><span class="biz-info-value">¥39.90</span></div>
        <div class="biz-info-item"><span class="biz-info-label">库存</span><span class="biz-info-value">9999</span></div>
        <div class="biz-info-item"><span class="biz-info-label">销量</span><span class="biz-info-value">1234</span></div>
        <div class="biz-info-item"><span class="biz-info-label">状态</span><span class="biz-info-value"><StatusTag type="green">上架</StatusTag></span></div>
        <div class="biz-info-item"><span class="biz-info-label">创建时间</span><span class="biz-info-value">2026-06-01 10:00:00</span></div>
      </div>
    </BaseCard>

    <BaseCard title="销售记录">
      <BaseTable :data="sales" :show-pagination="false" :total="0">
        <el-table-column prop="time" label="时间" />
        <el-table-column prop="orderNo" label="订单号" />
        <el-table-column prop="user" label="用户" />
        <el-table-column prop="amount" label="金额" />
        <el-table-column prop="status" label="状态" />
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

const sales = ref([
  { time: '2026-07-01 10:30:00', orderNo: 'ORD00001234', user: '张三', amount: '¥39.90', status: '已完成' },
  { time: '2026-07-01 09:15:00', orderNo: 'ORD00001200', user: '李四', amount: '¥39.90', status: '已完成' },
  { time: '2026-06-30 22:10:00', orderNo: 'ORD00001180', user: '王五', amount: '¥39.90', status: '已完成' },
])
</script>
