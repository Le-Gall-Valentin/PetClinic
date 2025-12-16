<template>
  <div>
    <h2>Veterinarian</h2>
    <form style="max-width: 32em;" @submit.prevent="submitVetForm">
      <div class="form-group">
        <label>First name</label>
        <input
            v-model="vet.firstName"
            class="form-control"
            name="firstName"
            required
        />
        <span v-if="errors.firstName" class="help-block">First name is required.</span>
      </div>

      <div class="form-group">
        <label>Last name</label>
        <input
            v-model="vet.lastName"
            class="form-control"
            name="lastName"
            required
        />
        <span v-if="errors.lastName" class="help-block">Last name is required.</span>
      </div>

      <div class="form-group">
        <label>Specialties</label>
        <div class="d-flex flex-wrap gap-2 mb-2">
          <span
            v-if="vet.specialties.length === 0"
            class="text-muted"
          >No specialties selected.</span>
          <div
            v-for="sp in vet.specialties"
            :key="sp.id"
            class="d-inline-flex align-items-center gap-2"
          >
            <span class="badge rounded-pill text-bg-secondary">{{ sp.name }}</span>
            <button
              type="button"
              class="btn btn-sm btn-outline-danger"
              @click="removeSpecialty(sp.id)"
              aria-label="Remove"
            >
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>
        <div class="input-group">
          <select
            class="form-select"
            v-model.number="selectedSpecialtyId"
          >
            <option :value="null" disabled>Select a specialty</option>
            <option
              v-for="sp in availableSpecialties"
              :key="sp.id"
              :value="sp.id"
            >
              {{ sp.name }}
            </option>
          </select>
          <button
            class="btn btn-outline-primary"
            type="button"
            :disabled="!selectedSpecialtyId"
            @click="addSelectedSpecialty"
          >
            Add
          </button>
        </div>
        <small class="text-muted">Duplicates are prevented; you can remove any selected specialty.</small>
      </div>

      <div class="form-group">
        <button class="btn btn-primary" type="submit">Submit</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import {computed, onMounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import api from '../services/api'

const route = useRoute()
const router = useRouter()

const vet = ref({
  firstName: '',
  lastName: '',
  specialties: []
})

const errors = ref({})

const vetId = route.params.vetId
const allSpecialties = ref([])
const selectedSpecialtyId = ref(null)

const availableSpecialties = computed(() => {
  const chosenIds = new Set(vet.value.specialties.map(s => s.id))
  return allSpecialties.value.filter(s => !chosenIds.has(s.id))
})

onMounted(async () => {
  if (vetId) {
    try {
      const response = await api.getVet(vetId)
      vet.value = response.data
    } catch (error) {
      console.error('Failed to load vet:', error)
    }
  }
  try {
    const spResponse = await api.getSpecialties()
    allSpecialties.value = spResponse.data
  } catch (error) {
    console.error('Failed to load specialties:', error)
  }
})

const addSelectedSpecialty = () => {
  if (!selectedSpecialtyId.value) return
  const exists = vet.value.specialties.some(s => s.id === selectedSpecialtyId.value)
  if (exists) {
    selectedSpecialtyId.value = null
    return
  }
  const sp = allSpecialties.value.find(s => s.id === selectedSpecialtyId.value)
  if (sp) {
    vet.value.specialties = [...vet.value.specialties, sp]
  }
  selectedSpecialtyId.value = null
}

const removeSpecialty = (id) => {
  vet.value.specialties = vet.value.specialties.filter(s => s.id !== id)
}

const submitVetForm = async () => {
  errors.value = {}

  try {
    const payload = {
      firstName: vet.value.firstName,
      lastName: vet.value.lastName,
      specialties: vet.value.specialties.map(s => s.id)
    }
    if (vetId) {
      await api.updateVet(vetId, payload)
      await router.push(`/vets/${vetId}`)
    } else {
      const response = await api.createVet(payload)
      await router.push(`/vets/${response.data.id}`)
    }
  } catch (error) {
    console.error('Failed to save vet:', error)
  }
}
</script>

<style scoped>
.help-block {
  color: #dc3545;
  font-size: 0.875em;
  margin-top: 0.25rem;
}
</style>
