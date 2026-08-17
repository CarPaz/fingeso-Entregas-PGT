<template>
  <section class="deliveries-page">
    <div class="deliveries-card">
      <header class="card-header"><h1>Entregas</h1></header>

      <p v-if="loading" class="empty-message">Cargando entregas...</p>
      <p v-else-if="errorMsg" class="empty-message error" role="alert">{{ errorMsg }}</p>
      <p v-else-if="entregas.length === 0" class="empty-message">Aún no hay entregas registradas.</p>

      <div v-else class="table-scroll">
        <table>
          <thead>
            <tr><th>ID</th><th>Proceso</th><th>Hito</th><th>Tipo</th><th>Estado</th><th>Versión</th><th>Archivo</th><th>Tamaño</th><th>Ver</th></tr>
          </thead>
          <tbody>
            <tr v-for="entrega in entregas" :key="entrega.idEntrega">
              <td>{{ entrega.idEntrega }}</td>
              <td>{{ entrega.idProcesoTesis }}</td>
              <td>{{ entrega.idHitoEntrega }}</td>
              <td>{{ entrega.tipoEntrega }}</td>
              <td><span class="status" :data-color="estadoColor(entrega.estado)">{{ entrega.estado }}</span></td>
              <td>{{ entrega.numeroVersion }}</td>
              <td>{{ entrega.nombreOriginal }}</td>
              <td>{{ formatTamano(entrega.tamanoBytes) }}</td>
              <td><button class="pdf-button" type="button" @click="descargarArchivo(entrega)">Ver PDF</button></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>
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
  { title: 'Archivo', key: 'acciones', sortable: false, align: 'end' },
]

async function descargarArchivo(entrega) {
  try {
    const respuesta = await api.get(
      `/api/entregas/${entrega.idEntrega}/archivo`,
      { responseType: 'blob' }
    )
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

<style scoped>
.deliveries-page { min-height: calc(100vh - 68px); padding: 2rem 1rem; background: var(--pgt-bg); }
.deliveries-card { width: min(100%, 1050px); margin: 0 auto; padding: 2rem; border-radius: 12px; background: white; box-shadow: 0 4px 16px rgb(0 0 0 / 8%); }
.card-header { margin-bottom: 1.5rem; }
h1 { margin: 0; font-size: 1.65rem; }
.table-scroll { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; }
th { padding: 0.75rem; border-bottom: 2px solid #e5e7eb; text-align: left; text-transform: uppercase; white-space: nowrap; font-size: 0.76rem; }
td { padding: 0.8rem 0.75rem; border-bottom: 1px solid #eef0f3; color: #5f4b66; white-space: nowrap; font-size: 0.88rem; }
.status { display: inline-block; padding: 0.2rem 0.55rem; border-radius: 999px; color: #92400e; background: #fffbeb; font-size: 0.7rem; font-weight: 700; }
.status[data-color="success"] { color: #065f46; background: #ecfdf5; }
.status[data-color="error"] { color: #991b1b; background: #fef2f2; }
.pdf-button { padding: 0.4rem 0.75rem; border: 0; border-radius: 6px; color: white; background: var(--pgt-blue); cursor: pointer; font-weight: 650; }
.empty-message { padding: 2rem 0; color: #6b7280; text-align: center; }
.empty-message.error { color: var(--pgt-danger); }
</style>
