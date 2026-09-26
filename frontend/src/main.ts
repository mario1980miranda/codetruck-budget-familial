import { createApp } from 'vue'
import App from './App.vue'
import routeur from './router'
import './assets/main.css'

createApp(App).use(routeur).mount('#app')
