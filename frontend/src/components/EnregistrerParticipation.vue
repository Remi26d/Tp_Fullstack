<template>
  <div class="card">
    <h2>Enregistrer une participation</h2>

    <form class="participation-form" @submit.prevent="enregistrerParticipation">
      <!-- Personne -->
      <label for="personne" class="label">Personne</label>
      <select
        id="personne"
        v-model="selectedPersonne"
        required
        class="select"
      >
        <option value="" disabled>-- Choisir une personne --</option>
        <option
          v-for="personne in personnes"
          :key="personne.matricule"
          :value="personne.matricule"
        >
          {{ personne.nom }} {{ personne.prenom }}
        </option>
      </select>

      <!-- Projet -->
      <label for="projet" class="label">Projet</label>
      <select
        id="projet"
        v-model="selectedProjet"
        required
        class="select"
      >
        <option value="" disabled>-- Choisir un projet --</option>
        <option
          v-for="projet in projets"
          :key="projet.id"
          :value="projet.id"
        >
          {{ projet.nom }}
        </option>
      </select>

      <!-- Rôle -->
      <label for="role" class="label">Rôle</label>
      <input
        id="role"
        type="text"
        v-model="role"
        placeholder="Ex: développeur"
        required
        class="input"
      />

      <!-- Pourcentage (Slider) -->
      <label for="pourcentage" class="label">Pourcentage</label>
      <div class="slider-container">
        <input
          id="pourcentage"
          type="range"
          v-model.number="pourcentage"
          step="0.1"
          min="0"
          max="1"
          class="slider"
        />
        <span class="slider-value">{{ (pourcentage * 100).toFixed(0) }}%</span>
      </div>

      <button type="submit" class="btn-submit">Enregistrer</button>
    </form>

    <!-- Messages de feedback -->
    <div v-if="errorMsg" class="error">{{ errorMsg }}</div>
    <div v-if="successMsg" class="success">{{ successMsg }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

// Données réactives
const personnes = ref([])
const projets = ref([])
const selectedPersonne = ref('')
const selectedProjet = ref('')
const role = ref('')
const pourcentage = ref(0.1) // Valeur par défaut : 0.1 = 10%
const errorMsg = ref('')
const successMsg = ref('')

// Charger la liste des personnes
const chargerPersonnes = async () => {
  try {
    const resp = await axios.get('/api/personnes')
    personnes.value = resp.data._embedded
      ? resp.data._embedded.personnes
      : resp.data
  } catch (err) {
    errorMsg.value = 'Erreur chargement personnes : ' + err
  }
}

// Charger la liste des projets
const chargerProjets = async () => {
  try {
    const resp = await axios.get('/api/projets')
    projets.value = resp.data._embedded
      ? resp.data._embedded.projets
      : resp.data
  } catch (err) {
    errorMsg.value = 'Erreur chargement projets : ' + err
  }
}

// Enregistrer la participation
const enregistrerParticipation = async () => {
  const payload = {
    matricule: selectedPersonne.value,
    codeProjet: selectedProjet.value,
    role: role.value,
    pourcentage: pourcentage.value,
  }
  try {
    await axios.post('/api/gestion/participation', payload)
    successMsg.value = 'Participation enregistrée avec succès.'
    errorMsg.value = ''
    // Réinitialisation
    selectedPersonne.value = ''
    selectedProjet.value = ''
    role.value = ''
    pourcentage.value = 0.1
  } catch (err) {
    successMsg.value = ''
    if (err.response && err.response.data && err.response.data.message) {
      errorMsg.value = err.response.data.message
    } else {
      errorMsg.value = 'Erreur lors de l’enregistrement.'
    }
  }
}

onMounted(() => {
  chargerPersonnes()
  chargerProjets()
})
</script>

<style scoped>
/* --- Mise en forme générale --- */
.card {
  max-width: 420px;
  margin: 2rem auto;
  padding: 2rem;
  background-color: #fff4e5;
  border-radius: 14px;
  box-shadow: 0 5px 15px rgba(230, 81, 0, 0.3);
}

h2 {
  margin-bottom: 1.25rem;
  text-align: center;
  font-size: 1.3rem;
  color: #b53d00;
}

/* --- Formulaire --- */
.participation-form {
  display: flex;
  flex-direction: column;
  gap: 1.1rem;
}

.label {
  font-weight: 600;
  margin-bottom: 0.3rem;
  color: #8a2c00;
}

.select, .input {
  padding: 0.55rem;
  border: 1px solid #ff5722;
  border-radius: 8px;
  font-size: 0.92rem;
  background: #fff;
}

/* Slider */
.slider-container {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.slider {
  flex: 1;
  -webkit-appearance: none;
  height: 5px;
  background: #ffab91;
  border-radius: 3px;
  outline: none;
  cursor: pointer;
}
.slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #e65100;
  cursor: pointer;
  border: 2px solid #fff;
  box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
}
.slider::-moz-range-thumb {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #e65100;
  cursor: pointer;
  border: 2px solid #fff;
  box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
}

/* Valeur du slider */
.slider-value {
  width: 42px;
  text-align: center;
  font-weight: 500;
}

/* --- Bouton --- */
.btn-submit {
  padding: 0.65rem 1.1rem;
  background-color: #ff5722;
  color: #fff;
  font-weight: 600;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.15s ease-in-out, background-color 0.2s ease-in-out;
  font-size: 0.92rem;
}
.btn-submit:hover {
  background-color: #e64a19;
  transform: scale(1.03);
}

/* --- Feedback --- */
.error {
  margin-top: 1rem;
  color: #d32f2f;
}
.success {
  margin-top: 1rem;
  color: #388e3c;
}
</style>
