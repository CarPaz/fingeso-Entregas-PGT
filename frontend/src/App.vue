<script setup>
import { RouterView, useRoute } from 'vue-router'
import NavBar from './components/NavBar.vue'

const route = useRoute()
</script>

<template>
  <NavBar v-if="route.name !== 'login'" />
  <div class="page-content">
    <RouterView />
  </div>
</template>