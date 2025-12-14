<template>
  <div>
    <h2>Owner</h2>
    <form @submit.prevent="submitOwnerForm" style="max-width: 25em;">
      <div class="form-group">
        <label>First name</label>
        <input
          class="form-control"
          v-model="owner.firstName"
          name="firstName"
          required
        />
        <span v-if="errors.firstName" class="help-block">First name is required.</span>
      </div>

      <div class="form-group">
        <label>Last name</label>
        <input
          class="form-control"
          v-model="owner.lastName"
          name="lastName"
          required
        />
        <span v-if="errors.lastName" class="help-block">Last name is required.</span>
      </div>

      <div class="form-group">
        <label>Address</label>
        <input
          class="form-control"
          v-model="owner.address"
          name="address"
          required
        />
        <span v-if="errors.address" class="help-block">Address is required.</span>
      </div>

      <div class="form-group">
        <label>City</label>
        <input
          class="form-control"
          v-model="owner.city"
          name="city"
          required
        />
        <span v-if="errors.city" class="help-block">City is required.</span>
      </div>

      <div class="form-group">
        <label>Telephone</label>
        <input
          class="form-control"
          v-model="owner.telephone"
          pattern="[0-9]{12}"
          placeholder="905554443322"
          name="telephone"
          maxlength="12"
          title="Telephone must be exactly 12 digits (e.g., 905554443322)"
          required
        />
        <span v-if="errors.telephone" class="help-block">Telephone is required.</span>
      </div>

      <div class="form-group">
        <button class="btn btn-primary" type="submit">Submit</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../services/api'

const route = useRoute()
const router = useRouter()

const owner = ref({
  firstName: '',
  lastName: '',
  address: '',
  city: '',
  telephone: ''
})

const errors = ref({})

const ownerId = route.params.ownerId

onMounted(async () => {
  if (ownerId) {
    try {
      const response = await api.getOwner(ownerId)
      owner.value = response.data
    } catch (error) {
      console.error('Failed to load owner:', error)
    }
  }
})

const submitOwnerForm = async () => {
  errors.value = {}

  try {
    if (owner.value.id) {
      await api.updateOwner(owner.value.id, owner.value)
      router.push(`/owners/${ownerId}`)
    } else {
      await api.createOwner(owner.value)
      router.push('/owners')
    }
  } catch (error) {
    console.error('Failed to save owner:', error)
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
