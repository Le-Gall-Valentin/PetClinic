<template>
  <div>
    <h2>New Visit</h2>

    <form class="form-horizontal" @submit.prevent="submit">
      <div class="form-group">
        <label class="col-sm-2 control-label">Date</label>
        <div class="col-sm-6">
          <input
            class="form-control"
            v-model="date"
            required
            type="date"
          />
        </div>
      </div>

      <div class="form-group">
        <label class="col-sm-2 control-label">Description</label>
        <div class="col-sm-6">
          <textarea
            class="form-control"
            v-model="description"
            rows="3"
            required
          ></textarea>
        </div>
      </div>

      <div class="form-group">
        <label class="col-sm-2 control-label">Vétérinaire (optionnel)</label>
        <div class="col-sm-6">
          <select class="form-control" v-model="selectedVetId">
            <option :value="null">Non assigné</option>
            <option v-for="vet in activeVets" :key="vet.id" :value="vet.id">
              {{ vet.firstName }} {{ vet.lastName }}
            </option>
          </select>
        </div>
      </div>

      <div class="form-group">
        <div class="col-sm-6 col-sm-offset-2">
          <button class="btn btn-primary" type="submit">Submit</button>
        </div>
      </div>
    </form>

    <h3 v-if="visits.length > 0">Previous Visits</h3>
    <table v-if="visits.length > 0" class="table table-striped">
      <thead>
        <tr>
          <th>Date</th>
          <th>Description</th>
          <th>Vétérinaire</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="visit in visits" :key="visit.id">
          <td>{{ formatDate(visit.date) }}</td>
          <td>{{ visit.description }}</td>
          <td>{{ vetName(visit.vetId) }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../services/api'

const route = useRoute()
const router = useRouter()

const date = ref(new Date().toISOString().split('T')[0])
const description = ref('')
const visits = ref([])
const vets = ref([])
const selectedVetId = ref(null)
const activeVets = computed(() => (vets.value || []).filter(v => !v?.deleted))

const ownerId = route.params.ownerId
const petId = route.params.petId

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const options = { year: 'numeric', month: 'short', day: '2-digit' }
  return date.toLocaleDateString('en-US', options)
}

const vetName = (vetId) => {
  if (!vetId) return 'Non assigné'
  const v = vets.value.find(v => v.id === vetId)
  return v ? `${v.firstName} ${v.lastName}` : 'Non assigné'
}

onMounted(async () => {
  try {
    const response = await api.getOwner(ownerId)
    const pet = response.data.pets?.find(p => p.id === parseInt(petId))
    if (pet && pet.visits) {
      visits.value = pet.visits
    }
    const vetsResp = await api.getVets()
    vets.value = vetsResp.data
  } catch (error) {
    console.error('Failed to load visits:', error)
  }
})

const submit = async () => {
  const data = {
    date: date.value,
    description: description.value
  }
  if (selectedVetId.value) {
    data.vetId = selectedVetId.value
  }

  try {
    await api.createVisit(ownerId, petId, data)
    router.push(`/owners/${ownerId}`)
  } catch (error) {
    console.error('Failed to save visit:', error)
  }
}
</script>

<style scoped>
</style>
