import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/actuator': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  build: {
    rollupOptions: {
      output: {
        /*
         * Separamos las librerías grandes del código propio. Las vistas ya se
         * cargan bajo demanda desde el router, por lo que el navegador evita
         * descargar toda la aplicación antes de mostrar el login.
        */
        manualChunks(id) {
          if (id.includes('node_modules/vuetify')) {
            const ruta = id.replaceAll('\\', '/')
            const componente = ruta.match(/vuetify\/lib\/components\/([^/]+)/)?.[1]

            if (componente) {
              const inicial = componente.replace(/^V/, '').charAt(0).toUpperCase()
              if (inicial <= 'F') return 'vendor-vuetify-components-a-f'
              if (inicial <= 'L') return 'vendor-vuetify-components-g-l'
              if (inicial <= 'R') return 'vendor-vuetify-components-m-r'
              return 'vendor-vuetify-components-s-z'
            }

            return 'vendor-vuetify-core'
          }
          if (id.includes('node_modules/@mdi')) return 'vendor-icons'
          if (id.includes('node_modules/vue')) return 'vendor-vue'
          if (id.includes('node_modules/axios')) return 'vendor-http'
          return undefined
        },
      },
    },
  },
})
