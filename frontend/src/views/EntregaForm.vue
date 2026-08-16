<script setup>
import { ref } from 'vue'
import axios from 'axios'
import logo from '../assets/mono.jpg'

const tipoEntrega = ref('AVANCE')
const idProcesoTesis = ref(null)
const idHitoEntrega = ref(null)
const idEstudiante = ref(null)
const archivo = ref(null)

const enviando = ref(false)
const mensaje = ref('')
const error = ref('')

function onFileChange(e) {
  const file = e.target.files[0]
  if (file && file.type !== 'application/pdf') {
    error.value = 'Solo se permiten archivos PDF.'
    archivo.value = null
    e.target.value = ''
    return
  }
  error.value = ''
  archivo.value = file
}

async function enviarEntrega() {
  if (!archivo.value) {
    error.value = 'Debes seleccionar un archivo PDF.'
    return
  }

  const formData = new FormData()
  formData.append('tipoEntrega', tipoEntrega.value)
  formData.append('idProcesoTesis', idProcesoTesis.value)
  formData.append('idHitoEntrega', idHitoEntrega.value)
  formData.append('idEstudiante', idEstudiante.value)
  formData.append('archivo', archivo.value)

  enviando.value = true
  mensaje.value = ''
  error.value = ''

  try {
    await axios.post('/api/entregas', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    mensaje.value = 'Entrega registrada correctamente.'
    // reset simple
    idProcesoTesis.value = null
    idHitoEntrega.value = null
    idEstudiante.value = null
    archivo.value = null
  } catch (e) {
    error.value = 'Ocurrió un error al enviar la entrega.'
    console.error(e)
  } finally {
    enviando.value = false
  }
}
</script>

<template>
  <div class="entrega-form">
    <img :src="logo" alt="Logo" class="logo" />
    <h1>PGT</h1>
    <h2>Presentar Entrega</h2>

    <form @submit.prevent="enviarEntrega">
      <div class="campo">
        <label>Tipo de entrega</label>
        <select v-model="tipoEntrega">
          <option value="AVANCE">Avance</option>
          <option value="FINAL">Final</option>
        </select>
      </div>

      <div class="campo">
        <label>ID Estudiante</label>
        <input type="number" v-model="idEstudiante" required />
      </div>

      <div class="campo">
        <label>Archivo (PDF)</label>
        <label for="archivo-input" class="file-button">
            {{ archivo ? archivo.name : 'Seleccionar archivo PDF' }}
        </label>
        <input
            id="archivo-input"
            type="file"
            accept="application/pdf"
            @change="onFileChange"
            required
            class="file-hidden"
        />
      </div>

      <button type="submit" :disabled="enviando">
        {{ enviando ? 'Enviando...' : 'Enviar Entrega' }}
      </button>

      <p v-if="mensaje" class="ok">{{ mensaje }}</p>
      <p v-if="error" class="error">{{ error }}</p>
    </form>
  </div>
</template>

<style scoped>
.entrega-form {
  max-width: 480px;
  margin: 1rem auto;
  padding: 2rem;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  font-family: system-ui, sans-serif;
}

h1 {
  font-size: 5rem;
  margin-bottom: 1.5rem;
  color: #1f2937;
  text-align: center;
}

h2 {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: #1f2937;
  text-align: center;
}

.campo {
  margin-bottom: 1.2rem;
  display: flex;
  flex-direction: column;
}

label {
  font-weight: 600;
  margin-bottom: 6px;
  color: #374151;
  font-size: 0.9rem;
}

input, select {
  padding: 10px 12px;
  border: 1px solid #000000;
  border-radius: 8px;
  font-size: 0.95rem;
  transition: border-color 0.2s;
}

input:focus, select:focus {
  outline: none;
  border-color: #302247;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.15);
}

input[type="file"] {
  padding: 10px 12px;
}

button {
  width: 100%;
  padding: 12px;
  margin-top: 0.5rem;
  background: #6911bb;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s;
}

button:hover:not(:disabled) {
  background: #2d0750;
}

button:disabled {
  background: #777f8b;
  cursor: not-allowed;
}

.ok {
  margin-top: 1rem;
  padding: 10px;
  background: #ecfdf5;
  color: #065f46;
  border-radius: 8px;
  font-size: 0.9rem;
}

.error {
  margin-top: 1rem;
  padding: 10px;
  background: #fef2f2;
  color: #991b1b;
  border-radius: 8px;
  font-size: 0.9rem;
}

.file-hidden {
  display: none;
}

.file-button {
  display: block;
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  background: #f9fafb;
  color: #374151;
  font-size: 0.95rem;
  transition: background 0.2s, border-color 0.2s;
  box-sizing: border-box;
}

.file-button:hover {
  background: #f3f4f6;
  border-color: #9ca3af;
}

.logo {
  position: fixed;
  top: 16px;
  left: 16px;
  width: 200px;
  z-index: 10;
}
</style>

