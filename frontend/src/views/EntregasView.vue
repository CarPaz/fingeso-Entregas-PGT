<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'

const entregas = ref([])
const cargando = ref(true)
const error = ref(null)

onMounted(async () => {
  try {
    const res = await api.get('/api/entregas')
    entregas.value = res.data
  } catch (e) {
    error.value = 'No se pudieron cargar las entregas.'
    console.error(e)
  } finally {
    cargando.value = false
  }
})

function claseEstado(estado) {
  if (estado === 'APROBADA') return 'badge badge-ok'
  if (estado === 'CORRECCION_REQUERIDA') return 'badge badge-warn'
  return 'badge badge-pending' // PENDIENTE_REVISION u otros
}
</script>

<template>
  <div class="entregas-container">
    <div class="entregas-card">
      <div class="header">
        <h1>Entregas</h1>
        <router-link to="/entregas/nueva" class="btn-nueva">
          + Nueva Entrega
        </router-link>
      </div>

      <p v-if="cargando" class="estado-msg">Cargando...</p>
      <p v-else-if="error" class="estado-msg error">{{ error }}</p>
      <p v-else-if="entregas.length === 0" class="estado-msg">
        Aún no hay entregas registradas.
      </p>

      <table v-else>
        <thead>
          <tr>
            <th>ID</th>
            <th>Tipo</th>
            <th>Fecha</th>
            <th>Estado</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="e in entregas" :key="e.idEntrega">
            <td>{{ e.idEntrega }}</td>
            <td>{{ e.tipoEntrega }}</td>
            <td>{{ e.fechaHora }}</td>
            <td><span :class="claseEstado(e.estado)">{{ e.estado }}</span></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.entregas-container {
  min-height: 100vh;
  background: #f3f4f6;
  padding: 3rem 1rem;
  font-family: system-ui, sans-serif;
}

.entregas-card {
  max-width: 800px;
  margin: 0 auto;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  padding: 2rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

h1 {
  font-size: 1.6rem;
  color: #000000;
  margin: 0;
}

.btn-nueva {
  background: #29AAE1;
  color: white;
  text-decoration: none;
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  transition: background 0.2s;
}

.btn-nueva:hover {
  background: #a75a02;
}

.estado-msg {
  text-align: center;
  color: #6b7280;
  padding: 2rem 0;
}

.estado-msg.error {
  color: #991b1b;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  text-align: left;
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #000000;
  padding: 10px 12px;
  border-bottom: 2px solid #e5e7eb;
}

td {
  padding: 12px;
  border-bottom: 1px solid #f3f4f6;
  color: #81638b;
  font-size: 0.95rem;
}

tr:hover td {
  background: #f9fafb;
}

.badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 600;
}

.badge-ok {
  background: #ecfdf5;
  color: #065f46;
}

.badge-warn {
  background: #fef2f2;
  color: #991b1b;
}

.badge-pending {
  background: #fffbeb;
  color: #92400e;
}
</style>