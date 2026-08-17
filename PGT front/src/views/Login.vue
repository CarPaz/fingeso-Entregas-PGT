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
import axios from 'axios'

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
  console.log('1 - función llamada')
  errorMsg.value = ''
  const { valid } = await formRef.value.validate()
  console.log('2 - validado:', valid)
  if (!valid) return

  loading.value = true
  try {
    console.log('3 - antes del axios.post')
    const res = await axios.post('/api/auth/login', {
      correo: correo.value,
      contrasena: contrasena.value,
    })
    console.log('4 - respuesta recibida', res) 

    const { token, idUsuario, nombre, correo: correoUsuario, rol } = res.data

    // Guardamos el token y los datos del usuario para usarlos después
    localStorage.setItem('token', token)
    localStorage.setItem('usuario', JSON.stringify({ idUsuario, nombre, correo: correoUsuario, rol }))

    router.push('subir-entregas')
  } catch (err) {
    console.log('5 - error capturado', err)  
    if (err.response?.status === 401) {
      errorMsg.value = 'Correo o contraseña incorrectos'
    } else if (err.response?.status === 429) {
      errorMsg.value = 'Demasiados intentos. Intentá de nuevo más tarde'
    } else {
      errorMsg.value = 'Ocurrió un error al iniciar sesión'
    }
  } finally {
    loading.value = false
    console.log('6 - finally ejecutado, loading:', loading.value)
  }
}
</script>