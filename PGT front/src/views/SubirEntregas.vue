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

          <v-text-field
            v-model.number="form.idProcesoTesis"
            label="ID Proceso de tesis"
            type="number"
            :rules="[rules.required]"
            variant="outlined"
            class="mb-2"
          />

          <v-text-field
            v-model.number="form.idHitoEntrega"
            label="ID Hito de entrega"
            type="number"
            :rules="[rules.required]"
            variant="outlined"
            class="mb-2"
          />

          <v-file-input
            v-model="form.archivo"
            label="Archivo PDF"
            accept="application/pdf"
            prepend-icon="mdi-file-pdf-box"
            variant="outlined"
            :rules="[rules.required, rules.isPdf, rules.maxSize]"
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
            :disabled="!isFormValid"
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
import { ref, reactive } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()

const MAX_SIZE_MB = 20
const MAX_SIZE_BYTES = MAX_SIZE_MB * 1024 * 1024

const formRef = ref(null)
const isFormValid = ref(false)
const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

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
  isPdf: (v) => {
    const file = Array.isArray(v) ? v[0] : v
    if (!file) return true
    return file.type === 'application/pdf' || 'El archivo debe ser un PDF'
  },
  maxSize: (v) => {
    const file = Array.isArray(v) ? v[0] : v
    if (!file) return true
    return file.size <= MAX_SIZE_BYTES || `El archivo no debe superar ${MAX_SIZE_MB}MB`
  },
}

function getFile() {
  return Array.isArray(form.archivo) ? form.archivo[0] : form.archivo
}

async function submitForm() {
  errorMsg.value = ''
  successMsg.value = ''

  const { valid } = await formRef.value.validate()
  if (!valid) return

  const file = getFile()

  if (file && file.size > MAX_SIZE_BYTES) {
    errorMsg.value = `El archivo no debe superar ${MAX_SIZE_MB}MB`
    return
  }

  loading.value = true
  try {
    const usuario = JSON.parse(localStorage.getItem('usuario') || '{}')

    const entregaPayload = {
      idProcesoTesis: form.idProcesoTesis,
      idHitoEntrega: form.idHitoEntrega,
      idEstudiante: usuario.idUsuario,
    }

    const formData = new FormData()
    formData.append(
      'entrega',
      new Blob([JSON.stringify(entregaPayload)], { type: 'application/json' })
    )
    formData.append('archivo', file)

    const endpoint =
      tipoEnvio.value === 'avance' ? '/api/entregas/avance' : '/api/entregas/final'

    await axios.post(endpoint, formData, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`,
      },
    })

    successMsg.value = 'Entrega enviada correctamente'
    formRef.value.reset()
    tipoEnvio.value = 'avance'
  } catch (err) {
    if (err.response?.status === 401) {
      errorMsg.value = 'Tu sesión expiró, iniciá sesión de nuevo'
      localStorage.removeItem('token')
      router.push('/login')
    } else if (err.response?.status === 413) {
      errorMsg.value = `El archivo es demasiado grande. Máximo ${MAX_SIZE_MB}MB`
    } else {
      errorMsg.value = err.response?.data?.mensaje || 'Ocurrió un error al enviar los datos'
    }
  } finally {
    loading.value = false
  }
}
</script>