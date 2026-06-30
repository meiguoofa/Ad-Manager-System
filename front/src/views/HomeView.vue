<template>
  <div class="dashboard-container">
    <el-row :gutter="24" class="stat-row">
      <el-col :span="6" v-for="(stat, index) in stats" :key="index">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-header">
            <span>{{ stat.title }}</span>
            <el-icon :class="stat.colorClass"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-body">
            <span class="stat-value">{{ stat.value }}</span>
          </div>
          <div class="stat-footer">
            <span>较昨日 <span :class="stat.trend > 0 ? 'trend-up' : 'trend-down'">{{ stat.trend > 0 ? '+' : '' }}{{ stat.trend }}%</span></span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="24">
      <el-col :span="16">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>系统运行指标</span>
            </div>
          </template>
          <div class="chart-placeholder">
            <!-- ECharts Placeholder -->
            <div class="placeholder-content">系统流量报表区域</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>近期动态</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(activity, index) in activities"
              :key="index"
              :type="activity.type"
              :timestamp="activity.timestamp"
              size="small"
            >
              {{ activity.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { User, DataLine, Operation, Warning } from '@element-plus/icons-vue'

const stats = [
  { title: '活跃用户数', value: '1,284', trend: 12.5, icon: User, colorClass: 'text-primary' },
  { title: '接口调用量', value: '84,392', trend: 8.2, icon: DataLine, colorClass: 'text-success' },
  { title: '更新发布数', value: '14', trend: 0.0, icon: Operation, colorClass: 'text-warning' },
  { title: '异常报警', value: '3', trend: -4.1, icon: Warning, colorClass: 'text-danger' },
]

const activities = [
  { content: '运维人员更新了 Windows 5.2.0 版本', timestamp: '2026-04-07 14:02', type: 'primary' },
  { content: '授权服务检测到 3 个异常登录', timestamp: '2026-04-07 10:45', type: 'danger' },
  { content: 'Ubuntu 5.1.0 稳定版发布完成', timestamp: '2026-04-06 18:40', type: 'success' },
  { content: '数据库成功执行每日备份', timestamp: '2026-04-06 02:00', type: 'info' },
]
</script>

<style scoped>
.dashboard-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.stat-card {
  min-height: 130px;
  border-radius: var(--radius-md);
  border: 1px solid var(--border-color-light);
}

.stat-row {
  margin-bottom: 24px;
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--text-secondary);
  font-size: 14px;
}

.stat-header .el-icon {
  font-size: 18px;
}

.text-primary { color: var(--brand-color); }
.text-success { color: #67c23a; }
.text-warning { color: #e6a23c; }
.text-danger { color: #f56c6c; }

.stat-body {
  margin-top: 12px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-primary);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", sans-serif;
}

.stat-footer {
  margin-top: 12px;
  font-size: 13px;
  color: var(--text-secondary);
}

.trend-up { color: #f56c6c; }
.trend-down { color: #67c23a; }

.chart-card {
  height: 420px;
  border-radius: var(--radius-md);
  border: 1px solid var(--border-color-light);
}

.card-header {
  font-weight: 600;
  color: var(--text-primary);
}

.chart-placeholder {
  height: 300px;
  background-color: var(--bg-color);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-content {
  color: var(--text-secondary);
  font-size: 14px;
}

.el-timeline {
  padding-left: 0;
  padding-top: 12px;
}
</style>
