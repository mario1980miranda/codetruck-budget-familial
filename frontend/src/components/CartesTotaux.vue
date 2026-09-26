<script setup lang="ts">
import { computed } from 'vue'
import type { Agregation } from '@/types/budget'
import { formaterMontant } from '@/utils/format'

const props = defineProps<{ agregation: Agregation }>()

const solde = computed(
  () => props.agregation.totalRevenus - props.agregation.totalDepenses - props.agregation.totalEpargne,
)
</script>

<template>
  <div class="cartes">
    <div class="carte">
      <p class="etiquette">Revenus</p>
      <p class="chiffre valeur positif">{{ formaterMontant(agregation.totalRevenus) }}</p>
    </div>
    <div class="carte">
      <p class="etiquette">Dépenses</p>
      <p class="chiffre valeur negatif">{{ formaterMontant(agregation.totalDepenses) }}</p>
    </div>
    <div class="carte">
      <p class="etiquette">Épargne</p>
      <p class="chiffre valeur or">{{ formaterMontant(agregation.totalEpargne) }}</p>
    </div>
    <div class="carte">
      <p class="etiquette">Solde</p>
      <p class="chiffre valeur" :class="solde < 0 ? 'negatif' : 'positif'">{{ formaterMontant(solde) }}</p>
    </div>
  </div>
</template>

<style scoped>
.cartes {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.etiquette {
  font-size: 12px;
  color: var(--encre-attenuee);
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.valeur {
  font-size: 22px;
  font-weight: 600;
  margin: 4px 0 0;
}

.positif {
  color: var(--positif);
}

.negatif {
  color: var(--negatif);
}

.or {
  color: var(--or);
}
</style>
