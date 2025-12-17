<template>
  <nav class="navbar navbar-expand-lg navbar-dark" role="navigation">
    <div class="container-fluid">
      <a class="navbar-brand" href="/"><span></span></a>
      <button class="navbar-toggler" data-bs-target="#main-navbar" data-bs-toggle="collapse" type="button">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div id="main-navbar" class="collapse navbar-collapse">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <router-link :class="{ active: $route.name === 'welcome' }" class="nav-link" title="home page" to="/">
              <span class="fa fa-home"></span>
              <span>Home</span>
            </router-link>
          </li>

          <li class="nav-item">
            <router-link :class="{ active: $route.name === 'owners' }" class="nav-link" title="find owners"
                         to="/owners">
              <span class="fa fa-search"></span>
              <span>Find owners</span>
            </router-link>
          </li>

          <li class="nav-item">
            <router-link :class="{ active: $route.name === 'ownerNew' }" class="nav-link" title="register owner"
                         to="/owners/new">
              <span class="fa fa-plus"></span>
              <span>Register owner</span>
            </router-link>
          </li>

          <li class="nav-item">
            <router-link :class="{ active: $route.name === 'vets' }" class="nav-link" title="veterinarians" to="/vets">
              <span class="fa fa-th-list"></span>
              <span>Veterinarians</span>
            </router-link>
          </li>

          <li class="nav-item">
            <router-link :class="{ active: $route.name === 'vetNew' }" class="nav-link" title="register veterinarian"
                         to="/vets/new">
              <span class="fa fa-plus"></span>
              <span>Register veterinarian</span>
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
