<script setup>
import { RouterView, useRoute } from 'vue-router'
import { ref } from 'vue'
import Sidebar from '@/components/Sidebar.vue' // ajustá la ruta según donde lo tengas

const rail = ref(false)
const route = useRoute()
</script>

<template>
  <v-app>
    <Sidebar v-if="route.meta.requiresAuth" v-model:rail="rail" />

    <v-main>
      <RouterView />
    </v-main>
  </v-app>
</template>

<style>
/* Estilos globales básicos */
:root {
  --primary: #1976D2;
  --secondary: #424242;
  --accent: #82B1FF;
  --error: #FF5252;
  --warning: #FFC107;
  --info: #2196F3;
  --success: #4CAF50;
}

/* * {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
} */

body {
  font-family: 'Roboto', sans-serif;
  line-height: 1.6;
}

/* Ajustes para el contenedor principal */
.fill-height {
  height: 100%;
}

/* Ajustes para el scrollbar */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #aaa;
}

html, body, #app {
  height: 100%;
}

</style>