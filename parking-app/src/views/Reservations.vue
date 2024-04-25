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
  </div>


</template>

<script>
import {mapActions, mapState} from "vuex";
import {CalendarIcon, TrashIcon} from "@vue-hero-icons/outline";
import * as moment from "moment";
export default {
  name: "Reservations",
  components: {
    CalendarIcon, TrashIcon
  },
  data() {
    return {
      moment: moment
    }
  },
  computed: {
    ...mapState({
        loadingReservations: state => state.parking.loading.reservations,
        reservations: state => state.parking.reservations
    })
  },
  methods: {
    ...mapActions('parking', [
        '_getReservations',
        '_deleteReservation'
    ]),
    getReservations(){
      this._getReservations();
    },
    deleteReservation(reservation) {
      this._deleteReservation(reservation);
    }
  },
  mounted() {
    this.getReservations();
  }
}
</script>

<style lang="scss">
  //datepicker
  $primary-color: #3B82F6;
  @import '~vue2-datepicker/scss/index.scss';
</style>
