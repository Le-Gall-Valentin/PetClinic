import axios from 'axios'

const api = axios.create({
    baseURL: '/api',
    headers: {
        'Cache-Control': 'no-cache',
        'Content-Type': 'application/json'
    }
})

// Add response interceptor for error handling
api.interceptors.response.use(
    response => response,
    error => {
        console.error('API Error:', error)
        return Promise.reject(error)
    }
)

export default {
    // Owner endpoints
    getOwners() {
        return api.get('/customer/owners')
    },
    getOwner(ownerId) {
        return api.get(`/gateway/owners/${ownerId}`)
    },
    createOwner(owner) {
        return api.post('/customer/owners', owner)
    },
    updateOwner(ownerId, owner) {
        return api.put(`/customer/owners/${ownerId}`, owner)
    },

    // Pet endpoints
    getPetTypes() {
        return api.get('/customer/petTypes')
    },
    createPet(ownerId, pet) {
        return api.post(`/customer/owners/${ownerId}/pets`, pet)
    },
    updatePet(ownerId, petId, pet) {
        return api.put(`/customer/owners/${ownerId}/pets/${petId}`, pet)
    },

    // Visit endpoints
    createVisit(ownerId, petId, visit) {
        return api.post(`/visit/owners/${ownerId}/pets/${petId}/visits`, visit)
    },
    getVisitsByVet(vetId) {
        return api.get(`/visit/vets/${vetId}/visits`)
    },
    getVisitsByVetWithDate(vetId, params) {
        return api.get(`/visit/vets/${vetId}/visits`, { params })
    },
    deleteVisit(visitId) {
        return api.delete(`/visit/visits/${visitId}`)
    },

    // Vet endpoints
    getVets() {
        return api.get('/vet/vets')
    },

    getVet(vetId) {
        return api.get(`/vet/vets/${vetId}`)
    },

    createVet(vet) {
        return api.post('/vet/vets', vet)
    },
    updateVet(vetId, vet) {
        return api.put(`/vet/vets/${vetId}`, vet)
    },
    getSpecialties() {
        return api.get('/vet/specialties')
    },

    // GenAI Chat
    sendChatMessage(message) {
        return api.post('/genai/chatclient', JSON.stringify(message), {
            headers: {'Content-Type': 'application/json'},
            responseType: 'text'
        })
    }
}
