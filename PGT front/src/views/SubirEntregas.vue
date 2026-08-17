<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api'
import {
  MAX_SIZE_MB,
  crearPayloadEntrega,
  obtenerEndpointEntrega,
  validarPdf,
} from '@/services/entregaForm'

const router = useRouter()
const archivoInput = ref(null)
const loading = ref(false)
const loadingOpciones = ref(true)
const errorMsg = ref('')
const successMsg = ref('')
const procesos = ref([])
const tipoEnvio = ref('avance')

const form = reactive({
  idProcesoTesis: null,
  idHitoEntrega: null,
  archivo: null,
})

const opcionesHito = computed(() => {
  const proceso = procesos.value.find(
    (item) => item.idProcesoTesis === form.idProcesoTesis
  )
  return proceso?.hitos || []
})

// Al cambiar el proceso solo se permiten sus hitos relacionados.
watch(() => form.idProcesoTesis, () => {
  if (!opcionesHito.value.some((hito) => hito.idHitoEntrega === form.idHitoEntrega)) {
    form.idHitoEntrega = null
  }
  if (opcionesHito.value.length === 1) {
    form.idHitoEntrega = opcionesHito.value[0].idHitoEntrega
  }
})

function seleccionarArchivo(evento) {
  const archivo = evento.target.files?.[0] || null
  const validacion = validarPdf(archivo)
  if (archivo && validacion !== true) {
    errorMsg.value = validacion
    evento.target.value = ''
    form.archivo = null
    return
  }
  errorMsg.value = ''
  form.archivo = archivo
}

async function cargarOpciones() {
  loadingOpciones.value = true
  errorMsg.value = ''
  try {
    const respuesta = await api.get('/api/entregas/opciones')
    procesos.value = respuesta.data
    if (procesos.value.length === 1) {
      form.idProcesoTesis = procesos.value[0].idProcesoTesis
    }
  } catch (error) {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('usuario')
      await router.replace('/login')
    } else {
      errorMsg.value = 'No fue posible cargar tus procesos e hitos'
    }
  } finally {
    loadingOpciones.value = false
  }
}

async function submitForm() {
  errorMsg.value = ''
  successMsg.value = ''

  const validacion = validarPdf(form.archivo)
  if (!form.idProcesoTesis || !form.idHitoEntrega || validacion !== true) {
    errorMsg.value = validacion !== true
      ? validacion
      : 'Debes seleccionar el proceso y el hito de entrega'
    return
  }

  loading.value = true
  try {
    const entrega = crearPayloadEntrega(form.idProcesoTesis, form.idHitoEntrega)
    const formData = new FormData()
    formData.append('entrega', new Blob([JSON.stringify(entrega)], { type: 'application/json' }))
    formData.append('archivo', form.archivo)

    await api.post(obtenerEndpointEntrega(tipoEnvio.value), formData)

    successMsg.value = 'Entrega enviada correctamente'
    form.archivo = null
    if (archivoInput.value) archivoInput.value.value = ''
  } catch (error) {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('usuario')
      await router.replace('/login')
    } else if (error.response?.status === 403) {
      errorMsg.value = 'Tu usuario no tiene permiso para subir entregas'
    } else if (error.response?.status === 413) {
      errorMsg.value = `El archivo supera el máximo de ${MAX_SIZE_MB} MB`
    } else {
      errorMsg.value = error.response?.data?.mensaje || 'No fue posible registrar la entrega'
    }
  } finally {
    loading.value = false
  }
}

onMounted(cargarOpciones)
</script>

<template>
  <section class="form-page">
    <div class="form-card">
      <h1>PGT</h1>
      <h2>Presentar Entrega</h2>

      <form @submit.prevent="submitForm">
        <label for="tipo">Tipo de entrega</label>
        <select id="tipo" v-model="tipoEnvio">
          <option value="avance">Avance</option>
          <option value="final">Final</option>
        </select>

        <label for="proceso">Proceso de tesis</label>
        <select id="proceso" v-model.number="form.idProcesoTesis" :disabled="loadingOpciones" required>
          <option :value="null" disabled>{{ loadingOpciones ? 'Cargando...' : 'Selecciona un proceso' }}</option>
          <option v-for="proceso in procesos" :key="proceso.idProcesoTesis" :value="proceso.idProcesoTesis">
            {{ proceso.tema }} ({{ proceso.estado }})
          </option>
        </select>

        <label for="hito">Hito de entrega</label>
        <select id="hito" v-model.number="form.idHitoEntrega" :disabled="!form.idProcesoTesis" required>
          <option :value="null" disabled>Selecciona un hito</option>
          <option v-for="hito in opcionesHito" :key="hito.idHitoEntrega" :value="hito.idHitoEntrega">
            {{ hito.nombre }} ({{ hito.estado }})
          </option>
        </select>

        <label>Archivo (PDF)</label>
        <label for="archivo" class="file-button">
          {{ form.archivo?.name || 'Seleccionar archivo PDF' }}
        </label>
        <input id="archivo" ref="archivoInput" class="file-hidden" type="file" accept="application/pdf" required @change="seleccionarArchivo" />
        <small>Tamaño máximo: {{ MAX_SIZE_MB }} MB</small>

        <button type="submit" :disabled="loading || loadingOpciones || opcionesHito.length === 0">
          {{ loading ? 'Enviando...' : 'Enviar Entrega' }}
        </button>

        <p v-if="!loadingOpciones && procesos.length === 0 && !errorMsg" class="message info">No tienes procesos con hitos disponibles.</p>
        <p v-if="errorMsg" class="message error" role="alert">{{ errorMsg }}</p>
        <p v-if="successMsg" class="message success" role="status">{{ successMsg }}</p>
      </form>
    </div>
  </section>
</template>

<style scoped>
.form-page { min-height: calc(100vh - 68px); padding: 3.5rem 1rem; background: white; }
.form-card { width: min(100%, 545px); margin: 0 auto; padding: 2rem; border-radius: 12px; background: white; box-shadow: 0 4px 18px rgb(0 0 0 / 10%); }
h1, h2 { margin-top: 0; text-align: center; }
h1 { margin-bottom: 1rem; font-size: 4rem; line-height: 1; }
h2 { margin-bottom: 1.7rem; font-size: 1.45rem; }
label { display: block; margin: 1rem 0 0.4rem; font-size: 0.9rem; font-weight: 650; }
select { width: 100%; min-height: 43px; padding: 0.65rem 0.8rem; border: 1px solid #111; border-radius: 8px; background: white; }
select:focus { outline: 3px solid rgb(0 52 217 / 15%); border-color: var(--pgt-blue); }
.file-hidden { position: absolute; width: 1px; height: 1px; overflow: hidden; opacity: 0; }
.file-button { width: 100%; margin-top: 0; padding: 0.7rem; border-radius: 8px; color: white; background: var(--pgt-cyan); text-align: center; cursor: pointer; }
small { display: block; margin-top: 0.35rem; color: #6b7280; }
button { width: 100%; margin-top: 1.5rem; padding: 0.75rem; border: 0; border-radius: 8px; color: white; background: var(--pgt-blue); cursor: pointer; font-weight: 700; }
button:disabled { opacity: 0.6; cursor: not-allowed; }
.message { margin: 1rem 0 0; padding: 0.75rem; border-radius: 8px; text-align: center; }
.error { color: var(--pgt-danger); background: #fef2f2; }
.success { color: var(--pgt-success); background: #ecfdf5; }
.info { color: #1e40af; background: #eff6ff; }
</style>
