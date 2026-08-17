<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// La navegación visible depende del rol guardado después del login.
const usuario = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('usuario') || '{}')
  } catch {
    return {}
  }
})

const esTesista = computed(() => usuario.value.rol === 'TESISTA')

function cerrarSesion() {
  localStorage.removeItem('token')
  localStorage.removeItem('usuario')
  router.replace('/login')
}
</script>

<template>
  <header class="navbar">
    <div class="brand">
      <div class="brand-logo" aria-hidden="true">PGT</div>
      <span>PGT · Entregas de Tesis</span>
    </div>

    <nav class="navigation" aria-label="Navegación principal">
      <RouterLink v-if="esTesista" to="/mis-entregas">Entregas</RouterLink>
      <RouterLink v-else to="/entregas">Entregas</RouterLink>
      <RouterLink v-if="esTesista" to="/subir-entregas">Nueva Entrega</RouterLink>
      <button type="button" @click="cerrarSesion">Cerrar sesión</button>
    </nav>
  </header>
</template>

<style scoped>
.navbar {
  position: fixed;
  z-index: 100;
  top: 0;
  right: 0;
  left: 0;
  min-height: 68px;
  padding: 0.7rem 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  color: white;
  background: var(--pgt-blue);
  box-shadow: 0 2px 8px rgb(0 0 0 / 16%);
}

.brand,
.navigation {
  display: flex;
  align-items: center;
}

.brand {
  gap: 12px;
  font-size: 1.05rem;
  font-weight: 700;
}

.brand-logo {
  width: 42px;
  height: 42px;
  display: grid;
  place-items: center;
  border-radius: 5px;
  color: var(--pgt-blue);
  background: white;
  font-size: 0.72rem;
  font-weight: 800;
}

.navigation {
  gap: 1.5rem;
}

.navigation a {
  padding: 0.45rem 0;
  color: white;
  border-bottom: 2px solid transparent;
  text-decoration: none;
  font-size: 0.92rem;
  font-weight: 650;
}

.navigation a:hover,
.navigation a.router-link-active {
  border-bottom-color: white;
}

.navigation button {
  padding: 0.45rem 1rem;
  border: 1px solid white;
  border-radius: 7px;
  color: white;
  background: transparent;
  cursor: pointer;
  font-weight: 650;
}

.navigation button:hover {
  background: rgb(255 255 255 / 14%);
}

@media (max-width: 680px) {
  .navbar {
    padding-inline: 1rem;
    align-items: flex-start;
  }

  .brand span {
    display: none;
  }

  .navigation {
    gap: 0.75rem;
    flex-wrap: wrap;
    justify-content: flex-end;
  }
}
</style>
