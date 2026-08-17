import { createRouter, createWebHistory } from 'vue-router'
import Login from "@/views/Login.vue";
import LasEntregas from "@/views/LasEntregas.vue";
import SubirEntregas from "@/views/SubirEntregas.vue";

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'login',
        component: Login,
        meta: { title: 'Iniciar sesión' }
    },
    {
        path: '/entregas',
        name: 'entregas',
        component: LasEntregas,
        meta: {
            title: 'Las Entregas',
            requiresAuth: true,
            roles: ['PROFESOR', 'COORDINADOR'],
        }
    },
    {
        path: '/subir-entregas',
        name: 'subir-entregas',
        component: SubirEntregas,
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

    const token = localStorage.getItem('token');
    let usuario = {};
    try {
        usuario = JSON.parse(localStorage.getItem('usuario') || '{}');
    } catch {
        localStorage.removeItem('token');
        localStorage.removeItem('usuario');
    }

    const rolesValidos = ['TESISTA', 'PROFESOR', 'COORDINADOR'];
    const sesionValida = token && rolesValidos.includes(usuario.rol);

    if (token && !sesionValida) {
        localStorage.removeItem('token');
        localStorage.removeItem('usuario');
    }

    const rutaInicial = usuario.rol === 'TESISTA' ? '/subir-entregas' : '/entregas';

    if (to.meta.requiresAuth && !sesionValida) {
        return '/login';
    }

    if (to.meta.roles && !to.meta.roles.includes(usuario.rol)) {
        return rutaInicial;
    }

    if (to.name === 'login' && sesionValida) {
        return rutaInicial;
    }
});

export default router
