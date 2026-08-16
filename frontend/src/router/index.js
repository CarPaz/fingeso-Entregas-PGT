import { createRouter, createWebHistory } from 'vue-router'
import EntregasView from '../views/EntregasView.vue'
import EntregaForm from '../views/EntregaForm.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/entregas', name: 'entregas', component: EntregasView },
    { path: '/entregas/nueva', name: 'nueva-entrega', component: EntregaForm }
  ]
})

export default router