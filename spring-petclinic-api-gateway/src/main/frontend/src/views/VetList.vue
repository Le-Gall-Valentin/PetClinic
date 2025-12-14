<template>
  <div>
    <h2>Veterinarians</h2>

    <table class="table table-striped">
      <thead>
        <tr>
          <th>Name</th>
          <th>Specialties</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="vet in vets" :key="vet.id">
          <td>{{ vet.firstName }} {{ vet.lastName }}</td>
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
import { ref, onMounted } from 'vue'
import api from '../services/api'

const vets = ref([])

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
