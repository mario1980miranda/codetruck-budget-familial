<script setup lang="ts">
import ChampDate from '@/components/ChampDate.vue'
import { TITULAIRES, TYPES_COMPTE, type Compte, type Filtres } from '@/types/budget'
import { libelle } from '@/utils/format'

defineProps<{ comptes: Compte[] }>()
const filtres = defineModel<Filtres>({ required: true })
</script>

<template>
  <div class="filtres">
    <label>
      Début
      <ChampDate v-model="filtres.debut" />
    </label>
    <label>
      Fin
      <ChampDate v-model="filtres.fin" />
    </label>
    <label>
      Titulaire
      <select v-model="filtres.titulaire">
        <option value="">Tous</option>
        <option v-for="titulaire in TITULAIRES" :key="titulaire" :value="titulaire">
          {{ libelle(titulaire) }}
        </option>
      </select>
    </label>
    <label>
      Type de compte
      <select v-model="filtres.typeCompte">
        <option value="">Tous</option>
        <option v-for="type in TYPES_COMPTE" :key="type" :value="type">
          {{ libelle(type) }}
        </option>
      </select>
    </label>
    <label>
      Compte
      <select v-model="filtres.compteId">
        <option value="">Tous</option>
        <option v-for="compte in comptes" :key="compte.id" :value="compte.id">
          {{ compte.nom }}
        </option>
      </select>
    </label>
    <slot />
  </div>
</template>

<style scoped>
.filtres {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem 1rem;
  margin-bottom: 1.5rem;
}
</style>
