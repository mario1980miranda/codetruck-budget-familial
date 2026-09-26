<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { listerComptes } from '@/api/comptes'
import type { Compte } from '@/types/budget'
import { libelle } from '@/utils/format'

const comptes = ref<Compte[]>([])
const erreur = ref('')

onMounted(async () => {
  try {
    comptes.value = await listerComptes()
  } catch (e) {
    erreur.value = (e as Error).message
  }
})
</script>

<template>
  <section>
    <div class="barre">
      <RouterLink to="/comptes/nouveau" class="bouton">Nouveau compte</RouterLink>
    </div>
    <p v-if="erreur" class="message-erreur">{{ erreur }}</p>
    <p v-else-if="comptes.length === 0" class="message-info">Aucun compte enregistré.</p>
    <div v-else class="tableau-defilant">
      <table>
        <thead>
          <tr>
            <th>Nom</th>
            <th>Titulaire</th>
            <th>Type</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="compte in comptes" :key="compte.id">
            <td>{{ compte.nom }}</td>
            <td>{{ libelle(compte.titulaire) }}</td>
            <td>{{ libelle(compte.typeCompte) }}</td>
            <td class="liens">
              <RouterLink :to="{ path: '/import', query: { compteId: compte.id } }">Importer un relevé</RouterLink>
              <RouterLink :to="{ path: '/', query: { compteId: compte.id } }">Voir le tableau de bord</RouterLink>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<style scoped>
.barre {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 1rem;
}

.liens {
  display: flex;
  gap: 1.25rem;
}

.liens a {
  color: var(--or);
}
</style>
