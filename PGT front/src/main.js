import { createApp } from 'vue'
import router from './routes'
import App from './App.vue'

// La interfaz restaurada utiliza componentes Vue y estilos propios.
const app = createApp(App)
app.use(router)
app.mount('#app')
