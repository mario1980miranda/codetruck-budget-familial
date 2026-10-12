import { createRouter, createWebHistory } from 'vue-router'
import TableauDeBordView from '@/views/TableauDeBordView.vue'
import TransactionsView from '@/views/TransactionsView.vue'
import ComptesView from '@/views/ComptesView.vue'
import NouveauCompteView from '@/views/NouveauCompteView.vue'
import ImportReleveView from '@/views/ImportReleveView.vue'
import CategoriesView from '@/views/CategoriesView.vue'

const routeur = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'tableau-de-bord', component: TableauDeBordView },
    { path: '/transactions', name: 'transactions', component: TransactionsView },
    { path: '/comptes', name: 'comptes', component: ComptesView },
    { path: '/comptes/nouveau', name: 'nouveau-compte', component: NouveauCompteView },
    { path: '/categories', name: 'categories', component: CategoriesView },
    { path: '/import', name: 'import', component: ImportReleveView },
  ],
})

export default routeur
