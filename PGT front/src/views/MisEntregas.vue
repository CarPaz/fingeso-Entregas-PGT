<template>
  <v-container class="py-8">
    <v-card>
      <v-card-title class="text-h5 d-flex align-center justify-space-between">
        Mis Entregas
        <v-btn color="primary" prepend-icon="mdi-plus" to="/subir-entregas">
          Nueva entrega
        </v-btn>
      </v-card-title>

      <v-card-text>
        <v-alert v-if="errorMsg" type="error" variant="tonal" class="mb-4" closable @click:close="errorMsg = ''">
          {{ errorMsg }}
        </v-alert>

        <v-data-table
          :headers="headers"
          :items="entregas"
          :loading="loading"
          item-value="idEntrega"
          no-data-text="No tienes entregas registradas todavía"
          loading-text="Cargando entregas..."
        >
          <template #item.tipoEntrega="{ item }">
            <v-chip size="small" color="primary" variant="tonal">{{ item.tipoEntrega }}</v-chip>
          </template>

          <template #item.estado="{ item }">
            <v-chip size="small" :color="estadoColor(item.estado)" variant="tonal">{{ item.estado }}</v-chip>
          </template>

          <template #item.fechaHora="{ item }">{{ formatFecha(item.fechaHora) }}</template>

          <template #item.acciones="{ item }">
            <v-btn icon="mdi-download" size="small" variant="text" title="Descargar PDF" @click="descargarArchivo(item)" />
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api'

const router = useRouter()
const entregas = ref([])
const loading = ref(false)
const errorMsg = ref('')

const headers = [
  { title: 'Tipo', key: 'tipoEntrega' },
  { title: 'Estado', key: 'estado' },
  { title: 'Versión', key: 'numeroVersion' },
  { title: 'Fecha', key: 'fechaHora' },
  { title: 'Archivo', key: 'nombreOriginal' },
  { title: '', key: 'acciones', sortable: false, align: 'end' },
]

function estadoColor(estado) {
  const colores = {
    PENDIENTE_REVISION: 'warning',
    APROBADA: 'success',
    CORRECCION_REQUERIDA: 'error',
  }
  return colores[estado] || 'secondary'
}

function formatFecha(fecha) {
  if (!fecha) return ''
  return new Date(fecha).toLocaleString('es-CL', {
    day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit',
  })
}

async function descargarArchivo(entrega) {
  try {
    const respuesta = await api.get(`/api/entregas/${entrega.idEntrega}/archivo`, { responseType: 'blob' })
    const url = URL.createObjectURL(respuesta.data)
    const enlace = document.createElement('a')
    enlace.href = url
    enlace.download = entrega.nombreOriginal || 'entrega.pdf'
    enlace.click()
    URL.revokeObjectURL(url)
  } catch (err) {
    errorMsg.value = err.response?.status === 403
      ? 'No tienes permiso para descargar este archivo'
      : 'No fue posible descargar el archivo'
  }
}

async function fetchEntregas() {
  loading.value = true
  errorMsg.value = ''
  try {
    const respuesta = await api.get('/api/entregas')
    entregas.value = respuesta.data
  } catch (err) {
    entregas.value = []
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('usuario')
      await router.replace('/login')
    } else {
      errorMsg.value = 'No se pudieron cargar tus entregas'
    }
  } finally {
    loading.value = false
  }
}

onMounted(fetchEntregas)
</script>
