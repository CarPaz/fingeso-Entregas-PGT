<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const entregas = ref([])
const cargando = ref(true)
const error = ref(null)

onMounted(async () => {
  try {
    const res = await axios.get('/api/entregas') // ajustar al endpoint real del backend
    entregas.value = res.data
  } catch (e) {
    error.value = 'No se pudieron cargar las entregas.'
    console.error(e)
  } finally {
    cargando.value = false
  }
})
</script>

<template>
  <div class="entregas">
    <h1>Entregas</h1>

    <p v-if="cargando">Cargando...</p>
    <p v-else-if="error">{{ error }}</p>

    <table v-else>
      <thead>
        <tr>
          <th>ID</th>
          <th>Estado</th>
          <th>Fecha</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="e in entregas" :key="e.id">
          <td>{{ e.id }}</td>
          <td>{{ e.estado }}</td>
          <td>{{ e.fecha }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.entregas {
  padding: 2rem;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}
</style>