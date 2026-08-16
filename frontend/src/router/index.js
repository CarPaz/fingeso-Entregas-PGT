import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import EntregasView from '../views/EntregasView.vue'
import EntregaForm from '../views/EntregaForm.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'login', component: LoginView },
    { path: '/entregas', name: 'entregas', component: EntregasView, meta: { requiresAuth: true } },
    { path: '/entregas/nueva', name: 'nueva-entrega', component: EntregaForm, meta: { requiresAuth: true } }
  ]
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  if (to.meta.requiresAuth && !token) {
    next('/')
  } else if (to.name === 'login' && token) {
    next('/entregas')
  } else {
    next()
  }
})

export default router