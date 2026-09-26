<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import FiltresPeriode from '@/components/FiltresPeriode.vue'
import TableauTransactions from '@/components/TableauTransactions.vue'
import { listerComptes } from '@/api/comptes'
import { listerTransactions } from '@/api/transactions'
import { CATEGORIES, type CategorieDepense, type Compte, type Transaction } from '@/types/budget'
import { filtresTroisDerniersMois, libelle } from '@/utils/format'

const filtres = ref(filtresTroisDerniersMois())
const categorie = ref<CategorieDepense | ''>('')
const comptes = ref<Compte[]>([])
const transactions = ref<Transaction[]>([])
const erreur = ref('')

// Interroge /transactions avec les filtres et la catégorie ; en cas d'échec,
// vide le tableau et affiche le message d'erreur.
async function charger() {
  erreur.value = ''
  try {
    transactions.value = await listerTransactions(filtres.value, categorie.value)
  } catch (e) {
    transactions.value = []
    erreur.value = (e as Error).message
  }
}

// Charge la liste des comptes (pour le filtre) puis les transactions initiales.
onMounted(async () => {
  try {
    comptes.value = await listerComptes()
  } catch (e) {
    erreur.value = (e as Error).message
  }
  await charger()
})

watch([filtres, categorie], charger, { deep: true })
</script>

<template>
  <section>
    <FiltresPeriode v-model="filtres" :comptes="comptes">
      <label>
        Catégorie
        <select v-model="categorie">
          <option value="">Toutes</option>
          <option v-for="valeur in CATEGORIES" :key="valeur" :value="valeur">{{ libelle(valeur) }}</option>
        </select>
      </label>
    </FiltresPeriode>
    <p v-if="erreur" class="message-erreur">{{ erreur }}</p>
    <TableauTransactions v-else :transactions="transactions" />
  </section>
</template>
