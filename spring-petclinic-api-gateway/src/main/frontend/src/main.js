import { createApp } from 'vue'
import { createRouter, createWebHashHistory } from 'vue-router'
import App from './App.vue'

// Import CSS
import 'bootstrap/dist/css/bootstrap.min.css'
import '@fortawesome/fontawesome-free/css/all.min.css'

// Import components
import Welcome from './views/Welcome.vue'
import OwnersList from './views/OwnersList.vue'
import OwnerDetails from './views/OwnerDetails.vue'
import OwnerForm from './views/OwnerForm.vue'
import PetForm from './views/PetForm.vue'
import Visits from './views/Visits.vue'
import VetList from './views/VetList.vue'

// Configure routes
const routes = [
  { path: '/', redirect: '/welcome' },
  { path: '/welcome', name: 'welcome', component: Welcome },
  { path: '/owners', name: 'owners', component: OwnersList },
  { path: '/owners/new', name: 'ownerNew', component: OwnerForm },
  { path: '/owners/:ownerId', name: 'ownerDetails', component: OwnerDetails },
  { path: '/owners/:ownerId/edit', name: 'ownerEdit', component: OwnerForm },
  { path: '/owners/:ownerId/pets/new', name: 'petNew', component: PetForm },
  { path: '/owners/:ownerId/pets/:petId/edit', name: 'petEdit', component: PetForm },
  { path: '/owners/:ownerId/pets/:petId/visits/new', name: 'visitNew', component: Visits },
  { path: '/vets', name: 'vets', component: VetList }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

const app = createApp(App)
app.use(router)
app.mount('#app')
