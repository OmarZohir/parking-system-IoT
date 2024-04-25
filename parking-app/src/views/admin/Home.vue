<template>
  <div class="max-w-7xl mx-auto pb-12 px-4 sm:px-6 lg:px-8">
    <div class="bg-white shadow overflow-hidden sm:rounded-md">
      <ul class="divide-y divide-gray-200">
        <li v-for="reservation in reservations" :key="reservation.id">
          <div  class="block hover:bg-gray-50">
            <div class="flex items-center px-4 py-4 sm:px-6">
              <div class="min-w-0 flex-1 flex items-center">
                <div class="flex-shrink-0">
                  <div class="h-12 w-12 rounded-full flex justify-center items-center" v-bind:class="{'bg-blue-600': reservation.status==='PENDING', 'bg-yellow-500': reservation.status==='ACTIVE', 'bg-green-400': reservation.status==='DONE'}">
                    <calendar-icon class="text-white"></calendar-icon>
                  </div>
                </div>
                <div class="min-w-0 flex-1 px-4 md:grid md:grid-cols-2 md:gap-4">
                  <div>
                    <p class="text-sm font-medium text-blue-600 truncate">Reservation id: {{ reservation.id }}</p>
                    <p class="mt-2 flex items-center text-sm text-gray-500">
                      <span class="truncate mr-2">Plate</span>
                      <span class="flex-shrink-0 inline-block px-2 py-0.5 text-green-800 text-xs font-medium bg-green-100 rounded-full">{{reservation.licensePlate}}</span>

                    </p>
                  </div>
                  <div class="md:block">
                    <div>
                      <p class="text-sm text-gray-900">
                        Parking Spot
                        <b>{{reservation.spotId}}</b>
                      </p>
                      <p class="mt-2 text-xs text-gray-500 leading-5">
                        Made reservation <strong>{{moment(reservation.createdAt).fromNow()}}</strong>
                      </p>
                    </div>
                  </div>
                </div>
              </div>
              <div>
                <button @click="deleteReservation(reservation)" class="text-red-400">
                  <trash-icon></trash-icon>
                </button>
              </div>
            </div>
          </div>
        </li>
        <li v-if="loadingReservations">
          <div class="block">
            <div class="flex animate-pulse  items-center px-4 py-4 sm:px-6">
              <div class="min-w-0 flex-1 flex items-center">
                <div class="flex-shrink-0">
                  <div class="h-12 w-12 rounded-full flex justify-center items-center bg-gray-200">
                  </div>
                </div>
                <div class="min-w-0 flex-1 px-4 md:grid md:grid-cols-2 md:gap-4">
                  <div>
                    <p class="h-4 bg-blue-400 rounded w-1/2"></p>
                    <div class="h-4 bg-gray-200 rounded w-1/4 mt-2"></div>
                  </div>
                  <div class="md:block">
                    <div>
                      <div class="h-4 bg-gray-200 rounded w-5/6"></div>
                      <div class="h-4 bg-gray-200 rounded w-4/6 mt-2"></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </li>
        <li v-else-if="reservations.length===0">
          <a class="block hover:bg-gray-50">
            <div class="flex items-center px-4 py-4 sm:px-6">
              <div class="min-w-0 flex-1 flex items-center">
                <div class="flex-shrink-0">
                  <div class="h-12 w-12 rounded-full flex justify-center items-center bg-gray-100"></div>
                </div>
                <div class="min-w-0 flex-1 px-4 md:grid md:grid-cols-2 md:gap-4">
                  <div>
                    <p class="text-sm font-medium text-blue-600 truncate">No reservations found</p>
                    <p class="mt-2 flex items-center text-sm text-gray-400">
                      First make a reservation.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </a>
        </li>
      </ul>
    </div>
    <header class="py-10 pt-20">
      <div class="mx-auto">
        <h1 class="text-3xl font-bold text-blue-700">
          Parking lots
        </h1>
      </div>
    </header>
    <ul class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
      <li class="col-span-1 bg-white rounded-lg shadow divide-y divide-gray-200" v-for="spot in spots" :key="spot.id">
        <div class="w-full flex items-center justify-between p-6 space-x-6">
          <div class="flex-1 truncate">
            <div class="flex items-center space-x-3">
              <h3 class="text-gray-900 text-sm font-medium truncate">Spot {{ spot.id }} at {{spot.lotName}}</h3>
              <span class="flex-shrink-0 inline-block px-2 py-0.5 text-xs font-medium rounded-full" v-bind:class="{'bg-red-100 text-red-800': spot.state === 'Occupied', 'bg-yellow-100 text-yellow-800': spot.state === 'Reserved', 'bg-green-100 text-green-800': spot.state === 'Free'}">{{ spot.state }}</span>
            </div>
            <p class="mt-1 text-gray-500 text-sm truncate" v-if="spot.state === 'Occupied'">Occupied by {{spot.licensePlate}}</p>
            <p class="mt-1 text-gray-500 text-sm truncate" v-if="spot.state === 'Reserved'">Reserved by {{spot.licensePlate}}</p>
            <p class="mt-1 text-gray-500 text-sm truncate">Rate {{spot.rate | perHour}} per hour</p>

          </div>
          <div class="w-10 h-10 border border-gray-300 rounded-full flex-shrink-0 flex justify-center items-center">
            <location-marker-icon class="text-blue-700"></location-marker-icon>
          </div>
        </div>
        <div>
          <div class="-mt-px flex divide-x divide-gray-200" v-if="spot.state === 'Free'">
            <div class="w-0 flex-1 flex">
              <button @click = "updatingSpot = spot" class="relative -mr-px w-0 flex-1 inline-flex items-center justify-center py-4 text-sm text-gray-700 font-medium border border-transparent rounded-bl-lg hover:text-gray-500">
                <pencil-icon></pencil-icon>
                <span class="ml-3">Change rate</span>
              </button>
            </div>
            <div class="w-0 flex-1 flex">
              <router-link to="/make-reservation" href="#" class="relative w-0 flex-1 inline-flex items-center justify-center py-4 text-sm text-gray-700 font-medium border border-transparent rounded-br-lg hover:text-gray-500">
                <calendar-icon></calendar-icon>
                <span class="ml-3">Make reservation</span>
              </router-link>
            </div>
          </div>
        </div>
      </li>
    </ul>
    <div class="fixed z-10 inset-0 overflow-y-auto" v-if="updatingSpot">
      <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 transition-opacity" aria-hidden="true">
          <div class="absolute inset-0 bg-gray-500 opacity-75"></div>
        </div>
        <span class="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>
        <div class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full sm:p-6" role="dialog" aria-modal="true" aria-labelledby="modal-headline">
          <div>
            <div class="mt-3 text-center sm:mt-5">
              <h3 class="text-lg leading-6 font-medium text-gray-900" id="modal-headline">
               Update rate of spot {{updatingSpot.id}}
              </h3>
              <div class="mt-2 text-left">
                <label for="price" class="block text-sm font-medium text-gray-700">Price</label>
                <div class="mt-1 relative rounded-md shadow-sm">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <span class="text-gray-500 sm:text-sm">
                      €
                    </span>
                  </div>
                  <input type="number" id="price" class="focus:ring-indigo-500 focus:border-indigo-500 block w-full pl-7 pr-12 sm:text-sm border-gray-300 rounded-md" placeholder="0.00" v-model="updatingSpot.rate">
                  <div class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
                    <span class="text-gray-500 sm:text-sm" id="price-currency">
                      CENTS PER HOUR
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="mt-5 sm:mt-6 sm:grid sm:grid-cols-2 sm:gap-3 sm:grid-flow-row-dense">
            <button @click="updateRate(updatingSpot)" type="button" class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:col-start-2 sm:text-sm">
              Change
            </button>
            <button @click="updatingSpot = null" type="button" class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:col-start-1 sm:text-sm">
              Cancel
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {CalendarIcon, LocationMarkerIcon, TrashIcon, PencilIcon} from "@vue-hero-icons/outline"
import {mapActions, mapState} from "vuex";
import * as moment from "moment";
export default {
  name: "AdminHome",
  components: {
    CalendarIcon,
    LocationMarkerIcon,
    TrashIcon,
    PencilIcon
  },
  data() {
    return {
      moment: moment,
      updatingSpot: null
    }
  },
  computed: {
    ...mapState({
      loadingReservations: state => state.parking.loading.reservations,
      reservations: state => state.parking.reservations,
      loadingSpots: state => state.parking.loading.spots,
      spots: state => state.parking.spots
    })
  },
  methods: {
    ...mapActions('parking', [
        '_getReservations',
        '_getParkingSpots',
        '_deleteReservation',
        '_updateRate'
    ]),
    deleteReservation(reservation) {
      this._deleteReservation(reservation);
    },
    updateRate(spot) {
      this.updatingSpot = null
      this._updateRate(spot);
    }
  },
  mounted() {
    this._getParkingSpots();
    this._getReservations();
  },
  filters: {
    perHour(value){
      const formatter = new Intl.NumberFormat('nl-NL', {
        style: 'currency',
        currency: 'EUR',
        minimumFractionDigits: 2
      })
      return formatter.format(value*60/100);

    }
  }
}
</script>

<style scoped>

</style>
