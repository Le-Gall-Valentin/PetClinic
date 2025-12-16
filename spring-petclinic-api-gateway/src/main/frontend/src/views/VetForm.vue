<template>
  <div>
    <h2>Veterinarian</h2>
    <form style="max-width: 25em;" @submit.prevent="submitVetForm">
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
        <button class="btn btn-primary" type="submit">Submit</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import api from '../services/api'

const route = useRoute()
const router = useRouter()

const vet = ref({
  firstName: '',
  lastName: ''
})

const errors = ref({})

const vetId = route.params.vetId

onMounted(async () => {
  if (vetId) {
    try {
      const response = await api.getVet(vetId)
      vet.value = response.data
    } catch (error) {
      console.error('Failed to load vet:', error)
    }
  }
})

const submitVetForm = async () => {
  errors.value = {}

  try {
    if (vetId) {
      await api.updateVet(vetId, vet.value)
      await router.push(`/vets/${vetId}`)
    } else {
      console.log(vet.value)
      const response = await api.createVet(vet.value)
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
