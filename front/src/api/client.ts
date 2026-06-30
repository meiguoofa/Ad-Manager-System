import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios'
import { ACCESS_TOKEN_KEY } from '../stores/sessionKeys'

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: string
}

export interface AjaxResult<T> {
  code: number
  msg: string
  data: T
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
}

export class ApiClientError extends Error {
  status?: number
  code?: number

  constructor(message: string, status?: number, code?: number) {
    super(message)
    this.name = 'ApiClientError'
    this.status = status
    this.code = code
  }
}

type RetryConfig = InternalAxiosRequestConfig & { _retry?: boolean }

const REFRESH_URL = '/admin/auth/refresh'
const LOGIN_URL = '/admin/auth/login'
const RAW_AXIOS_STATUS_PATTERN = /^Request failed with status code \d+$/i

export const apiClient = axios.create({
  baseURL: '/api/v1',
  timeout: 15000,
})

export function getDefaultErrorMessage(status?: number) {
  if (status === 401) return '登录已过期，请重新登录'
  if (status === 403) return '权限不足，无法执行该操作'
  if (status === 409) return '状态冲突或重复操作，请刷新后重试'
  if (status && status >= 500) return '服务暂时不可用，请稍后重试'
  return ''
}

export function resolveUiErrorMessage(error: unknown, fallback = '请求失败，请稍后重试') {
  if (error instanceof ApiClientError && error.message) {
    return error.message
  }

  const maybeError = error as any
  const responseMessage =
    typeof maybeError?.response?.data?.message === 'string' ? maybeError.response.data.message.trim()
    : typeof maybeError?.response?.data?.msg === 'string' ? maybeError.response.data.msg.trim() : ''
  if (responseMessage) {
    return responseMessage
  }

  const message = typeof maybeError?.message === 'string' ? maybeError.message.trim() : ''
  if (message && !RAW_AXIOS_STATUS_PATTERN.test(message)) {
    return message
  }

  const status = maybeError?.status ?? maybeError?.response?.status
  return getDefaultErrorMessage(status) || fallback
}

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem(ACCESS_TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<any>) => {
    const config = (error.config || {}) as RetryConfig
    const status = error.response?.status
    const url = config.url || ''

    if (
      status === 401 &&
      !config._retry &&
      !url.includes(LOGIN_URL) &&
      !url.includes(REFRESH_URL)
    ) {
      config._retry = true
      try {
        const { useAdminSessionStore } = await import('../stores/adminSession')
        const session = useAdminSessionStore()
        await session.refreshSessionSilently()
        return apiClient(config)
      } catch {
        const { useAdminSessionStore } = await import('../stores/adminSession')
        const session = useAdminSessionStore()
        session.clearSession()
        if (!window.location.pathname.startsWith('/login')) {
          window.location.href = '/login'
        }
      }
    }

    const fallbackMessage = getDefaultErrorMessage(status) || '请求失败，请稍后重试'
    const businessCode =
      typeof error?.response?.data?.code === 'number' ? error.response.data.code : undefined
    const serverMessage =
      typeof error?.response?.data?.message === 'string'
        ? error.response.data.message
        : typeof error?.response?.data?.msg === 'string'
          ? error.response.data.msg
          : fallbackMessage

    return Promise.reject(new ApiClientError(serverMessage || fallbackMessage, status, businessCode))
  },
)
