<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import FiltresPeriode from '@/components/FiltresPeriode.vue'
import CartesTotaux from '@/components/CartesTotaux.vue'
import GraphiqueCategories from '@/components/GraphiqueCategories.vue'
import { listerComptes } from '@/api/comptes'
import { obtenirAgregation } from '@/api/agregations'
import type { Agregation, Compte } from '@/types/budget'
import { filtresTroisDerniersMois } from '@/utils/format'

const route = useRoute()
const compteIdInitial = typeof route.query.compteId === 'string' ? route.query.compteId : ''

const filtres = ref(filtresTroisDerniersMois(compteIdInitial))
const comptes = ref<Compte[]>([])
const agregation = ref<Agregation | null>(null)
const erreur = ref('')

// Interroge /agregations avec les filtres courants ; en cas d'échec, vide le
// résultat et affiche le message d'erreur.
async function charger() {
  erreur.value = ''
  try {
    agregation.value = await obtenirAgregation(filtres.value)
  } catch (e) {
    agregation.value = null
    erreur.value = (e as Error).message
  }
}

// Charge la liste des comptes (pour le filtre) puis l'agrégation initiale.
onMounted(async () => {
  try {
    comptes.value = await listerComptes()
  } catch (e) {
    erreur.value = (e as Error).message
  }
  await charger()
})

watch(filtres, charger, { deep: true })
</script>

<template>
  <section>
    <FiltresPeriode v-model="filtres" :comptes="comptes" />
    <p v-if="erreur" class="message-erreur">{{ erreur }}</p>
    <template v-if="agregation">
      <CartesTotaux :agregation="agregation" />
      <GraphiqueCategories :depenses-par-categorie="agregation.depensesParCategorie" />
    </template>
  </section>
</template>
