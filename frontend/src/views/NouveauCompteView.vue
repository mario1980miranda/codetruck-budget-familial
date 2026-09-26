<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { creerCompte } from '@/api/comptes'
import { TITULAIRES, TYPES_COMPTE, type TitulaireCompte, type TypeCompte } from '@/types/budget'
import { libelle } from '@/utils/format'

const routeur = useRouter()

const nom = ref('')
const titulaire = ref<TitulaireCompte>('MARIO')
const typeCompte = ref<TypeCompte>('COURANT')
const enCours = ref(false)
const erreur = ref('')

// Crée le compte puis enchaîne directement sur l'import d'un relevé pour ce
// compte ; en cas d'échec, reste sur le formulaire et affiche l'erreur.
async function enregistrer() {
  enCours.value = true
  erreur.value = ''
  try {
    const compte = await creerCompte({ nom: nom.value.trim(), titulaire: titulaire.value, typeCompte: typeCompte.value })
    await routeur.push({ path: '/import', query: { compteId: compte.id } })
  } catch (e) {
    erreur.value = (e as Error).message
  } finally {
    enCours.value = false
  }
}
</script>

<template>
  <section class="carte formulaire">
    <h2>Nouveau compte</h2>
    <form @submit.prevent="enregistrer">
      <label>
        Nom
        <input v-model="nom" type="text" maxlength="100" required placeholder="ex. Carte Visa Cristina" />
      </label>
      <label>
        Titulaire
        <select v-model="titulaire">
          <option v-for="valeur in TITULAIRES" :key="valeur" :value="valeur">{{ libelle(valeur) }}</option>
        </select>
      </label>
      <label>
        Type de compte
        <select v-model="typeCompte">
          <option v-for="valeur in TYPES_COMPTE" :key="valeur" :value="valeur">{{ libelle(valeur) }}</option>
        </select>
      </label>
      <p v-if="erreur" class="message-erreur">{{ erreur }}</p>
      <div class="actions">
        <RouterLink to="/comptes">Annuler</RouterLink>
        <button type="submit" class="bouton" :disabled="enCours || nom.trim() === ''">
          {{ enCours ? 'Enregistrement…' : 'Créer le compte' }}
        </button>
      </div>
    </form>
  </section>
</template>

<style scoped>
.formulaire {
  max-width: 480px;
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
  align-items: center;
  gap: 1rem;
}

.actions a {
  color: var(--encre-attenuee);
}
</style>
