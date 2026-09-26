<script setup lang="ts">
import type { Transaction } from '@/types/budget'
import { formaterMontant, libelle } from '@/utils/format'

defineProps<{ transactions: Transaction[] }>()
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
        <tr v-for="(transaction, index) in transactions" :key="index">
          <td class="chiffre">{{ transaction.date }}</td>
          <td>{{ transaction.description }}</td>
          <td>{{ libelle(transaction.typeTransaction) }}</td>
          <td>{{ transaction.categorie ? libelle(transaction.categorie) : '—' }}</td>
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
