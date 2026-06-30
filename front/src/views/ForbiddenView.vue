<template>
  <section class="panel page-panel forbidden-page">
    <div class="section-heading">
      <p>Forbidden</p>
      <h2>无权限访问</h2>
    </div>
    <el-result icon="warning" title="权限不足" sub-title="当前角色无权访问该页面" />
  </section>
</template>

<style scoped>
.forbidden-page {
  display: grid;
  align-content: start;
  gap: 16px;
}
</style>
