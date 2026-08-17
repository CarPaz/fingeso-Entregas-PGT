import axios from 'axios'

/*
 * En desarrollo las rutas /api pasan por el proxy de Vite.
 * En producción se recomienda servir frontend y backend bajo el mismo dominio.
 * VITE_API_BASE_URL queda disponible solo para despliegues que lo necesiten.
 */
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default api
