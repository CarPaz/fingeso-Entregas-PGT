<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api'
import { guardarSesion, rutaInicialPorRol } from '@/auth/session'

const router = useRouter()
const correo = ref('')
const contrasena = ref('')
const loading = ref(false)
const errorMsg = ref('')

// Autentica al usuario y conserva el mismo manejo seguro de sesión y roles.
async function handleLogin() {
  errorMsg.value = ''
  loading.value = true

  try {
    const respuesta = await api.post('/api/auth/login', {
      correo: correo.value,
      contrasena: contrasena.value,
    })

    const { usuario } = guardarSesion(respuesta.data)
    await router.replace(rutaInicialPorRol(usuario.rol))
  } catch (error) {
    if (error.response?.status === 401) {
      errorMsg.value = 'Correo o contraseña incorrectos'
    } else if (error.response?.status === 429) {
      errorMsg.value = 'Demasiados intentos. Intenta nuevamente más tarde'
    } else {
      errorMsg.value = 'No fue posible iniciar sesión'
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="login-page">
    <div class="login-card">
      <h1>PGT</h1>
      <h2>Iniciar Sesión</h2>

      <form @submit.prevent="handleLogin">
        <label for="correo">Correo</label>
        <input id="correo" v-model.trim="correo" type="email" autocomplete="username" placeholder="usuario@usach.cl" required />

        <label for="contrasena">Contraseña</label>
        <input id="contrasena" v-model="contrasena" type="password" autocomplete="current-password" placeholder="••••••••" required />

        <button type="submit" :disabled="loading">
          {{ loading ? 'Ingresando...' : 'Ingresar' }}
        </button>

        <p v-if="errorMsg" class="message error" role="alert">{{ errorMsg }}</p>
      </form>
    </div>
  </section>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 1.5rem;
  background: var(--pgt-bg);
}

.login-card {
  width: min(100%, 390px);
  padding: 2.5rem;
  border-radius: 12px;
  background: white;
  box-shadow: 0 4px 18px rgb(0 0 0 / 9%);
}

h1, h2 { margin-top: 0; text-align: center; }
h1 { margin-bottom: 0.25rem; font-size: 3.2rem; }
h2 { margin-bottom: 1.8rem; font-size: 1.35rem; }

label {
  display: block;
  margin: 1rem 0 0.4rem;
  font-size: 0.9rem;
  font-weight: 650;
}

input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #111;
  border-radius: 8px;
  background: white;
}

input:focus { outline: 3px solid rgb(0 52 217 / 16%); border-color: var(--pgt-blue); }

button {
  width: 100%;
  margin-top: 1.5rem;
  padding: 0.75rem;
  border: 0;
  border-radius: 8px;
  color: white;
  background: var(--pgt-blue);
  cursor: pointer;
  font-weight: 700;
}

button:hover:not(:disabled) { filter: brightness(0.92); }
button:disabled { opacity: 0.65; cursor: wait; }

.message { margin: 1rem 0 0; padding: 0.75rem; border-radius: 8px; text-align: center; }
.error { color: var(--pgt-danger); background: #fef2f2; }
</style>
