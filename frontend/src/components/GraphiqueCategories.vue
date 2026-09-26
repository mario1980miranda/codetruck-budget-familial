<script setup lang="ts">
import { computed } from 'vue'
import { Doughnut } from 'vue-chartjs'
import { ArcElement, Chart as ChartJS, Legend, Tooltip, type TooltipItem } from 'chart.js'
import { CATEGORIES, type CategorieDepense } from '@/types/budget'
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

const props = defineProps<{ depensesParCategorie: Partial<Record<CategorieDepense, number>> }>()

const categoriesPresentes = computed(() =>
  CATEGORIES.filter((categorie) => (props.depensesParCategorie[categorie] ?? 0) > 0),
)

const donnees = computed(() => ({
  labels: categoriesPresentes.value.map(libelle),
  datasets: [
    {
      data: categoriesPresentes.value.map((categorie) => props.depensesParCategorie[categorie] ?? 0),
      backgroundColor: categoriesPresentes.value.map((categorie) => COULEURS_CATEGORIES[categorie]),
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
