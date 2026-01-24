```vue
<template>
  <div>
    <h2>Owners</h2>

    <!-- Filters -->
    <form class="filters" @submit.prevent>
      <input v-model="filters.firstName" class="form-control" placeholder="First name"/>
      <input v-model="filters.lastName" class="form-control" placeholder="Last name"/>
      <input v-model="filters.city" class="form-control" placeholder="City"/>
      <input v-model="filters.petName" class="form-control" placeholder="Pet name"/>
    </form>

    <table class="table table-striped">
      <thead>
      <tr>
        <th class="sortable" @click="toggleSort('name')">
          Name
          <span v-if="sort.field === 'name'">{{ sortArrow }}</span>
        </th>
        <th class="hidden-sm hidden-xs">Address</th>
        <th class="sortable" @click="toggleSort('city')">
          City
          <span v-if="sort.field === 'city'">{{ sortArrow }}</span>
        </th>
        <th>Telephone</th>
        <th class="hidden-xs">
          Pets
        </th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="owner in owners" :key="owner.id">
        <td>
          <router-link :to="`/owners/${owner.id}`">
            {{ owner.firstName }} {{ owner.lastName }}
          </router-link>
        </td>
        <td class="hidden-sm hidden-xs">{{ owner.address }}</td>
        <td>{{ owner.city }}</td>
        <td>{{ owner.telephone }}</td>
        <td class="hidden-xs">
          {{ owner.pets?.map(p => p.name).join(', ') }}
        </td>
      </tr>
      </tbody>
    </table>

    <!-- Pagination -->
    <div class="pagination-bar">
      <button :disabled="isFirstPage" class="btn btn-secondary" @click="prevPage">
        Précédent
      </button>

      <div class="page-indicator">
        Page {{ page + 1 }} / {{ totalPages }}
      </div>

      <button :disabled="isLastPage" class="btn btn-secondary" @click="nextPage">
        Suivant
      </button>
    </div>
  </div>
</template>

<script setup>
import {computed, onMounted, ref, watch} from 'vue'
import api from '../services/api'

const owners = ref([])

const page = ref(0)
const size = ref(20)
const totalPages = ref(1)

const filters = ref({
  petName: '',
  firstName: '',
  lastName: '',
  city: ''
})

const sort = ref({
  field: 'name',
  direction: 'asc'
})

const isFirstPage = computed(() => page.value <= 0)
const isLastPage = computed(() => page.value >= totalPages.value - 1)

const sortArrow = computed(() => (sort.value.direction === 'asc' ? '▲' : '▼'))

function buildSortParams() {
  switch (sort.value.field) {
    case 'name':
      return [
        `lastName,${sort.value.direction}`,
        `firstName,${sort.value.direction}`,
        'id,asc'
      ]
    case 'city':
      return [`city,${sort.value.direction}`, 'id,asc']
    default:
      return ['id,asc']
  }
}

async function loadOwners() {
  const response = await api.getOwners({
    page: page.value,
    size: size.value,
    ...filters.value,
    sort: buildSortParams()
  })

  owners.value = response.data.content ?? []
  totalPages.value = response.data.totalPages ?? 1
}

function toggleSort(field) {
  if (sort.value.field === field) {
    sort.value.direction = sort.value.direction === 'asc' ? 'desc' : 'asc'
  } else {
    sort.value.field = field
    sort.value.direction = 'asc'
  }
  page.value = 0
  loadOwners()
}

function nextPage() {
  if (!isLastPage.value) {
    page.value++
    loadOwners()
  }
}

function prevPage() {
  if (!isFirstPage.value) {
    page.value--
    loadOwners()
  }
}

watch(filters, () => {
  page.value = 0
  loadOwners()
}, {deep: true})

onMounted(loadOwners)
</script>

<style scoped>
.filters {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(12rem, 1fr));
  gap: 0.75rem;
  margin: 1.5rem 0;
}

.sortable {
  cursor: pointer;
  user-select: none;
}

.pagination-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 1rem;
}

.page-indicator {
  min-width: 10rem;
  text-align: center;
  font-weight: 600;
}
</style>
```
