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

        <v-alert
          v-if="usandoDatosEjemplo"
          type="info"
          variant="tonal"
          class="mb-4"
        >
          Mostrando datos de ejemplo (no se pudo conectar al backend)
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
import axios from 'axios'

const entregas = ref([])
const loading = ref(false)
const errorMsg = ref('')
const usandoDatosEjemplo = ref(false)

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

// Datos de ejemplo, se usan solo si falla la conexión al backend
const entregasEjemplo = [
  {
    idEntrega: 1,
    idProcesoTesis: 1,
    idHitoEntrega: 1,
    idEstudiante: 5,
    tipoEntrega: 'AVANCE',
    estado: 'PENDIENTE_REVISION',
    numeroVersion: 1,
    fechaHora: '2026-08-10T14:30:00',
    nombreOriginal: 'avance_capitulo1.pdf',
    nombreAlmacenado: 'a1b2c3d4_avance_capitulo1.pdf',
    mimeType: 'application/pdf',
    tamanoBytes: 2457600,
    rutaRelativaArchivo: 'storage/entregas/a1b2c3d4_avance_capitulo1.pdf',
  },
  {
    idEntrega: 2,
    idProcesoTesis: 1,
    idHitoEntrega: 2,
    idEstudiante: 6,
    tipoEntrega: 'AVANCE',
    estado: 'APROBADA',
    numeroVersion: 2,
    fechaHora: '2026-08-12T09:15:00',
    nombreOriginal: 'avance_capitulo2.pdf',
    nombreAlmacenado: 'e5f6g7h8_avance_capitulo2.pdf',
    mimeType: 'application/pdf',
    tamanoBytes: 3145728,
    rutaRelativaArchivo: 'storage/entregas/e5f6g7h8_avance_capitulo2.pdf',
  },
  {
    idEntrega: 3,
    idProcesoTesis: 2,
    idHitoEntrega: 3,
    idEstudiante: 7,
    tipoEntrega: 'FINAL',
    estado: 'CORRECCION_REQUERIDA',
    numeroVersion: 1,
    fechaHora: '2026-08-14T17:45:00',
    nombreOriginal: 'tesis_final.pdf',
    nombreAlmacenado: 'i9j0k1l2_tesis_final.pdf',
    mimeType: 'application/pdf',
    tamanoBytes: 8912896,
    rutaRelativaArchivo: 'storage/entregas/i9j0k1l2_tesis_final.pdf',
  },
  {
    idEntrega: 4,
    idProcesoTesis: 2,
    idHitoEntrega: 4,
    idEstudiante: 8,
    tipoEntrega: 'AVANCE',
    estado: 'PENDIENTE_REVISION',
    numeroVersion: 3,
    fechaHora: '2026-08-15T11:20:00',
    nombreOriginal: 'avance_metodologia.pdf',
    nombreAlmacenado: 'm3n4o5p6_avance_metodologia.pdf',
    mimeType: 'application/pdf',
    tamanoBytes: 1843200,
    rutaRelativaArchivo: 'storage/entregas/m3n4o5p6_avance_metodologia.pdf',
  },
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
  usandoDatosEjemplo.value = false
  try {
    const res = await axios.get('/api/entregas', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
    })
    entregas.value = res.data
  } catch (err) {
    entregas.value = entregasEjemplo
    usandoDatosEjemplo.value = true
    errorMsg.value = 'No se pudieron cargar las entregas reales, mostrando ejemplos'
  } finally {
    loading.value = false
  }
}

onMounted(fetchEntregas)
</script>