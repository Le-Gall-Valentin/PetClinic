<template>
  <nav class="navbar navbar-expand-lg navbar-dark" role="navigation">
    <div class="container-fluid">
      <a class="navbar-brand" href="/"><span></span></a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#main-navbar">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="main-navbar">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <router-link class="nav-link" :class="{ active: $route.name === 'welcome' }" to="/" title="home page">
              <span class="fa fa-home"></span>
              <span>Home</span>
            </router-link>
          </li>

          <li class="nav-item">
            <router-link class="nav-link" :class="{ active: $route.name === 'owners' }" to="/owners" title="find owners">
              <span class="fa fa-search"></span>
              <span>Find owners</span>
            </router-link>
          </li>

          <li class="nav-item">
            <router-link class="nav-link" :class="{ active: $route.name === 'ownerNew' }" to="/owners/new" title="register owner">
              <span class="fa fa-plus"></span>
              <span>Register owner</span>
            </router-link>
          </li>

          <li class="nav-item">
            <router-link class="nav-link" :class="{ active: $route.name === 'vets' }" to="/vets" title="veterinarians">
              <span class="fa fa-th-list"></span>
              <span>Veterinarians</span>
            </router-link>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script setup>
</script>

<style scoped>
</style>
