import { createRouter, createWebHistory } from 'vue-router'
import { leerSesion, resolverRedireccion } from '@/auth/session'

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('@/views/Login.vue'),
        meta: { title: 'Iniciar sesión' }
    },
    {
        path: '/entregas',
        name: 'entregas',
        component: () => import('@/views/LasEntregas.vue'),
        meta: {
            title: 'Las Entregas',
            requiresAuth: true,
            roles: ['PROFESOR', 'COORDINADOR'],
        }
    },
    {
        path: '/mis-entregas',
        name: 'mis-entregas',
        component: () => import('@/views/MisEntregas.vue'),
        meta: { title: 'Mis Entregas', requiresAuth: true, roles: ['TESISTA'] }
    },
    {
        path: '/subir-entregas',
        name: 'subir-entregas',
        component: () => import('@/views/SubirEntregas.vue'),
        meta: { title: 'Subir Entregas', requiresAuth: true, roles: ['TESISTA'] }
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('@/views/NotFound.vue'),
        meta: { title: 'Página no encontrada' }
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

router.beforeEach((to) => {
    document.title = to.meta.title || 'Plataforma de Gestión de Tesistas';

    return resolverRedireccion(to, leerSesion())
});

export default router
