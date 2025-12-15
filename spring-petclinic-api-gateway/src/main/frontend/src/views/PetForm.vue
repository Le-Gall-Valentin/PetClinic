<template>
  <div>
    <h2>Pet</h2>

    <form class="form-horizontal" @submit.prevent="submit">
      <div class="form-group">
        <label class="col-sm-2 control-label">Owner</label>
        <div class="col-sm-6">
          <p class="form-control-static">{{ pet.owner }}</p>
        </div>
      </div>

      <div class="form-group">
        <label class="col-sm-2 control-label">Name</label>
        <div class="col-sm-6">
          <input
            class="form-control col-sm-4"
            v-model="pet.name"
            name="name"
            required
            type="text"
          />
          <span v-if="errors.name" class="help-inline">Name is required.</span>
        </div>
      </div>

      <div class="form-group">
        <label class="col-sm-2 control-label">Birth date</label>
        <div class="col-sm-6">
          <input
            class="form-control"
            v-model="pet.birthDate"
            required
            type="date"
          />
          <span v-if="errors.birthDate" class="help-inline">Birth date is required.</span>
        </div>
      </div>

      <div class="form-group">
        <label class="col-sm-2 control-label">Type</label>
        <div class="col-sm-6">
          <select class="form-control" v-model="petTypeId">
            <option v-for="type in types" :key="type.id" :value="type.id">
              {{ type.name }}
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../services/api'

const route = useRoute()
const router = useRouter()

const pet = ref({
  name: '',
  birthDate: '',
  owner: '',
  id: 0
})

const types = ref([])
const petTypeId = ref(null)
const errors = ref({})

const ownerId = route.params.ownerId
const petId = route.params.petId

onMounted(async () => {
  try {
    const typesResponse = await api.getPetTypes()
    types.value = typesResponse.data

    if (!petId && types.value.length && petTypeId.value == null) {
      petTypeId.value = types.value[0].id
    }

    if (petId) {
      // Edit existing pet
      const petResponse = await api.getOwner(ownerId)
      const existingPet = petResponse.data.pets?.find(p => p.id === parseInt(petId))
      if (existingPet) {
        pet.value = {
          ...existingPet,
          birthDate: existingPet.birthDate ? existingPet.birthDate.split('T')[0] : '',
          owner: `${petResponse.data.firstName} ${petResponse.data.lastName}`
        }
                petTypeId.value = existingPet.type?.id ??
          (types.value.find(t => t.name === existingPet.type?.name)?.id ?? null)
      }
    } else {
      // New pet
      const ownerResponse = await api.getOwner(ownerId)
      pet.value.owner = `${ownerResponse.data.firstName} ${ownerResponse.data.lastName}`
    }
  } catch (error) {
    console.error('Failed to load pet data:', error)
  }
})

const submit = async () => {
  errors.value = {}

  const data = {
    id: pet.value.id || 0,
    name: pet.value.name,
    birthDate: pet.value.birthDate,
    pets: [],
    typeId: petTypeId.value == null ? null : Number(petTypeId.value)
  }

  try {
    if (pet.value.id) {
      await api.updatePet(ownerId, pet.value.id, data)
    } else {
      await api.createPet(ownerId, data)
    }
    await router.push(`/owners/${ownerId}`)
  } catch (error) {
    console.error('Failed to save pet:', error)
  }
}
</script>

<style scoped>
.help-inline {
  color: #dc3545;
  font-size: 0.875em;
  margin-top: 0.25rem;
}

.form-control-static {
  padding-top: 7px;
  padding-bottom: 7px;
  margin-bottom: 0;
}
</style>
