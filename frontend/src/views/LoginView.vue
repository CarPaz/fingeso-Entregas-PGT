<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const correo = ref('')
const contrasena = ref('')
const cargando = ref(false)
const error = ref('')

const router = useRouter()

async function iniciarSesion() {
  error.value = ''
  cargando.value = true

  try {
    const res = await axios.post('/api/auth/login', {
      correo: correo.value,
      contrasena: contrasena.value
    })

    // Guardamos la sesión en localStorage
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('usuario', JSON.stringify({
      idUsuario: res.data.idUsuario,
      nombre: res.data.nombre,
      correo: res.data.correo,
      rol: res.data.rol
    }))

    router.push('/entregas')
  } catch (e) {
    if (e.response?.status === 400) {
      error.value = e.response.data.mensaje || 'Datos inválidos.'
    } else if (e.response?.status === 401) {
      error.value = 'Correo o contraseña incorrectos.'
    } else if (e.response?.status === 429) {
      error.value = e.response.data.mensaje || 'Demasiados intentos. Intenta más tarde.'
    } else {
      error.value = 'Error al iniciar sesión. Intenta nuevamente.'
    }
  } finally {
    cargando.value = false
  }
}
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <h1>PGT</h1>
      <h2>Iniciar Sesión</h2>

      <form @submit.prevent="iniciarSesion">
        <div class="campo">
          <label>Correo</label>
          <input
            type="email"
            v-model="correo"
            placeholder="usuario@usach.cl"
            required
          />
        </div>

        <div class="campo">
          <label>Contraseña</label>
          <input
            type="password"
            v-model="contrasena"
            placeholder="••••••••"
            required
          />
        </div>

        <button type="submit" :disabled="cargando">
          {{ cargando ? 'Ingresando...' : 'Ingresar' }}
        </button>

        <p v-if="error" class="error">{{ error }}</p>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f3f4f6;
  font-family: system-ui, sans-serif;
}

.login-card {
  background: white;
  padding: 2.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  width: 100%;
  max-width: 380px;
  text-align: center;
}

h1 {
  font-size: 2.5rem;
  margin: 0 0 0.5rem 0;
}

h2 {
  font-size: 1.2rem;
  color: #374151;
  margin-bottom: 1.5rem;
}

.campo {
  margin-bottom: 1.2rem;
  text-align: left;
}

label {
  display: block;
  font-weight: 600;
  margin-bottom: 6px;
  color: #374151;
  font-size: 0.9rem;
}

input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.95rem;
  box-sizing: border-box;
}

input:focus {
  outline: none;
  border-color: #4f46e5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.15);
}

button {
  width: 100%;
  padding: 12px;
  background: #4f46e5;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
}

button:hover:not(:disabled) {
  background: #4338ca;
}

button:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.error {
  margin-top: 1rem;
  color: #991b1b;
  font-size: 0.9rem;
}
</style>