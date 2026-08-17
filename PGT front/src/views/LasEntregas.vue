<template>
  <v-container class="py-8">
    <v-card>
      <v-card-title class="text-h5">
        Las Entregas
      </v-card-title>

      <v-card-text>
        <v-alert
          v-if="errorMsg"
          type="error"
          variant="tonal"
          class="mb-4"
          closable
          @click:close="errorMsg = ''"
        >
          {{ errorMsg }}
        </v-alert>

        <v-data-table
          :headers="headers"
          :items="entregas"
          :loading="loading"
          item-value="idEntrega"
          no-data-text="No hay entregas registradas todavía"
          loading-text="Cargando entregas..."
        >
          <template #item.tipoEntrega="{ item }">
            <v-chip size="small" color="primary" variant="tonal">
              {{ item.tipoEntrega }}
            </v-chip>
          </template>

          <template #item.estado="{ item }">
            <v-chip size="small" :color="estadoColor(item.estado)" variant="tonal">
              {{ item.estado }}
            </v-chip>
          </template>

          <template #item.fechaHora="{ item }">
            {{ formatFecha(item.fechaHora) }}
          </template>

          <template #item.tamanoBytes="{ item }">
            {{ formatTamano(item.tamanoBytes) }}
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

const entregas = ref([])
const loading = ref(false)
const errorMsg = ref('')
const router = useRouter()

// Mismos atributos que devuelve EntregaResponseDTO en el backend
const headers = [
  { title: 'ID Entrega', key: 'idEntrega' },
  { title: 'ID Proceso Tesis', key: 'idProcesoTesis' },
  { title: 'ID Hito Entrega', key: 'idHitoEntrega' },
  { title: 'ID Estudiante', key: 'idEstudiante' },
  { title: 'Tipo', key: 'tipoEntrega' },
  { title: 'Estado', key: 'estado' },
  { title: 'Versión', key: 'numeroVersion' },
  { title: 'Fecha', key: 'fechaHora' },
  { title: 'Nombre Original', key: 'nombreOriginal' },
  { title: 'Nombre Almacenado', key: 'nombreAlmacenado' },
  { title: 'Tipo MIME', key: 'mimeType' },
  { title: 'Tamaño', key: 'tamanoBytes' },
  { title: 'Ruta Archivo', key: 'rutaRelativaArchivo' },
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

function formatTamano(bytes) {
  if (!bytes) return ''
  const mb = bytes / (1024 * 1024)
  return `${mb.toFixed(2)} MB`
}

async function fetchEntregas() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await api.get('/api/entregas')
    entregas.value = res.data
  } catch (err) {
    entregas.value = []
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('usuario')
      await router.replace('/login')
    } else if (err.response?.status === 403) {
      errorMsg.value = 'Tu usuario no tiene permiso para revisar todas las entregas'
    } else {
      errorMsg.value = 'No se pudieron cargar las entregas'
    }
  } finally {
    loading.value = false
  }
}

onMounted(fetchEntregas)
</script>
