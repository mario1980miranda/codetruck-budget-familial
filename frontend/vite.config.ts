import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// En développement, /api/* est redirigé vers le backend Spring Boot (port 8080)
// en retirant le préfixe /api : pas besoin de configurer CORS côté Java.
export default defineConfig({
  plugins: [vue()],
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
        rewrite: (chemin) => chemin.replace(/^\/api/, ''),
      },
    },
  },
})
