<template>
  <div v-if="vet">
    <h2>Veterinarian Information</h2>

    <table class="table table-striped">
      <tr>
        <th class="col-sm-3">Name</th>
        <td><b>{{ vet.firstName }} {{ vet.lastName }}</b></td>
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
        <th>Pet Id</th>
        <th></th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="visit in filteredVisits" :key="visit.id">
        <td>{{ formatDate(visit.date) }}</td>
        <td>{{ visit.description }}</td>
        <td>{{ visit.petId }}</td>
        <td>
          <button
              :disabled="!isFuture(visit.date)"
              class="btn btn-danger btn-xs"
              @click="confirmDelete(visit.id)"
          >Supprimer
          </button>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
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
const petIdToType = ref({})

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
  const vresp = await api.getVisitsByVetWithDate(route.params.vetId, params)
  visits.value = vresp.data
  filterByType()
}

const filterByType = () => {
  if (!typeFilter.value) {
    filteredVisits.value = visits.value
  } else {
    filteredVisits.value = visits.value.filter(v => petIdToType.value[v.petId] === typeFilter.value)
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

onMounted(async () => {
  try {
    const response = await api.getVet(route.params.vetId)
    vet.value = response.data
    const vresp = await api.getVisitsByVet(route.params.vetId)
    visits.value = vresp.data
    filteredVisits.value = visits.value
    const ownersResp = await api.getOwners()
    const map = {}
    const typesSet = new Set()
    ownersResp.data.forEach(o => {
      (o.pets || []).forEach(p => {
        map[p.id] = p.type?.name
        if (p.type?.name) typesSet.add(p.type.name)
      })
    })
    petIdToType.value = map
    petTypes.value = Array.from(typesSet)
  } catch (error) {
    console.error('Failed to load vet:', error)
  }
})
</script>

<style scoped>
</style>
