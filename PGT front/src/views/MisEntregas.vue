<template>
  <v-container class="py-8">
    <v-card>
      <v-card-title class="text-h5 d-flex align-center justify-space-between">
        Mis Entregas
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          to="/subir-entregas"
        >
          Nueva entrega
        </v-btn>
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
          item-value="id"
          no-data-text="No tenés entregas registradas todavía"
          loading-text="Cargando entregas..."
        >
          <template #item.categoria="{ item }">
            <v-chip size="small" color="primary" variant="tonal">
              {{ item.categoria }}
            </v-chip>
          </template>

          <template #item.estado="{ item }">
            <v-chip
              size="small"
              :color="estadoColor(item.estado)"
              variant="tonal"
            >
              {{ item.estado }}
            </v-chip>
          </template>

          <template #item.fecha="{ item }">
            {{ formatFecha(item.fecha) }}
          </template>

          <template #item.acciones="{ item }">
            <v-btn
              icon="mdi-download"
              size="small"
              variant="text"
              :href="item.archivoUrl"
              target="_blank"
            />
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api'

const entregas = ref([])
const loading = ref(false)
const errorMsg = ref('')

const headers = [
  { title: 'Título', key: 'titulo' },
  { title: 'Categoría', key: 'categoria' },
  { title: 'Estado', key: 'estado' },
  { title: 'Fecha', key: 'fecha' },
  { title: '', key: 'acciones', sortable: false, align: 'end' },
]

function estadoColor(estado) {
  const colores = {
    Pendiente: 'warning',
    Aprobado: 'success',
    Rechazado: 'error',
  }
  return colores[estado] || 'secondary'
}

function formatFecha(fecha) {
  if (!fecha) return ''
  return new Date(fecha).toLocaleDateString('es-CL', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
}

async function fetchEntregas() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await api.get('/api/entregas')
    entregas.value = res.data
  } catch (err) {
    if (err.response?.status === 401) {
      errorMsg.value = 'Tu sesión expiró, iniciá sesión de nuevo'
    } else {
      errorMsg.value = 'No se pudieron cargar tus entregas'
    }
  } finally {
    loading.value = false
  }
}

onMounted(fetchEntregas)
</script>
