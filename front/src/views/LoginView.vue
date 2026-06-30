<template>
  <section class="login-container">
    <div class="login-box">
      <div class="login-header">
        <div class="logo">仙</div>
        <h2>仙之大陆</h2>
      </div>

      <el-form :model="form" class="login-form" size="large" @keyup.enter="submitLogin">
        <el-form-item>
          <el-input v-model="form.username" placeholder="请输入账号" :prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            show-password
            placeholder="请输入密码"
            :prefix-icon="Lock"
          />
        </el-form-item>
        <el-button :loading="loading" type="primary" class="login-btn" @click="submitLogin">
          登录
        </el-button>
      </el-form>
    </div>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { resolveUiErrorMessage } from '../api/client'
import { useAdminSessionStore } from '../stores/adminSession'

const route = useRoute()
const router = useRouter()
const session = useAdminSessionStore()

const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
})

async function submitLogin() {
  if (!form.username || !form.password || loading.value) {
    return
  }

  loading.value = true
  try {
    await session.login(form.username.trim(), form.password)
    form.password = ''
    router.push(String(route.query.redirect || '/'))
  } catch (error) {
    ElMessage.error(resolveUiErrorMessage(error, '登录失败，请重试'))
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
}

.login-box {
  width: 400px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.05);
  padding: 40px;
  box-sizing: border-box;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  width: 48px;
  height: 48px;
  background: var(--brand-color);
  color: #ffffff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 700;
  margin: 0 auto 16px;
}

.login-header h2 {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
}

.login-header p {
  margin: 0;
  font-size: 14px;
  color: var(--text-secondary);
}

.login-btn {
  width: 100%;
  margin-top: 12px;
  font-size: 16px;
}
</style>
