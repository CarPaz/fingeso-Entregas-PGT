<script setup>
import { ref } from 'vue'
import api from '../api'
import logo from '../assets/mono.jpg'

const tipoEntrega = ref('AVANCE')
const idProcesoTesis = ref(null)
const idHitoEntrega = ref(null)
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

function quitarArchivo() {
  archivo.value = null
  const input = document.getElementById('archivo-input')
  if (input) input.value = ''
}

async function enviarEntrega() {
  if (!archivo.value) {
    error.value = 'Debes seleccionar un archivo PDF.'
    return
  }

  const idEstudiante = Number(localStorage.getItem('idUsuario'))

  const entregaData = {
    idProcesoTesis: Number(idProcesoTesis.value),
    idHitoEntrega: Number(idHitoEntrega.value),
    idEstudiante,
    tipoEntrega: tipoEntrega.value
  }

  const formData = new FormData()
  formData.append(
    'entrega',
    new Blob([JSON.stringify(entregaData)], { type: 'application/json' })
  )
  formData.append('archivo', archivo.value)

  const endpoint = tipoEntrega.value === 'FINAL'
    ? '/api/entregas/final'
    : '/api/entregas/avance'

  enviando.value = true
  mensaje.value = ''
  error.value = ''

  try {
    await api.post(endpoint, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    mensaje.value = 'Entrega registrada correctamente.'
    idProcesoTesis.value = null
    idHitoEntrega.value = null
    quitarArchivo()
  } catch (e) {
    error.value = e.response?.data?.mensaje || 'Ocurrió un error al enviar la entrega.'
    console.error(e)
  } finally {
    enviando.value = false
  }
}
</script>

<template>
  <div class="entrega-form">
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
        <label>ID Proceso Tesis</label>
        <input type="number" v-model="idProcesoTesis" required />
      </div>

      <div class="campo">
        <label>ID Hito Entrega</label>
        <input type="number" v-model="idHitoEntrega" required />
      </div>

      <div class="campo">
        <label>Archivo (PDF)</label>
        <div class="file-row">
          <label for="archivo-input" class="file-button">
            {{ archivo ? archivo.name : 'Seleccionar archivo PDF' }}
          </label>
          <button
            v-if="archivo"
            type="button"
            class="file-clear"
            @click="quitarArchivo"
            title="Quitar archivo"
          >
            ✕
          </button>
        </div>
        <input
          id="archivo-input"
          type="file"
          accept="application/pdf"
          @change="onFileChange"
          :required="!archivo"
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
  margin: 5rem auto;
  padding: 2rem;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  font-family: system-ui, sans-serif;
}

h2 {
  font-size: 2rem;
  margin-bottom: 1.5rem;
  color: #000000;
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
  color: #000000;
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
  border-color: #0034D9;
  box-shadow: 0 0 0 3px rgba(0, 0, 0, 0.15);
}

input[type="file"] {
  padding: 10px 12px;
}

button {
  width: 100%;
  padding: 12px;
  margin-top: 0.5rem;
  background: #0034D9;
  color: rgb(255, 255, 255);
  border: rgb(0, 0, 0);
  border-radius: 8px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s;
}

button:hover:not(:disabled) {
  background: #a75a02;
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
  border: 0px solid #000000;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  background: #29AAE1;
  color: #ffffff;
  font-size: 0.95rem;
  transition: background 0.2s, border-color 0.2s;
  box-sizing: border-box;
}

.file-button:hover {
  background: #a75a02;
  border-color: #9ca3af;
}

.file-row {
  display: flex;
  gap: 8px;
  align-items: stretch;
}

.file-row .file-button {
  flex: 1;
  margin: 0;
  box-sizing: border-box;
}

.file-clear {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  background: #29AAE1;
  color: #000000;
  border: 1px solid #000000;
  border-radius: 8px;
  width: 44px;
  font-size: 1rem;
  font-weight: bold;
  font-family: inherit;
  line-height: normal;
  cursor: pointer;
  transition: background 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>

