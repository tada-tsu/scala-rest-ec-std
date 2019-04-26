<template>
  <div class="home">
    <search mode="index"></search>
    <products mode="index"></products>
  </div>
</template>

<script>
  import HelloWorld from '../components/HelloWorld'
  import Search from '../components/Search'
  import Products from '../components/Products'

  export default {
    components: {
      HelloWorld,
      Search,
      Products,
    }
  }
</script>
