<template>
  <v-container class="py-8">
    <v-card>
      <v-card-title class="text-h5">Nueva entrega</v-card-title>

      <v-card-text>
        <v-form ref="formRef" v-model="isFormValid" @submit.prevent="submitForm">
          <v-select
            v-model="tipoEnvio"
            :items="[
              { title: 'Entrega de avance', value: 'avance' },
              { title: 'Entrega final', value: 'final' },
            ]"
            label="¿Qué vas a entregar?"
            :rules="[rules.required]"
            variant="outlined"
            class="mb-2"
          />

          <v-select
            v-model.number="form.idProcesoTesis"
            :items="opcionesProceso"
            item-title="titulo"
            item-value="idProcesoTesis"
            label="Proceso de tesis"
            :rules="[rules.required]"
            variant="outlined"
            class="mb-2"
            :loading="loadingOpciones"
            :disabled="loadingOpciones || opcionesProceso.length === 0"
          />

          <v-select
            v-model.number="form.idHitoEntrega"
            :items="opcionesHito"
            item-title="titulo"
            item-value="idHitoEntrega"
            label="Hito de entrega"
            :rules="[rules.required]"
            variant="outlined"
            class="mb-2"
            :disabled="!form.idProcesoTesis || opcionesHito.length === 0"
          />

          <v-alert
            v-if="!loadingOpciones && opcionesProceso.length === 0 && !errorMsg"
            type="info"
            variant="tonal"
            class="mb-4"
          >
            No tienes un proceso de tesis con hitos disponible para entregar.
          </v-alert>

          <v-file-input
            v-model="form.archivo"
            label="Archivo PDF"
            accept="application/pdf"
            prepend-icon="mdi-file-pdf-box"
            variant="outlined"
            :rules="[rules.required, rules.pdf]"
            :hint="`Tamaño máximo: ${MAX_SIZE_MB}MB`"
            persistent-hint
            show-size
            class="mb-2"
          />

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
            v-if="successMsg"
            type="success"
            variant="tonal"
            class="mb-4"
            closable
            @click:close="successMsg = ''"
          >
            {{ successMsg }}
          </v-alert>

          <v-btn
            :loading="loading"
            :disabled="!isFormValid || loadingOpciones || opcionesHito.length === 0"
            color="primary"
            type="submit"
            block
          >
            Enviar
          </v-btn>
        </v-form>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import api from '@/api'
import { useRouter } from 'vue-router'
import {
  MAX_SIZE_MB,
  crearPayloadEntrega,
  obtenerArchivo,
  obtenerEndpointEntrega,
  validarPdf,
} from '@/services/entregaForm'

const router = useRouter()

const formRef = ref(null)
const isFormValid = ref(false)
const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
// Comienza activo para evitar mostrar el estado vacío antes de consultar.
const loadingOpciones = ref(true)
const procesos = ref([])

// Dropdown para elegir el tipo de envío (decide el endpoint)
const tipoEnvio = ref('avance')

const form = reactive({
  idProcesoTesis: null,
  idHitoEntrega: null,
  archivo: null,
})

const rules = {
  required: (v) => {
    if (Array.isArray(v)) return v.length > 0 || 'Este campo es obligatorio'
    return !!v || 'Este campo es obligatorio'
  },
  pdf: (v) => !obtenerArchivo(v) || validarPdf(v),
}

const opcionesProceso = computed(() => procesos.value.map((proceso) => ({
  ...proceso,
  titulo: `${proceso.tema} (${proceso.estado})`,
})))

const opcionesHito = computed(() => {
  const proceso = procesos.value.find(
    (item) => item.idProcesoTesis === form.idProcesoTesis
  )

  return (proceso?.hitos || []).map((hito) => ({
    ...hito,
    titulo: `${hito.nombre} (${hito.estado})`,
  }))
})

/*
 * Al cambiar de proceso se descarta un hito anterior que ya no corresponda.
 * Si existe una sola alternativa, la interfaz la selecciona automáticamente.
 */
watch(() => form.idProcesoTesis, () => {
  const sigueDisponible = opcionesHito.value.some(
    (hito) => hito.idHitoEntrega === form.idHitoEntrega
  )
  if (!sigueDisponible) form.idHitoEntrega = null
  if (opcionesHito.value.length === 1) {
    form.idHitoEntrega = opcionesHito.value[0].idHitoEntrega
  }
})

async function cargarOpciones() {
  loadingOpciones.value = true
  errorMsg.value = ''
  try {
    const respuesta = await api.get('/api/entregas/opciones')
    procesos.value = respuesta.data
    if (procesos.value.length === 1) {
      form.idProcesoTesis = procesos.value[0].idProcesoTesis
    }
  } catch (err) {
    if (err.response?.status === 401) {
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

  const { valid } = await formRef.value.validate()
  if (!valid) return

  const file = obtenerArchivo(form.archivo)
  const validacionArchivo = validarPdf(file)
  if (validacionArchivo !== true) {
    errorMsg.value = validacionArchivo
    return
  }

  loading.value = true
  try {
    const entregaPayload = crearPayloadEntrega(
      form.idProcesoTesis,
      form.idHitoEntrega
    )

    const formData = new FormData()
    formData.append(
      'entrega',
      new Blob([JSON.stringify(entregaPayload)], { type: 'application/json' })
    )
    formData.append('archivo', file)

    await api.post(obtenerEndpointEntrega(tipoEnvio.value), formData)

    successMsg.value = 'Entrega enviada correctamente'
    formRef.value.reset()
    tipoEnvio.value = 'avance'
  } catch (err) {
    if (err.response?.status === 401) {
      errorMsg.value = 'Tu sesión expiró, iniciá sesión de nuevo'
      localStorage.removeItem('token')
      localStorage.removeItem('usuario')
      router.push('/login')
    } else if (err.response?.status === 403) {
      errorMsg.value = 'Tu usuario no tiene permiso para subir entregas'
    } else if (err.response?.status === 413) {
      errorMsg.value = `El archivo es demasiado grande. Máximo ${MAX_SIZE_MB}MB`
    } else {
      errorMsg.value = err.response?.data?.mensaje || 'Ocurrió un error al enviar los datos'
    }
  } finally {
    loading.value = false
  }
}

onMounted(cargarOpciones)
</script>
