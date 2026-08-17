<template>
  <v-container class="py-8 mx-auto" style="max-width: 700px">
    <v-card>
      <v-card-title class="text-h5">Nuevo registro</v-card-title>

      <v-card-text>
        <v-form ref="formRef" v-model="isFormValid" @submit.prevent="submitForm">
          <v-text-field
            v-model="form.titulo"
            label="Título"
            :rules="[rules.required]"
            variant="outlined"
            class="mb-2"
          />

          <v-textarea
            v-model="form.descripcion"
            label="Descripción"
            :rules="[rules.required]"
            variant="outlined"
            rows="3"
            class="mb-2"
          />

          <v-select
            v-model="form.categoria"
            :items="categorias"
            label="Categoría"
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

const MAX_SIZE_MB = 20
const MAX_SIZE_BYTES = MAX_SIZE_MB * 1024 * 1024
const formRef = ref(null)
const isFormValid = ref(false)
const loading = ref(false)
const errorMsg = ref('')

const categorias = ['Contrato', 'Factura', 'Reporte', 'Otro']

const form = reactive({
  titulo: '',
  descripcion: '',
  categoria: null,
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

async function submitForm() {
  errorMsg.value = ''
  const { valid } = await formRef.value.validate()
  if (!valid) return

  loading.value = true
  try {
    const file = Array.isArray(form.archivo) ? form.archivo[0] : form.archivo

    const formData = new FormData()
    formData.append('titulo', form.titulo)
    formData.append('descripcion', form.descripcion)
    formData.append('categoria', form.categoria)
    formData.append('archivo', file)

    const res = await fetch('/api/tu-endpoint', {
      method: 'POST',
      body: formData,
    })

    if (!res.ok) throw new Error('Error al enviar el formulario')

    formRef.value.reset()
  } catch (err) {
    errorMsg.value = err.message || 'Ocurrió un error al enviar los datos'
  } finally {
    loading.value = false
  }
}
</script>