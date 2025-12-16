<template>
  <div v-if="owner">
    <h2>Owner Information</h2>

    <table class="table table-striped">
      <tr>
        <th class="col-sm-3">Name</th>
        <td><b>{{ owner.firstName }} {{ owner.lastName }}</b></td>
      </tr>
      <tr>
        <th>Address</th>
        <td>{{ owner.address }}</td>
      </tr>
      <tr>
        <th>City</th>
        <td>{{ owner.city }}</td>
      </tr>
      <tr>
        <th>Telephone</th>
        <td>{{ owner.telephone }}</td>
      </tr>
      <tr>
        <td>
          <router-link :to="`/owners/${owner.id}/edit`" class="btn btn-primary">Edit Owner</router-link>
        </td>
        <td>
          <router-link :to="`/owners/${owner.id}/pets/new`" class="btn btn-primary">Add New Pet</router-link>
        </td>
      </tr>
    </table>

    <h2>Pets and Visits</h2>

    <table class="table table-striped">
      <tr v-for="pet in owner.pets" :key="pet.id">
        <td valign="top">
          <dl class="dl-horizontal">
            <dt>Name</dt>
            <dd>
              <router-link :to="`/owners/${owner.id}/pets/${pet.id}/edit`">{{ pet.name }}</router-link>
            </dd>
            <dt>Birth Date</dt>
            <dd>{{ formatDate(pet.birthDate) }}</dd>
            <dt>Type</dt>
            <dd>{{ pet.type.name }}</dd>
          </dl>
        </td>
        <td valign="top">
          <table class="table-condensed">
            <thead>
              <tr>
                <th>Visit Date</th>
                <th>Description</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="visit in pet.visits" :key="visit.id">
                <td>{{ formatDate(visit.date) }}</td>
                <td>{{ visit.description }}</td>
              </tr>
              <tr>
                <td>
                  <router-link :to="`/owners/${owner.id}/pets/${pet.id}/edit`">Edit Pet</router-link>
                </td>
                <td>
                  <router-link :to="`/owners/${owner.id}/pets/${pet.id}/visits/new`">Add Visit</router-link>
                </td>
              </tr>
            </tbody>
          </table>
        </td>
      </tr>
    </table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api from '../services/api'

const route = useRoute()
const owner = ref(null)

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const options = { year: 'numeric', month: 'short', day: '2-digit' }
  return date.toLocaleDateString('en-US', options)
}

onMounted(async () => {
  try {
    const response = await api.getOwner(route.params.ownerId)
    owner.value = response.data
  } catch (error) {
    console.error('Failed to load owner:', error)
  }
})
</script>

<style scoped>
</style>
