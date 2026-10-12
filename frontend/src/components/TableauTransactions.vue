<script setup lang="ts">
import type { Categorie, CategorieDepense, Transaction } from '@/types/budget'
import { formaterMontant, libelle } from '@/utils/format'

defineProps<{ transactions: Transaction[]; categories: Categorie[] }>()

const emit = defineEmits<{ 'modifier-categorie': [id: string, categorie: CategorieDepense] }>()

function surChangementCategorie(id: string, evenement: Event) {
  emit('modifier-categorie', id, (evenement.target as HTMLSelectElement).value as CategorieDepense)
}
</script>

<template>
  <p v-if="transactions.length === 0" class="message-info">Aucune transaction sur la période.</p>
  <div v-else class="tableau-defilant">
    <table>
      <thead>
        <tr>
          <th>Date</th>
          <th>Description</th>
          <th>Type</th>
          <th>Catégorie</th>
          <th>Compte</th>
          <th>Titulaire</th>
          <th class="montant">Montant</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="transaction in transactions" :key="transaction.id">
          <td class="chiffre">{{ transaction.date }}</td>
          <td>{{ transaction.description }}</td>
          <td>{{ libelle(transaction.typeTransaction) }}</td>
          <td>
            <select
              v-if="transaction.typeTransaction === 'DEPENSE'"
              :value="transaction.categorie ?? ''"
              aria-label="Catégorie"
              @change="surChangementCategorie(transaction.id, $event)"
            >
              <option v-if="!transaction.categorie" value="" disabled>—</option>
              <option v-for="valeur in categories" :key="valeur.id" :value="valeur.nom">{{ libelle(valeur.nom) }}</option>
            </select>
            <template v-else>—</template>
          </td>
          <td>{{ transaction.compteNom }}</td>
          <td>{{ libelle(transaction.titulaire) }}</td>
          <td class="montant chiffre">{{ formaterMontant(transaction.montant) }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.montant {
  text-align: right;
}
</style>
