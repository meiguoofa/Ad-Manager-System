<template>
  <div class="biz-page">
    <PageHeader title="页面编辑器" desc="可视化落地页编辑" back />

    <div class="editor-layout">
      <!-- 左侧组件面板 -->
      <div class="editor-panel">
        <div class="biz-card-title biz-mb-16">组件库</div>
        <div v-for="g in components" :key="g.group" class="biz-mb-16">
          <div class="biz-text-muted biz-mb-8" style="font-size:12px">{{ g.group }}</div>
          <div class="comp-grid">
            <div v-for="item in g.items" :key="item" class="comp-item" @click="addComp(item)">{{ item }}</div>
          </div>
        </div>
      </div>

      <!-- 中间画布 -->
      <div class="editor-canvas">
        <div class="biz-flex-between biz-mb-16">
          <div class="biz-card-title">画布</div>
          <div>
            <el-button size="small">预览</el-button>
            <el-button size="small" type="primary">保存</el-button>
          </div>
        </div>
        <div class="canvas-area">
          <div v-for="(c, i) in canvas" :key="c.id" class="canvas-comp" :class="{ active: selected === i }" @click="selected = i">
            <div class="canvas-comp-type">{{ c.type }}</div>
            <div class="canvas-comp-content">{{ c.content }}</div>
          </div>
        </div>
      </div>

      <!-- 右侧属性面板 -->
      <div class="editor-props">
        <div class="biz-card-title biz-mb-16">属性设置</div>
        <el-form label-position="top" size="small" v-if="selected !== null">
          <el-form-item label="组件类型"><el-input :model-value="canvas[selected].type" disabled /></el-form-item>
          <el-form-item label="内容"><el-input :model-value="canvas[selected].content" /></el-form-item>
          <el-form-item label="样式">
            <el-select class="biz-w-full" model-value="">
              <el-option label="默认" value="" />
              <el-option label="主题色" value="primary" />
              <el-option label="强调" value="accent" />
            </el-select>
          </el-form-item>
          <el-form-item label="宽度"><el-slider :model-value="100" /></el-form-item>
        </el-form>
        <div v-else class="biz-empty"><div class="biz-empty-icon">👈</div><div class="biz-empty-text">选择组件以编辑属性</div></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import PageHeader from '../../../components/PageHeader.vue'
import { siteApi } from '../../../api/business'

const components = ref<any[]>([])
const canvas = ref<any[]>([])
const selected = ref<number | null>(null)
let compId = 100

onMounted(async () => {
  components.value = await siteApi.editorComponents()
  canvas.value = await siteApi.editorCanvas()
})

function addComp(type: string) {
  canvas.value.push({ id: ++compId, type, content: '新增' + type })
}
</script>

<style scoped>
.editor-layout { display: grid; grid-template-columns: 240px 1fr 280px; gap: 16px; }
.editor-panel, .editor-props, .editor-canvas { background: #fff; border-radius: var(--biz-radius-md); padding: 16px; box-shadow: var(--biz-shadow-sm); }
.editor-canvas { min-height: 600px; }
.canvas-area { min-height: 540px; background: #fafafa; border: 1px dashed #d9d9d9; border-radius: var(--biz-radius-sm); padding: 16px; }
.canvas-comp { background: #fff; border: 1px solid #e4e7ed; border-radius: var(--biz-radius-sm); padding: 12px; margin-bottom: 12px; cursor: pointer; transition: all .2s; }
.canvas-comp:hover { border-color: #1890ff; }
.canvas-comp.active { border-color: #1890ff; box-shadow: 0 0 0 2px rgba(24,144,255,.2); }
.canvas-comp-type { font-size: 12px; color: var(--biz-text-secondary); margin-bottom: 4px; }
.canvas-comp-content { font-size: 14px; color: var(--biz-text); }
.comp-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }
.comp-item { padding: 8px; text-align: center; background: #fafafa; border-radius: var(--biz-radius-sm); cursor: pointer; font-size: 12px; transition: all .2s; }
.comp-item:hover { background: #e6f7ff; color: #1890ff; }
</style>
