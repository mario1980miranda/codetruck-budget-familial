<script setup lang="ts">
import { ref, watch } from 'vue'
import { estDateIso } from '@/utils/format'

// Champ texte au format AAAA-MM-JJ, indépendant de la langue du navigateur
// (contrairement à <input type="date">). Le modèle n'est mis à jour qu'avec
// une date complète et valide, pour ne jamais envoyer de date erronée à l'API.
const date = defineModel<string>({ required: true })
const texte = ref(date.value)

watch(date, (nouvelleDate) => {
  texte.value = nouvelleDate
})

// Garde la saisie telle quelle, et ne la propage au modèle que si elle est valide.
function saisir(evenement: Event) {
  texte.value = (evenement.target as HTMLInputElement).value
  if (estDateIso(texte.value)) {
    date.value = texte.value
  }
}
</script>

<template>
  <input
    :value="texte"
    :class="{ invalide: !estDateIso(texte) }"
    type="text"
    inputmode="numeric"
    placeholder="AAAA-MM-JJ"
    maxlength="10"
    class="chiffre"
    @input="saisir"
  />
</template>

<style scoped>
input {
  width: 9.5rem;
}

.invalide {
  border-color: var(--negatif);
  color: var(--negatif);
}
</style>
