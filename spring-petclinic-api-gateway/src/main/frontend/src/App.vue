<template>
  <div>
    <NavBar />
    <div class="container-fluid">
      <div class="container xd-container">
        <router-view />
      </div>
    </div>
    <ChatBox />
    <Footer />
  </div>
</template>

<script setup>
import NavBar from './components/NavBar.vue'
import Footer from './components/Footer.vue'
import ChatBox from './components/ChatBox.vue'
</script>

<style>
/* Global styles can be added here if needed */
</style>
