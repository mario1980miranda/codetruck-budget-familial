<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { creerCategorie, listerCategories, modifierCategorie, supprimerCategorie } from '@/api/categories'
import type { Categorie } from '@/types/budget'
import { libelle } from '@/utils/format'

const categories = ref<Categorie[]>([])
const erreurChargement = ref('')

const nom = ref('')
const description = ref('')
const enCours = ref(false)
const erreurFormulaire = ref('')
const erreurListe = ref('')

const idEnEdition = ref<string | null>(null)
const descriptionEditee = ref('')
const enregistrementEdition = ref(false)

// Interroge /categories ; en cas d'échec, vide la liste et affiche le message.
async function charger() {
  erreurChargement.value = ''
  try {
    categories.value = await listerCategories()
  } catch (e) {
    categories.value = []
    erreurChargement.value = (e as Error).message
  }
}

// Met le nom en majuscules sans accent ni espace (format exigé par le backend)
// au fil de la saisie : « Télécom mobile » devient « TELECOM_MOBILE ».
function normaliserNom(evenement: Event) {
  nom.value = (evenement.target as HTMLInputElement).value
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toUpperCase()
    .replace(/[^A-Z]+/g, '_')
}

// Crée la catégorie puis vide le formulaire et recharge la liste ; en cas
// d'échec (ex. nom déjà pris), garde la saisie et affiche l'erreur.
async function enregistrer() {
  enCours.value = true
  erreurFormulaire.value = ''
  try {
    await creerCategorie({ nom: nom.value, description: description.value.trim() })
    nom.value = ''
    description.value = ''
    await charger()
  } catch (e) {
    erreurFormulaire.value = (e as Error).message
  } finally {
    enCours.value = false
  }
}

// Demande confirmation, supprime la catégorie puis recharge la liste ; le
// backend refuse AUTRE et toute catégorie déjà utilisée par des transactions.
async function supprimer(categorie: Categorie) {
  if (!window.confirm(`Supprimer la catégorie « ${libelle(categorie.nom)} » ?`)) {
    return
  }
  erreurListe.value = ''
  try {
    await supprimerCategorie(categorie.id)
    await charger()
  } catch (e) {
    erreurListe.value = (e as Error).message
  }
}

// Ouvre l'édition d'une seule ligne à la fois, pré-remplie avec la
// description actuelle.
function commencerEdition(categorie: Categorie) {
  erreurListe.value = ''
  idEnEdition.value = categorie.id
  descriptionEditee.value = categorie.description
}

function annulerEdition() {
  idEnEdition.value = null
}

// Envoie la nouvelle description (le nom reste le même) puis ferme l'édition
// et recharge la liste ; en cas d'échec, garde l'édition ouverte et affiche l'erreur.
async function enregistrerDescription(categorie: Categorie) {
  enregistrementEdition.value = true
  erreurListe.value = ''
  try {
    await modifierCategorie(categorie.id, { nom: categorie.nom, description: descriptionEditee.value.trim() })
    idEnEdition.value = null
    await charger()
  } catch (e) {
    erreurListe.value = (e as Error).message
  } finally {
    enregistrementEdition.value = false
  }
}

onMounted(charger)
</script>

<template>
  <section class="mise-en-page">
    <div>
      <p v-if="erreurChargement" class="message-erreur">{{ erreurChargement }}</p>
      <template v-else>
        <p v-if="erreurListe" class="message-erreur">{{ erreurListe }}</p>
        <div class="tableau-defilant">
          <table>
            <thead>
              <tr>
                <th>Nom</th>
                <th>Description (consigne pour l'IA)</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="categorie in categories" :key="categorie.id">
                <td>{{ libelle(categorie.nom) }}</td>
                <td v-if="idEnEdition === categorie.id">
                  <textarea
                    v-model="descriptionEditee"
                    maxlength="300"
                    rows="3"
                    aria-label="Description"
                    class="edition-description"
                  ></textarea>
                </td>
                <td v-else>{{ categorie.description }}</td>
                <td class="liens">
                  <template v-if="idEnEdition === categorie.id">
                    <button
                      type="button"
                      class="lien"
                      :disabled="enregistrementEdition || descriptionEditee.trim() === ''"
                      @click="enregistrerDescription(categorie)"
                    >
                      {{ enregistrementEdition ? 'Enregistrement…' : 'Enregistrer' }}
                    </button>
                    <button type="button" class="lien lien-attenue" @click="annulerEdition">Annuler</button>
                  </template>
                  <template v-else>
                    <button type="button" class="lien" @click="commencerEdition(categorie)">Modifier</button>
                    <button v-if="categorie.nom !== 'AUTRE'" type="button" class="lien" @click="supprimer(categorie)">
                      Supprimer
                    </button>
                  </template>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </template>
    </div>

    <div class="carte formulaire">
      <h2>Nouvelle catégorie</h2>
      <form @submit.prevent="enregistrer">
        <label>
          Nom
          <input
            :value="nom"
            type="text"
            maxlength="20"
            required
            placeholder="ex. TELECOM"
            @input="normaliserNom"
          />
        </label>
        <label>
          Description
          <textarea
            v-model="description"
            maxlength="300"
            rows="4"
            required
            placeholder="ex. Téléphonie mobile et internet : Fizz, Vidéotron, Bell"
          ></textarea>
        </label>
        <p class="message-info">
          La description est envoyée à l'IA à chaque import : nomme des commerçants typiques pour qu'elle reconnaisse
          la catégorie. Les relevés déjà importés ne sont pas recatégorisés.
        </p>
        <p v-if="erreurFormulaire" class="message-erreur">{{ erreurFormulaire }}</p>
        <div class="actions">
          <button type="submit" class="bouton" :disabled="enCours || nom === '' || description.trim() === ''">
            {{ enCours ? 'Enregistrement…' : 'Créer la catégorie' }}
          </button>
        </div>
      </form>
    </div>
  </section>
</template>

<style scoped>
.mise-en-page {
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(0, 1fr);
  gap: 1.5rem;
  align-items: start;
}

@media (max-width: 800px) {
  .mise-en-page {
    grid-template-columns: minmax(0, 1fr);
  }
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

textarea {
  resize: vertical;
}

.actions {
  display: flex;
  justify-content: flex-end;
}

.liens {
  display: flex;
  gap: 1rem;
  white-space: nowrap;
}

.lien {
  background: none;
  border: none;
  padding: 0;
  color: var(--or);
  cursor: pointer;
  font: inherit;
}

.lien:disabled {
  opacity: 0.5;
  cursor: default;
}

.lien-attenue {
  color: var(--encre-attenuee);
}

.edition-description {
  width: 100%;
  min-width: 240px;
}
</style>
