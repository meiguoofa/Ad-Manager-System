import { ref } from 'vue'

export function useMockTable<T>(fetcher: () => Promise<T[]>) {
  const data = ref<T[]>([]) as any
  const loading = ref(false)
  const page = ref(1)
  const pageSize = ref(10)
  const total = ref(0)

  async function load() {
    loading.value = true
    try {
      const all = await fetcher()
      total.value = all.length
      const start = (page.value - 1) * pageSize.value
      data.value = all.slice(start, start + pageSize.value)
    } finally {
      loading.value = false
    }
  }

  function onPageChange(p: number, s: number) {
    page.value = p
    pageSize.value = s
    load()
  }

  return { data, loading, page, pageSize, total, load, onPageChange }
}
