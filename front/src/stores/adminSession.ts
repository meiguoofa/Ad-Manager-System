import { defineStore } from 'pinia'
import { ApiClientError, apiClient, type ApiResponse } from '../api/client'
import { ACCESS_TOKEN_KEY, PROFILE_KEY, REFRESH_TOKEN_KEY } from './sessionKeys'

export type RoleCode = 'PRIMARY_SUPER_ADMIN' | 'SUPER_ADMIN' | 'OPERATOR' | 'SALES' | 'AUDITOR'

export interface AdminProfile {
  id: string
  username: string
  displayName: string
  email?: string | null
  status: string
  roles: RoleCode[]
  lastLoginAt?: string | null
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  accessTokenExpiresAt: string
  refreshTokenExpiresAt: string
  user: AdminProfile
}

let refreshPromise: Promise<void> | null = null

function parseProfile(raw: string | null): AdminProfile | null {
  if (!raw) return null
  try {
    return JSON.parse(raw) as AdminProfile
  } catch {
    return null
  }
}

export const useAdminSessionStore = defineStore('adminSession', {
  state: () => ({
    accessToken: localStorage.getItem(ACCESS_TOKEN_KEY),
    refreshToken: localStorage.getItem(REFRESH_TOKEN_KEY),
    profile: parseProfile(localStorage.getItem(PROFILE_KEY)) as AdminProfile | null,
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.accessToken),
    roles: (state): RoleCode[] => state.profile?.roles ?? [],
    primaryRole: (state): RoleCode | '' => state.profile?.roles?.[0] ?? '',
  },
  actions: {
    async login(username: string, password: string) {
      const { data } = await apiClient.post<ApiResponse<LoginResponse>>('/admin/auth/login', {
        username,
        password,
      })
      this.applySession(data.data)
    },

    async refreshSessionSilently() {
      if (!this.refreshToken) {
        throw new ApiClientError('登录状态已失效，请重新登录', 401)
      }

      if (!refreshPromise) {
        refreshPromise = apiClient
          .post<ApiResponse<LoginResponse>>('/admin/auth/refresh', {
            refreshToken: this.refreshToken,
          })
          .then(({ data }) => {
            this.applySession(data.data)
          })
          .finally(() => {
            refreshPromise = null
          })
      }

      return refreshPromise
    },

    async fetchMe() {
      if (!this.accessToken) return
      const { data } = await apiClient.get<ApiResponse<AdminProfile>>('/admin/auth/me')
      this.profile = data.data
      this.persist()
    },

    async logout() {
      const refreshToken = this.refreshToken
      try {
        if (!refreshToken) return
        await apiClient.post('/admin/auth/logout', { refreshToken })
      } catch {
        // noop
      } finally {
        this.clearSession()
      }
    },

    clearSession() {
      this.accessToken = null
      this.refreshToken = null
      this.profile = null
      localStorage.removeItem(ACCESS_TOKEN_KEY)
      localStorage.removeItem(REFRESH_TOKEN_KEY)
      localStorage.removeItem(PROFILE_KEY)
    },

    hasAnyRole(required: RoleCode[] | undefined) {
      if (!required || required.length === 0) return true
      return required.some((role) => this.roles.includes(role))
    },

    applySession(session: LoginResponse) {
      this.accessToken = session.accessToken
      this.refreshToken = session.refreshToken
      this.profile = session.user
      this.persist()
    },

    persist() {
      if (this.accessToken) {
        localStorage.setItem(ACCESS_TOKEN_KEY, this.accessToken)
      }
      if (this.refreshToken) {
        localStorage.setItem(REFRESH_TOKEN_KEY, this.refreshToken)
      }
      if (this.profile) {
        localStorage.setItem(PROFILE_KEY, JSON.stringify(this.profile))
      }
    },
  },
})
