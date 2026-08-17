<template>
  <v-container class="fill-height" fluid>
    <v-row justify="center" align="center" class="fill-height">
      <v-col cols="12" sm="8" md="4">
        <v-card class="pa-4">
          <v-card-title class="text-h5 text-center mb-2">
            Iniciar sesión
          </v-card-title>

          <v-card-text>
            <v-form ref="formRef" v-model="isFormValid" @submit.prevent="handleLogin">
              <v-text-field
                v-model="correo"
                label="Correo"
                type="email"
                :rules="[rules.required, rules.email]"
                variant="outlined"
                class="mb-2"
              />

              <v-text-field
                v-model="contrasena"
                label="Contraseña"
                type="password"
                :rules="[rules.required]"
                variant="outlined"
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
                Ingresar
              </v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api'
import { guardarSesion, rutaInicialPorRol } from '@/auth/session'

const router = useRouter()

const formRef = ref(null)
const isFormValid = ref(false)
const loading = ref(false)
const errorMsg = ref('')

const correo = ref('')
const contrasena = ref('')

const rules = {
  required: (v) => !!v || 'Este campo es obligatorio',
  email: (v) => /.+@.+\..+/.test(v) || 'Correo inválido',
}

async function handleLogin() {
  errorMsg.value = ''
  const { valid } = await formRef.value.validate()
  if (!valid) return

  loading.value = true
  try {
    const res = await api.post('/api/auth/login', {
      correo: correo.value,
      contrasena: contrasena.value,
    })

    // La función común valida la respuesta antes de guardar el JWT.
    const { usuario } = guardarSesion(res.data)
    await router.replace(rutaInicialPorRol(usuario.rol))
  } catch (err) {
    if (err.response?.status === 401) {
      errorMsg.value = 'Correo o contraseña incorrectos'
    } else if (err.response?.status === 429) {
      errorMsg.value = 'Demasiados intentos. Intentá de nuevo más tarde'
    } else {
      errorMsg.value = 'Ocurrió un error al iniciar sesión'
    }
  } finally {
    loading.value = false
  }
}
</script>
