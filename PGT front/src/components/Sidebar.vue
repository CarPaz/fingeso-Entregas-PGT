<template>
  <v-navigation-drawer v-model:rail="rail" permanent>
    <div class="d-flex justify-end pa-2">
      <v-btn :icon="rail ? 'mdi-chevron-right' : 'mdi-chevron-left'" variant="text" size="small"
        @click="rail = !rail" />
    </div>

    <v-divider />

    <v-list nav density="comfortable">
      <v-list-item
        v-if="['PROFESOR', 'COORDINADOR'].includes(usuario.rol)"
        prepend-icon="mdi-file-document-multiple-outline"
        title="Las Entregas"
        to="/entregas"
        @click="rail = true"
      />

      <v-list-item
        v-if="usuario.rol === 'TESISTA'"
        prepend-icon="mdi-file-document-outline"
        title="Mis Entregas"
        to="/mis-entregas"
        @click="rail = true"
      />

      <v-list-item
        v-if="usuario.rol === 'TESISTA'"
        prepend-icon="mdi-upload-outline"
        title="Subir Entregas"
        to="/subir-entregas"
        @click="rail = true"
      />
    </v-list>

    <template #append>
      <v-divider class="mb-2" />
      <v-list nav density="comfortable">
        <v-list-item prepend-icon="mdi-logout" title="Cerrar Sesión" class="text-error" @click="handleLogout" />
      </v-list>
    </template>
  </v-navigation-drawer>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const rail = defineModel('rail', { default: false })

const router = useRouter()

function handleLogout() {
  localStorage.removeItem('token')
  localStorage.removeItem('usuario')
  router.push('/login')
}

const usuario = ref({})
try {
  usuario.value = JSON.parse(localStorage.getItem('usuario') || '{}')
} catch {
  localStorage.removeItem('token')
  localStorage.removeItem('usuario')
}
</script>
