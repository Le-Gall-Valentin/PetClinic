<template>
  <div>
    <h2>Owners</h2>

    <form @submit.prevent style="max-width: 20em; margin-top: 2em;">
      <div class="form-group">
        <input
          type="text"
          class="form-control"
          placeholder="Search Filter"
          v-model="query"
        />
      </div>
    </form>

    <table class="table table-striped">
      <thead>
        <tr>
          <th>Name</th>
          <th class="hidden-sm hidden-xs">Address</th>
          <th>City</th>
          <th>Telephone</th>
          <th class="hidden-xs">Pets</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="owner in filteredOwners" :key="owner.id">
          <td>
            <router-link :to="`/owners/${owner.id}`">
              {{ owner.firstName }} {{ owner.lastName }}
            </router-link>
          </td>
          <td class="hidden-sm hidden-xs">{{ owner.address }}</td>
          <td>{{ owner.city }}</td>
          <td>{{ owner.telephone }}</td>
          <td class="hidden-xs">
            <span v-for="pet in owner.pets" :key="pet.id">{{ pet.name }} </span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../services/api'

const owners = ref([])
const query = ref('')

const filteredOwners = computed(() => {
  if (!query.value) return owners.value

  const searchTerm = query.value.toLowerCase()
  return owners.value.filter(owner => {
    return (
      owner.firstName?.toLowerCase().includes(searchTerm) ||
      owner.lastName?.toLowerCase().includes(searchTerm) ||
      owner.address?.toLowerCase().includes(searchTerm) ||
      owner.city?.toLowerCase().includes(searchTerm) ||
      owner.telephone?.includes(searchTerm) ||
      owner.pets?.some(pet => pet.name?.toLowerCase().includes(searchTerm))
    )
  })
})

onMounted(async () => {
  try {
    const response = await api.getOwners()
    owners.value = response.data
  } catch (error) {
    console.error('Failed to load owners:', error)
  }
})
</script>

<style scoped>
</style>
