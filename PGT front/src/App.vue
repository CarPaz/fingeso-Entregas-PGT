<script setup>
import { RouterView, useRoute } from 'vue-router'
import NavBar from '@/components/NavBar.vue'

const route = useRoute()
</script>

<template>
  <!-- El login ocupa toda la pantalla; las demás vistas comparten la cabecera. -->
  <NavBar v-if="route.meta.requiresAuth" />
  <main :class="{ 'page-with-navbar': route.meta.requiresAuth }">
    <RouterView />
  </main>
</template>

<style>
:root {
  --pgt-blue: #0034d9;
  --pgt-cyan: #29aae1;
  --pgt-bg: #f3f4f6;
  --pgt-text: #111827;
  --pgt-danger: #991b1b;
  --pgt-success: #065f46;
}

* {
  box-sizing: border-box;
}

html,
body,
#app {
  min-height: 100%;
  margin: 0;
}

body {
  color: var(--pgt-text);
  background: var(--pgt-bg);
  font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
}

.page-with-navbar {
  min-height: 100vh;
  padding-top: 68px;
}

button,
input,
select {
  font: inherit;
}
</style>
