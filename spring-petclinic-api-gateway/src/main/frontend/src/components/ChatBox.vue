<template>
  <div class="chatbox" :class="{ minimized: isMinimized }" id="chatbox">
    <button class="chatbox-header" @click="toggleChatbox">
      Chat with Us!
    </button>
    <div class="chatbox-content" :style="{ height: isMinimized ? '40px' : '400px' }" id="chatbox-content">
      <div class="chatbox-messages" id="chatbox-messages" ref="messagesContainer">
        <div v-for="(msg, index) in messages" :key="index" class="chat-bubble" :class="msg.type" v-html="msg.html"></div>
      </div>
      <div class="chatbox-footer">
        <input
          type="text"
          v-model="inputMessage"
          @keydown.enter="sendMessage"
          placeholder="Type a message..."
          id="chatbox-input"
        />
        <button @click="sendMessage">Send</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { marked } from 'marked'
import api from '../services/api'

const isMinimized = ref(false)
const inputMessage = ref('')
const messages = ref([])
const messagesContainer = ref(null)

const toggleChatbox = () => {
  isMinimized.value = !isMinimized.value
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const appendMessage = (message, type) => {
  const htmlContent = marked.parse(message)
  messages.value.push({ html: htmlContent, type })
  scrollToBottom()
  saveChatMessages()
}

const sendMessage = async () => {
  const query = inputMessage.value.trim()
  if (!query) return

  inputMessage.value = ''
  appendMessage(query, 'user')

  try {
    const response = await api.sendChatMessage(query)
    appendMessage(response.data, 'bot')
  } catch (error) {
    console.error('Error:', error)
    appendMessage('Chat is currently unavailable', 'bot')
  }
}

const saveChatMessages = () => {
  localStorage.setItem('chatMessages', JSON.stringify(messages.value))
}

const loadChatMessages = () => {
  const saved = localStorage.getItem('chatMessages')
  if (saved) {
    try {
      messages.value = JSON.parse(saved)
      scrollToBottom()
    } catch (e) {
      console.error('Failed to load chat messages:', e)
    }
  }
}

onMounted(() => {
  loadChatMessages()
})

onBeforeUnmount(() => {
  saveChatMessages()
})
</script>

<style scoped>
/* ChatBox styles will inherit from existing petclinic.css */
</style>
