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
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {useRoute} from 'vue-router'
import api from '../services/api'

const route = useRoute()
const vet = ref(null)

onMounted(async () => {
  try {
    const response = await api.getVet(route.params.vetId)
    vet.value = response.data
  } catch (error) {
    console.error('Failed to load vet:', error)
  }
})
</script>

<style scoped>
</style>
