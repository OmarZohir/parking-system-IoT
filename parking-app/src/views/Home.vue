<template>
  <div class="max-w-7xl mx-auto pb-12 px-4 sm:px-6 lg:px-8">
    <div class="bg-white shadow overflow-hidden sm:rounded-md">
      <ul class="divide-y divide-gray-200">
        <li v-for="reservation in upcomingReservations" :key="reservation">
          <a href="#" class="block hover:bg-gray-50">
            <div class="flex items-center px-4 py-4 sm:px-6">
              <div class="min-w-0 flex-1 flex items-center">
                <div class="flex-shrink-0">
                  <div class="h-12 w-12 rounded-full flex justify-center items-center bg-blue-700">
                    <calendar-icon class="text-white"></calendar-icon>
                  </div>
                </div>
                <div class="min-w-0 flex-1 px-4 md:grid md:grid-cols-2 md:gap-4">
                  <div>
                    <p class="text-sm font-medium text-indigo-600 truncate">Parking lot {{ reservation }}</p>
                    <p class="mt-2 flex items-center text-sm text-gray-500">
                      <span class="truncate mr-2">Plate</span>
                      <span class="flex-shrink-0 inline-block px-2 py-0.5 text-green-800 text-xs font-medium bg-green-100 rounded-full">2-AGX-09</span>

                    </p>
                  </div>
                  <div class="hidden md:block">
                    <div>
                      <p class="text-sm text-gray-900">
                        Reservation is for
                        <b>January 7, 2020</b>
                      </p>
                      <p class="mt-2 text-xs text-gray-500 leading-5">
                        Made reservation on <strong>2 January</strong>
                      </p>
                    </div>
                  </div>
                </div>
              </div>
              <div>
                <!-- Heroicon name: chevron-right -->
                <svg class="h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                  <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                </svg>
              </div>
            </div>
          </a>
        </li>
      </ul>
    </div>
    <header class="py-10">
      <div class="mx-auto">
        <h1 class="text-3xl font-bold text-blue-700">
          Parking lots
        </h1>
      </div>
    </header>
    <ul class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
      <li class="col-span-1 bg-white rounded-lg shadow divide-y divide-gray-200" v-for="lot in lots" :key="lot">
        <div class="w-full flex items-center justify-between p-6 space-x-6">
          <div class="flex-1 truncate">
            <div class="flex items-center space-x-3">
              <h3 class="text-gray-900 text-sm font-medium truncate">Parking lot A</h3>
              <span class="flex-shrink-0 inline-block px-2 py-0.5 text-green-800 text-xs font-medium bg-green-100 rounded-full">Now available</span>
            </div>
            <p class="mt-1 text-gray-500 text-sm truncate">Hoogstraat 10, Eindhoven</p>
          </div>
          <div class="w-10 h-10 border border-gray-300 rounded-full flex-shrink-0 flex justify-center items-center">
            <location-marker-icon class="text-blue-700"></location-marker-icon>
          </div>
        </div>
        <div>
          <div class="-mt-px flex divide-x divide-gray-200">
            <div class="w-0 flex-1 flex">
              <a href="#" class="relative -mr-px w-0 flex-1 inline-flex items-center justify-center py-4 text-sm text-gray-700 font-medium border border-transparent rounded-bl-lg hover:text-gray-500">
                <eye-icon></eye-icon>
                <span class="ml-3">Show</span>
              </a>
            </div>
            <div class="-ml-px w-0 flex-1 flex">
              <a href="#" class="relative w-0 flex-1 inline-flex items-center justify-center py-4 text-sm text-gray-700 font-medium border border-transparent rounded-br-lg hover:text-gray-500">
                <calendar-icon></calendar-icon>
                <span class="ml-3">Make reservation</span>
              </a>
            </div>
          </div>
        </div>
      </li>
    </ul>
  </div>
</template>

<script>
import { CalendarIcon, LocationMarkerIcon, EyeIcon } from "@vue-hero-icons/outline"
import {mapActions, mapState} from "vuex";
export default {
  name: "Home",
  components: {
    CalendarIcon,
    LocationMarkerIcon,
    EyeIcon
  },
  data() {
    return {
      lots: ["A", "B", "C", "D", "E"],
      upcomingReservations: [0, 1, 2]
    }
  },
  computed: {
    ...mapState({
        loadingLots: state => state.parking.loading.lots
    })
  },
  methods: {
    ...mapActions('parking', [
        '_getParkingLots'
    ]),
    getLots(){
      this._getParkingLots();
    }
  },
  mounted() {
    this.getLots();
  }
}
</script>

<style scoped>

</style>
