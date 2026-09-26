<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="page">
    <header class="entete">
      <h1>Budget familial</h1>
      <nav class="navigation">
        <RouterLink to="/">Tableau de bord</RouterLink>
        <RouterLink to="/transactions">Transactions</RouterLink>
        <RouterLink to="/comptes">Comptes</RouterLink>
        <RouterLink to="/import">Importer</RouterLink>
      </nav>
    </header>
    <main>
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.entete {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  justify-content: space-between;
  gap: 1rem;
  border-bottom: 1px solid var(--trait);
  padding-bottom: 1rem;
  margin-bottom: 1.5rem;
}

.navigation {
  display: flex;
  gap: 1.25rem;
}

.navigation a {
  color: var(--encre-attenuee);
  text-decoration: none;
  font-weight: 500;
}

.navigation a.router-link-exact-active {
  color: var(--encre);
  border-bottom: 2px solid var(--or);
}
</style>
