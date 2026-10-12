<script setup lang="ts">
import { computed } from 'vue'
import { Doughnut } from 'vue-chartjs'
import { ArcElement, Chart as ChartJS, Legend, Tooltip, type TooltipItem } from 'chart.js'
import type { CategorieDepense } from '@/types/budget'
import { formaterMontant, libelle } from '@/utils/format'

ChartJS.register(ArcElement, Tooltip, Legend)

const COULEURS_CATEGORIES: Record<CategorieDepense, string> = {
  ALIMENTATION: '#3F6249',
  PHARMACIE: '#7A3B46',
  ANIMAUX: '#5E7F8C',
  LOGEMENT: '#4A5A70',
  TRANSPORT: '#B08D57',
  LOISIRS: '#8B5A8F',
  AUTRE: '#8A8378',
}
const PALETTE_SUPPLEMENTAIRE = ['#C2703D', '#2F7F7A', '#9C7A2E', '#5B4E8C', '#A04E6E', '#6F8B3A', '#3D6E9E', '#7D5A44']

const props = defineProps<{ depensesParCategorie: Partial<Record<CategorieDepense, number>> }>()

const categoriesPresentes = computed(() =>
  Object.keys(props.depensesParCategorie).filter((categorie) => (props.depensesParCategorie[categorie] ?? 0) > 0),
)

// Couleur fixe pour les catégories d'origine ; une catégorie créée ensuite
// prend une couleur de PALETTE_SUPPLEMENTAIRE selon sa position dans le graphique.
function couleur(categorie: CategorieDepense, position: number): string {
  return COULEURS_CATEGORIES[categorie] ?? PALETTE_SUPPLEMENTAIRE[position % PALETTE_SUPPLEMENTAIRE.length]!
}

const donnees = computed(() => ({
  labels: categoriesPresentes.value.map(libelle),
  datasets: [
    {
      data: categoriesPresentes.value.map((categorie) => props.depensesParCategorie[categorie] ?? 0),
      backgroundColor: categoriesPresentes.value.map(couleur),
      borderWidth: 2,
      borderColor: '#FAF7F0',
    },
  ],
}))

const options = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { position: 'right' as const },
    tooltip: {
      callbacks: {
        label: (element: TooltipItem<'doughnut'>) => `${element.label} : ${formaterMontant(element.parsed)}`,
      },
    },
  },
}
</script>

<template>
  <div class="carte">
    <h2>Dépenses par catégorie</h2>
    <p v-if="categoriesPresentes.length === 0" class="message-info">Aucune dépense sur la période.</p>
    <div v-else class="zone-graphique">
      <Doughnut :data="donnees" :options="options" />
    </div>
  </div>
</template>

<style scoped>
h2 {
  font-size: 18px;
  margin-bottom: 1rem;
}

.zone-graphique {
  position: relative;
  height: 320px;
}
</style>
