<template>
  <div>
    <h2>Veterinarians</h2>

    <div class="form-check mb-3">
      <input id="toggleDeletedVets" v-model="viewDeleted" class="form-check-input" type="checkbox"/>
      <label class="form-check-label" for="toggleDeletedVets">
        Afficher les vétérinaires supprimés
      </label>
    </div>
    <table class="table table-striped">
      <thead>
      <tr>
        <th>Name</th>
        <th>Specialties</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="vet in visibleVets" :key="vet.id" :class="{'table-danger': vet.deleted}">
        <td>
          <router-link :to="`/vets/${vet.id}`">
            {{ vet.firstName }} {{ vet.lastName }}
          </router-link>
        </td>
        <td>
            <span v-for="(specialty, index) in vet.specialties" :key="specialty.id">
              {{ specialty.name }}<span v-if="index < vet.specialties.length - 1">, </span>
            </span>
          <span v-if="!vet.specialties || vet.specialties.length === 0">none</span>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import {onMounted, ref, computed} from 'vue'
import api from '../services/api'

const vets = ref([])
const viewDeleted = ref(false)
const visibleVets = computed(() => {
  const items = vets.value || []
  return viewDeleted.value ? items : items.filter(v => !v?.deleted)
})

onMounted(async () => {
  try {
    const response = await api.getVets()
    vets.value = response.data
  } catch (error) {
    console.error('Failed to load vets:', error)
  }
})
</script>

<style scoped>
</style>
