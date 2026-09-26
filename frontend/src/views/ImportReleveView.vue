<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import TableauTransactions from '@/components/TableauTransactions.vue'
import { listerComptes } from '@/api/comptes'
import { importerReleve } from '@/api/releves'
import type { Compte, Releve, Transaction } from '@/types/budget'
import { libelle } from '@/utils/format'

const route = useRoute()

const comptes = ref<Compte[]>([])
const compteId = ref(typeof route.query.compteId === 'string' ? route.query.compteId : '')
const fichier = ref<File | null>(null)
const enCours = ref(false)
const erreur = ref('')
const releve = ref<Releve | null>(null)

// Transactions du relevé importé, complétées avec le compte pour réutiliser
// le même tableau que la page Transactions.
const transactionsImportees = computed<Transaction[]>(() => {
  if (!releve.value) {
    return []
  }
  const compte = releve.value.compte
  return releve.value.transactions.map((transaction) => ({
    ...transaction,
    compteNom: compte.nom,
    titulaire: compte.titulaire,
  }))
})

onMounted(async () => {
  try {
    comptes.value = await listerComptes()
  } catch (e) {
    erreur.value = (e as Error).message
  }
})

function choisirFichier(evenement: Event) {
  const fichiers = (evenement.target as HTMLInputElement).files
  fichier.value = fichiers && fichiers.length > 0 ? fichiers[0]! : null
}

// Envoie le PDF au backend (extraction, masquage, catégorisation par IA,
// sauvegarde) puis affiche le résumé ; en cas d'échec, affiche l'erreur.
async function importer() {
  if (!fichier.value || compteId.value === '') {
    return
  }
  enCours.value = true
  erreur.value = ''
  releve.value = null
  try {
    releve.value = await importerReleve(compteId.value, fichier.value)
  } catch (e) {
    erreur.value = (e as Error).message
  } finally {
    enCours.value = false
  }
}
</script>

<template>
  <section>
    <div class="carte formulaire">
      <h2>Importer un relevé PDF</h2>
      <p v-if="comptes.length === 0 && !erreur" class="message-info">
        Aucun compte : <RouterLink to="/comptes/nouveau">créer un compte</RouterLink> d'abord.
      </p>
      <form v-else @submit.prevent="importer">
        <label>
          Compte
          <select v-model="compteId" required>
            <option value="" disabled>Choisir un compte</option>
            <option v-for="compte in comptes" :key="compte.id" :value="compte.id">
              {{ compte.nom }} ({{ libelle(compte.titulaire) }}, {{ libelle(compte.typeCompte) }})
            </option>
          </select>
        </label>
        <label>
          Relevé (PDF)
          <input type="file" accept="application/pdf,.pdf" required @change="choisirFichier" />
        </label>
        <p class="message-info">
          Les numéros de compte et adresses sont masqués avant l'envoi à l'IA. L'analyse peut prendre
          jusqu'à une minute. Un même relevé importé deux fois crée des transactions en double.
        </p>
        <p v-if="erreur" class="message-erreur">{{ erreur }}</p>
        <div class="actions">
          <button type="submit" class="bouton" :disabled="enCours || !fichier || compteId === ''">
            {{ enCours ? 'Analyse en cours…' : 'Importer' }}
          </button>
        </div>
      </form>
    </div>

    <div v-if="releve" class="resultat">
      <p class="succes">
        {{ releve.transactions.length }} transactions importées sur « {{ releve.compte.nom }} »<template
          v-if="releve.periodeDebut"
          >, du <span class="chiffre">{{ releve.periodeDebut }}</span> au
          <span class="chiffre">{{ releve.periodeFin }}</span></template
        >.
      </p>
      <TableauTransactions :transactions="transactionsImportees" />
    </div>
  </section>
</template>

<style scoped>
.formulaire {
  max-width: 560px;
}

h2 {
  font-size: 18px;
  margin-bottom: 1rem;
}

form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.actions {
  display: flex;
  justify-content: flex-end;
}

.resultat {
  margin-top: 1.5rem;
}

.succes {
  color: var(--positif);
  font-weight: 500;
}
</style>
