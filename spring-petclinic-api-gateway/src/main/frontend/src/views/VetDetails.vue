<template>
  <div v-if="vet">
    <h2>Veterinarian Information</h2>

    <table class="table table-striped">
      <tr>
        <th class="col-sm-3">Name</th>
        <td><b>{{ vet.firstName }} {{ vet.lastName }}</b></td>
      </tr>
      <tr>
        <th class="col-sm-3">Status</th>
        <td><b :class="{'text-danger': isDeleted, 'text-success': !isDeleted}">{{ isDeleted ? 'Supprimé' : 'Actif' }}</b></td>
      </tr>
      <tr>
        <th class="col-sm-3">Specialties</th>
        <td><b v-for="(specialty, index) in vet.specialties" :key="specialty.id">
          {{ specialty.name }}<b v-if="index < vet.specialties.length - 1">, </b>
        </b></td>
      </tr>
      <tr>
        <td>
          <router-link :to="`/vets/${vet.id}/edit`" class="btn btn-primary">Edit Veterinarian</router-link>
        </td>
        <td>
          <button
              :disabled="hasFutureVisits() || isDeleted"
              class="btn btn-danger"
              @click="deleteVet(vet.id)"
          >Supprimer le vétérinaire</button>
        </td>
      </tr>
    </table>

    <h2>Visits</h2>
    <div class="filters form-horizontal">
      <div class="form-group">
        <label class="col-sm-2 control-label">From</label>
        <div class="col-sm-3">
          <input v-model="from" class="form-control" type="date"/>
        </div>
        <label class="col-sm-2 control-label">To</label>
        <div class="col-sm-3">
          <input v-model="to" class="form-control" type="date"/>
        </div>
      </div>
      <div class="form-group">
        <label class="col-sm-2 control-label">Type</label>
        <div class="col-sm-6">
          <select v-model="typeFilter" class="form-control">
            <option value="">Tous</option>
            <option v-for="t in petTypes" :key="t" :value="t">{{ t }}</option>
          </select>
        </div>
        <div class="col-sm-2">
          <button class="btn btn-primary" @click="applyFilters">Filtrer</button>
        </div>
      </div>
    </div>
    <table v-if="visits.length > 0" class="table table-striped">
      <thead>
      <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Animal</th>
        <th></th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="vw in filteredVisits" :key="vw.visit.id">
        <td>{{ formatDate(vw.visit.date) }}</td>
        <td>{{ vw.visit.description }}</td>
        <td>{{ vw.pet?.name }} ({{ vw.pet?.type?.name }})</td>
        <td>
          <button
              :disabled="!isFuture(vw.visit.date)"
              class="btn btn-danger btn-xs"
              @click="confirmDelete(vw.visit.id)"
          >Supprimer
          </button>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import {onMounted, ref, computed} from 'vue'
import {useRoute} from 'vue-router'
import api from '../services/api'

const route = useRoute()
const vet = ref(null)
const visits = ref([])
const filteredVisits = ref([])
const from = ref('')
const to = ref('')
const typeFilter = ref('')
const petTypes = ref([])
const isDeleted = computed(() => !!vet.value?.deleted)
const hasFutureVisits = () => {
  if (!visits.value) return false
  const now = new Date()
  return visits.value.some(v => {
    const d = new Date(v.date)
    return d.getTime() > now.getTime()
  })
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const options = {year: 'numeric', month: 'short', day: '2-digit'}
  return date.toLocaleDateString('en-US', options)
}

const isFuture = (dateString) => {
  if (!dateString) return false
  const d = new Date(dateString)
  const now = new Date()
  return d.getTime() > now.getTime()
}

const applyFilters = async () => {
  const params = {}
  if (from.value) params.from = from.value
  if (to.value) params.to = to.value
  const page = await api.getVetPage(route.params.vetId, params)
  vet.value = page.data.vet
  visits.value = page.data.visits || []
  filterByType()
}

const filterByType = () => {
  if (!typeFilter.value) {
    filteredVisits.value = visits.value
  } else {
    filteredVisits.value = visits.value.filter(vw => vw.pet?.type?.name === typeFilter.value)
  }
}

const confirmDelete = async (visitId) => {
  if (window.confirm('Confirmer la suppression de cette visite ?')) {
    try {
      await api.deleteVisit(visitId)
      await applyFilters()
    } catch (e) {
      console.error('Delete failed', e)
    }
  }
}

const deleteVet = async (vetId) => {
  if (window.confirm('Confirmer la suppression de ce vétérinaire ?')) {
    try {
      await api.deleteVet(vetId)
      await applyFilters()
    } catch (e) {
      console.error('Delete vet failed', e)
    }
  }
}

onMounted(async () => {
  try {
    const page = await api.getVetPage(route.params.vetId)
    vet.value = page.data.vet
    visits.value = page.data.visits || []
    filteredVisits.value = visits.value
    const typesSet = new Set()
    visits.value.forEach(vw => {
      const t = vw.pet?.type?.name
      if (t) typesSet.add(t)
    })
    petTypes.value = Array.from(typesSet)
  } catch (error) {
    console.error('Failed to load vet:', error)
  }
})
</script>

<style scoped>
</style>
