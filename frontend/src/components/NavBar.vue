<script setup>
import { useRouter } from 'vue-router'
import logo from '../assets/mono.jpg'

const router = useRouter()

function cerrarSesion() {
  localStorage.removeItem('token')
  localStorage.removeItem('usuario')
  router.push('/')
}
</script>

<template>
  <header class="navbar">
    <div class="navbar-left">
      <img :src="logo" alt="Logo" class="navbar-logo" />
      <span class="navbar-title">PGT · Entregas de Tesis</span>
    </div>

    <nav class="navbar-right">
      <router-link to="/entregas" class="nav-link">Entregas</router-link>
      <router-link to="/entregas/nueva" class="nav-link">Nueva Entrega</router-link>
      <button class="logout-btn" @click="cerrarSesion">Cerrar sesión</button>
    </nav>
  </header>
</template>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: #0034D9;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.8rem 2rem;
  font-family: system-ui, sans-serif;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.navbar-logo {
  height: 40px;
  width: auto;
  border-radius: 4px;
}

.navbar-title {
  font-weight: 700;
  font-size: 1.1rem;
}

.navbar-right {
  display: flex;
  gap: 1.5rem;
}

.nav-link {
  color: white;
  text-decoration: none;
  font-weight: 600;
  font-size: 0.95rem;
  opacity: 0.9;
  transition: opacity 0.2s;
}

.nav-link:hover {
  opacity: 1;
  text-decoration: underline;
}

.router-link-active {
  border-bottom: 2px solid #ffffff; /* naranjo, como el subrayado de "tesis" en la imagen */
  padding-bottom: 4px;
}

.logout-btn {
  background: #0034D9;
  border: 1px solid rgb(255, 255, 255);
  color: white;
  padding: 6px 14px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  font-size: 0.85rem;
}

.logout-btn:hover {
  background: #a75a02;
}
</style>