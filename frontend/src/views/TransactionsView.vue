<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import FiltresPeriode from '@/components/FiltresPeriode.vue'
import TableauTransactions from '@/components/TableauTransactions.vue'
import { listerCategories } from '@/api/categories'
import { listerComptes } from '@/api/comptes'
import { listerTransactions, modifierCategorieTransaction } from '@/api/transactions'
import type { Categorie, CategorieDepense, Compte, Transaction } from '@/types/budget'
import { filtresTroisDerniersMois, libelle } from '@/utils/format'

const filtres = ref(filtresTroisDerniersMois())
const categorie = ref<CategorieDepense | ''>('')
const comptes = ref<Compte[]>([])
const categories = ref<Categorie[]>([])
const transactions = ref<Transaction[]>([])
const erreur = ref('')
const erreurModification = ref('')

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

// Envoie la nouvelle catégorie au backend puis recharge le tableau : en cas de
// succès la ligne reflète la base (et respecte le filtre catégorie) ; en cas
// d'échec le message est affiché et le rechargement remet l'ancienne valeur.
async function modifierCategorie(id: string, nouvelleCategorie: CategorieDepense) {
  erreurModification.value = ''
  try {
    await modifierCategorieTransaction(id, nouvelleCategorie)
  } catch (e) {
    erreurModification.value = (e as Error).message
  }
  await charger()
}

// Charge les comptes et les catégories (pour les filtres et les listes
// déroulantes du tableau) puis les transactions initiales.
onMounted(async () => {
  try {
    comptes.value = await listerComptes()
    categories.value = await listerCategories()
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
          <option v-for="valeur in categories" :key="valeur.id" :value="valeur.nom">{{ libelle(valeur.nom) }}</option>
        </select>
      </label>
    </FiltresPeriode>
    <p v-if="erreur" class="message-erreur">{{ erreur }}</p>
    <template v-else>
      <p v-if="erreurModification" class="message-erreur">{{ erreurModification }}</p>
      <TableauTransactions :transactions="transactions" :categories="categories" @modifier-categorie="modifierCategorie" />
    </template>
  </section>
</template>
